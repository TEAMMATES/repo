import { Component, OnInit } from '@angular/core';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course, FeedbackSession,
  FeedbackSessionLog, FeedbackSessionLogEntry,
  FeedbackSessionLogs, FeedbackSessionLogType,
  Student,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { DatePickerFormatter } from '../../components/datepicker/datepicker-formatter';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { TimeFormat } from '../../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { ActivatedRoute } from "@angular/router";

/**
 * Model for searching of logs
 */
interface SearchLogsFormModel {
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
  logType: string;
  feedbackSessionName: string;
  studentEmail: string;
}

/**
 * Model for displaying of feedback session logs
 */
interface FeedbackSessionLogModel {
  feedbackSessionName: string;
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
  isTabExpanded: boolean;
}

/**
 * Component for instructor logs
 */
@Component({
  selector: 'tm-instructor-audit-logs-page',
  templateUrl: './instructor-student-activity-logs.component.html',
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  styleUrls: ['./instructor-student-activity-logs.component.scss'],
})
export class InstructorStudentActivityLogsComponent implements OnInit {
  LOGS_DATE_TIME_FORMAT: string = 'ddd, DD MMM YYYY hh:mm:ss A';
  LOGS_RETENTION_PERIOD: number = ApiConst.LOGS_RETENTION_PERIOD;
  LOG_TYPES = ["session access", "response submission", "result view"];

  // enum
  SortBy: typeof SortBy = SortBy;

  formModel: SearchLogsFormModel = {
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
    logType: '',
    studentEmail: '',
    feedbackSessionName: '',
  };
  course: Course = {
    courseId: '',
    courseName: '',
    institute: '',
    timeZone: '',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  students: Student[] = [];
  feedbackSessions: FeedbackSession[] = [];
  searchResults: FeedbackSessionLogModel[] = [];
  isLoading: boolean = true;
  isSearching: boolean = false;

  constructor(private route: ActivatedRoute,
              private courseService: CourseService,
              private studentService: StudentService,
              private logsService: LogService,
              private timezoneService: TimezoneService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadControlPanel();
      this.loadCourse(queryParams.courseid);
    });
  }

  /**
   * Loads the control panel based on the given course ID.
   */
  loadControlPanel(): void {
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD * 24 * 60 * 60 * 1000);
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
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    this.isSearching = true;
    this.searchResults = [];
    const timeZone: string = this.course.timeZone;
    const searchFrom: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateFrom, this.formModel.logsTimeFrom, timeZone, true);
    const searchUntil: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateTo, this.formModel.logsTimeTo, timeZone, true);

    this.logsService.searchFeedbackSessionLog({
      courseId: this.course.courseId,
      searchFrom: searchFrom.toString(),
      searchUntil: searchUntil.toString(),
      studentEmail: this.formModel.studentEmail,
    }).pipe(
        finalize(() => {
          this.isSearching = false;
        }),
    ).subscribe((logs: FeedbackSessionLogs) => {
      logs.feedbackSessionLogs.map((log: FeedbackSessionLog) =>
          this.searchResults.push(this.toFeedbackSessionLogModel(log)));
    }, (e: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(e.error.message);
    });
  }

  /**
   * Load the course based on the course id
   */
  private loadCourse(courseId: string): void {
    this.courseService
        .getCourseAsInstructor(courseId)
        .pipe(finalize(() => {
          this.isLoading = false;
        }))
        .subscribe((course: Course) => this.course = course,
            (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  /**
   * Load all students for the selected course
   */
  loadStudents(): void {
    if (this.students.length === 0) {
      this.isLoading = true;
      this.studentService.getStudentsFromCourse({ courseId: this.course.courseId })
          .pipe(finalize(() => { this.isLoading = false; }))
          .subscribe(({ students }: { students: Student[] }) => {
            const emptyStudent: Student = {
              courseId: '', email: '', name: '', sectionName: '', teamName: '',
            };
            students.sort((a: Student, b: Student): number => a.name.localeCompare(b.name));

            // Student with no name is selectable to search for all students since the field is optional
            this.students = [emptyStudent, ...students];
          });
    }
  }

  private toFeedbackSessionLogModel(log: FeedbackSessionLog): FeedbackSessionLogModel {
    return {
      isTabExpanded: log.feedbackSessionLogEntries.length === 0,
      feedbackSessionName: log.feedbackSessionData.feedbackSessionName,
      logColumnsData: [
        { header: 'Time', sortBy: SortBy.LOG_DATE },
        { header: 'Name', sortBy: SortBy.GIVER_NAME },
        { header: 'Activity', sortBy: SortBy.LOG_TYPE },
        { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
        { header: 'Section', sortBy: SortBy.SECTION_NAME },
        { header: 'Team', sortBy: SortBy.TEAM_NAME },
      ],
      logRowsData: log.feedbackSessionLogEntries
        .filter((entry: FeedbackSessionLogEntry) =>
            entry.feedbackSessionLogType.toString() as keyof typeof FeedbackSessionLogType
            !== 'VIEW_RESULT')
        .map((entry: FeedbackSessionLogEntry) => {
          return [
            {
              value: this.timezoneService.formatToString(
                  entry.timestamp, log.feedbackSessionData.timeZone,
                  this.LOGS_DATE_TIME_FORMAT),
              style: 'font-family:monospace;',
            },
            { value: entry.studentData.name },
            {
              value: entry.feedbackSessionLogType.toString() as keyof typeof FeedbackSessionLogType
                  === 'ACCESS' ? 'Viewed the submission page' : 'Submitted responses',
            },
            { value: entry.studentData.email },
            { value: entry.studentData.sectionName },
            { value: entry.studentData.teamName },
          ];
        }),
    };
  }
}
