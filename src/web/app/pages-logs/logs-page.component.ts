import { Component, OnInit } from '@angular/core';
import { EMPTY } from 'rxjs';
import { expand, finalize, reduce } from 'rxjs/operators';
import { AdvancedFilters, LogsEndpointQueryParams, LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
import { ApiConst } from '../../types/api-const';
import { ActionClasses, GeneralLogEntry, GeneralLogs, SourceLocation } from '../../types/api-output';
import { DateFormat } from '../components/datepicker/datepicker.component';
import { LogsHistogramDataModel } from '../components/logs-histogram/logs-histogram-model';
import { LogsTableRowModel } from '../components/logs-table/logs-table-model';
import { collapseAnim } from '../components/teammates-common/collapse-anim';
import { TimeFormat } from '../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsSeverity: string;
  logsMinSeverity: string;
  logsEvent: string;
  logsFilter: string;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
  advancedFilters: AdvancedFilters;
}

/**
 * Admin and maintainer logs page.
 */
@Component({
  selector: 'tm-logs-page',
  templateUrl: './logs-page.component.html',
  styleUrls: ['./logs-page.component.scss'],
  animations: [collapseAnim],
})
export class LogsPageComponent implements OnInit {
  readonly LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  readonly LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  readonly SEVERITIES: string[] = ['INFO', 'WARNING', 'ERROR'];
  readonly EVENTS: string[] = ['REQUEST_RECEIVED', 'RESPONSE_DISPATCHED', 'EMAIL_SENT', 'FEEDBACK_SESSION_AUDIT'];
  readonly SEVERITY: string = 'severity';
  readonly MIN_SEVERITY: string = 'minSeverity';
  readonly EVENT: string = 'event';
  readonly MAXIMUM_PAGES_FOR_ERROR_LOGS: number = 20;
  ACTION_CLASSES: string[] = [];

  formModel: SearchLogsFormModel = {
    logsSeverity: '',
    logsMinSeverity: '',
    logsEvent: '',
    logsFilter: '',
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
    advancedFilters: {},
  };
  queryParams: LogsEndpointQueryParams = { searchFrom: '', searchUntil: '', advancedFilters: {} };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  searchResults: LogsTableRowModel[] = [];
  histogramResult: LogsHistogramDataModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;
  isTableView: boolean = true;
  nextPageToken: string = '';
  isFiltersExpanded: boolean = false;

  constructor(private logService: LogService,
    private timezoneService: TimezoneService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.isLoading = true;
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    const fromDate: Date = new Date();
    fromDate.setDate(today.getDate() - 1);

    this.formModel.logsDateFrom = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.logsDateTo = { ...this.dateToday };
    this.formModel.logsTimeFrom = { hour: 23, minute: 59 };
    this.formModel.logsTimeTo = { hour: 23, minute: 59 };

    this.logService.getActionClassList()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe((actionClasses: ActionClasses) => this.ACTION_CLASSES = actionClasses.actionClasses.sort());
  }

  searchForLogs(): void {
    if (this.isTableView && !this.isFormValid()) {
      return;
    }

    this.hasResult = false;
    this.isSearching = true;
    this.histogramResult = [];
    this.searchResults = [];
    this.nextPageToken = '';
    this.isFiltersExpanded = false;
    const timestampFrom: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateFrom, this.formModel.logsTimeFrom);
    const timestampUntil: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateTo, this.formModel.logsTimeTo);

    if (this.isTableView) {
      this.searchForLogsTableView(timestampFrom, timestampUntil);
    } else {
      this.searchForLogsHistogramView(timestampFrom, timestampUntil);
    }
  }

  private searchForLogsTableView(timestampFrom: number, timestampUntil: number): void {
    this.setQueryParams(timestampFrom, timestampUntil);
    this.logService.searchLogs(this.queryParams)
      .pipe(
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }))
      .subscribe((generalLogs: GeneralLogs) => this.processLogsForTableView(generalLogs),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private isFormValid(): boolean {
    if (this.formModel.logsFilter === '') {
      this.statusMessageService.showErrorToast('Please choose to filter by severity / minimum severity / event');
      return false;
    }
    if (this.formModel.logsFilter === this.SEVERITY && this.formModel.logsSeverity === '') {
      this.statusMessageService.showErrorToast('Please choose a severity level');
      return false;
    }
    if (this.formModel.logsFilter === this.MIN_SEVERITY && this.formModel.logsMinSeverity === '') {
      this.statusMessageService.showErrorToast('Please choose a minimum severity level');
      return false;
    }
    if (this.formModel.logsFilter === this.EVENT && this.formModel.logsEvent === '') {
      this.statusMessageService.showErrorToast('Please choose an event type');
      return false;
    }
    if (!this.formModel.advancedFilters.sourceLocationFile && this.formModel.advancedFilters.sourceLocationFunction) {
      this.isFiltersExpanded = true;
      this.statusMessageService.showErrorToast('Please fill in Source location file or clear Source location function');
      return false;
    }

    return true;
  }

  /**
   * Sets the query parameters with the given timestamps and filters in form model.
   */
  private setQueryParams(timestampFrom: number, timestampUntil: number): void {
    this.queryParams = {
      searchFrom: timestampFrom.toString(),
      searchUntil: timestampUntil.toString(),
      advancedFilters: JSON.parse(JSON.stringify(this.formModel.advancedFilters)),
    };

    if (this.formModel.logsFilter === this.SEVERITY) {
      this.queryParams.severity = this.formModel.logsSeverity;
    }

    if (this.formModel.logsFilter === this.MIN_SEVERITY) {
      this.queryParams.minSeverity = this.formModel.logsMinSeverity;
    }

    if (this.formModel.logsFilter === this.EVENT) {
      this.queryParams.logEvent = this.formModel.logsEvent;
    }
  }

  private searchForLogsHistogramView(timestampFrom: number, timestampUntil: number): void {
    let numberOfPagesRetrieved: number = 0;
    this.queryParams = {
      searchFrom: timestampFrom.toString(),
      searchUntil: timestampUntil.toString(),
      severity: 'ERROR',
      advancedFilters: {},
    };
    this.logService.searchLogs(this.queryParams)
      .pipe(
        expand((logs: GeneralLogs) => {
          if (logs.nextPageToken !== undefined && numberOfPagesRetrieved < this.MAXIMUM_PAGES_FOR_ERROR_LOGS) {
            numberOfPagesRetrieved += 1;
            this.queryParams.nextPageToken = logs.nextPageToken;
            return this.logService.searchLogs(this.queryParams);
          }

          return EMPTY;
        }),
        reduce((acc: GeneralLogEntry[], res: GeneralLogs) => acc.concat(res.logEntries), [] as GeneralLogEntry[]),
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }),
      )
      .subscribe((logResults: GeneralLogEntry[]) => this.processLogsForHistogram(logResults),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private processLogsForTableView(generalLogs: GeneralLogs): void {
    this.nextPageToken = generalLogs.nextPageToken || '';
    generalLogs.logEntries.forEach((log: GeneralLogEntry) => this.searchResults.push(this.toLogModel(log)));
  }

  private processLogsForHistogram(logs: GeneralLogEntry[]): void {
    const sourceToFrequencyMap: Map<string, number> = logs.reduce((acc: Map<string, number>, log: GeneralLogEntry) =>
      acc.set(JSON.stringify(log.sourceLocation), (acc.get(JSON.stringify(log.sourceLocation)) || 0) + 1),
      new Map<string, number>());
    sourceToFrequencyMap.forEach((value: number, key: string) => {
      this.histogramResult.push({ sourceLocation: JSON.parse(key), numberOfTimes: value });
    });
  }

  toLogModel(log: GeneralLogEntry): LogsTableRowModel {
    let summary: string = '';
    let payload: any = '';
    let httpStatus: number | undefined;
    let responseTime: number | undefined;
    let traceIdForSummary: string | undefined;
    let userInfo: any;

    if (log.trace) {
      traceIdForSummary = this.formatTraceForSummary(log.trace);
    }

    if (log.message) {
      summary = log.message;
      payload = this.formatTextPayloadForDisplay(log.message);
    } else if (log.details) {
      payload = log.details;
      if (payload.requestMethod) {
        summary += `${payload.requestMethod} `;
      }
      if (payload.requestUrl) {
        summary += `${payload.requestUrl} `;
      }
      if (payload.responseStatus) {
        httpStatus = payload.responseStatus;
      }
      if (payload.responseTime) {
        responseTime = payload.responseTime;
      }
      if (payload.actionClass) {
        summary += `${payload.actionClass}`;
      }
      if (payload.userInfo) {
        userInfo = payload.userInfo;
        payload.userInfo = undefined; // Removed so that userInfo is not displayed twice
      }
    }

    return {
      summary,
      httpStatus,
      responseTime,
      userInfo,
      traceIdForSummary,
      traceId: log.trace,
      sourceLocation: log.sourceLocation,
      timestamp: this.timezoneService.formatToString(log.timestamp, this.timezoneService.guessTimezone(), 'DD MMM, YYYY hh:mm:ss A'),
      severity: log.severity,
      details: payload,
      isDetailsExpanded: false,
    };
  }

  private formatTextPayloadForDisplay(textPayload: String): String {
    return textPayload
      .replace(/\n/g, '<br/>')
      .replace(/\t/g, '&#9;');
  }

  /**
   * Display the first 9 digits of the trace.
   */
  private formatTraceForSummary(trace: string): string | undefined {
    return trace.split('/').pop()?.slice(0, 9);
  }

  getNextPageLogs(): void {
    this.isSearching = true;
    this.queryParams.nextPageToken = this.nextPageToken;
    this.logService.searchLogs(this.queryParams)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe((generalLogs: GeneralLogs) => this.processLogsForTableView(generalLogs),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  addTraceToFilter(trace: string): void {
    this.isFiltersExpanded = true;
    this.formModel.advancedFilters.traceId = trace;
    this.statusMessageService.showSuccessToast('Trace ID added to filters');
  }

  addSourceLocationToFilter(sourceLocation: SourceLocation): void {
    this.isFiltersExpanded = true;
    this.formModel.advancedFilters.sourceLocationFile = sourceLocation.file;
    this.formModel.advancedFilters.sourceLocationFunction = sourceLocation.function;
    this.statusMessageService.showSuccessToast('Source location added to filters');
  }

  addUserInfoToFilter(userInfo: any): void {
    this.isFiltersExpanded = true;
    if (userInfo.googleId) {
      this.formModel.advancedFilters.googleId = userInfo.googleId;
    } else if (userInfo.regkey) {
      this.formModel.advancedFilters.regkey = userInfo.regkey;
    } else if (userInfo.email) {
      this.formModel.advancedFilters.email = userInfo.email;
    }

    this.statusMessageService.showSuccessToast('User info added to filters');
  }

  clearFilters(): void {
    this.formModel.advancedFilters.traceId = '';
    this.formModel.advancedFilters.googleId = '';
    this.formModel.advancedFilters.regkey = '';
    this.formModel.advancedFilters.email = '';
    this.formModel.advancedFilters.actionClass = '';
    this.formModel.advancedFilters.sourceLocationFile = '';
    this.formModel.advancedFilters.sourceLocationFunction = '';
    this.formModel.advancedFilters.exceptionClass = '';
  }

  switchView(): void {
    this.isTableView = !this.isTableView;
    this.searchResults = [];
    this.histogramResult = [];
  }
}
