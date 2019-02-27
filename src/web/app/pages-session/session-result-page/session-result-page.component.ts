import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';
import { FeedbackSession } from '../../../types/api-output';
import moment from 'moment-timezone';

/**
 * Feedback session result page.
 */
@Component({
  selector: 'tm-session-result-page',
  templateUrl: './session-result-page.component.html',
  styleUrls: ['./session-result-page.component.scss'],
})
export class SessionResultPageComponent implements OnInit {

  session: any = {};
  questions: any[] = [];
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';

  constructor(private httpRequestService: HttpRequestService, private route: ActivatedRoute,
      private timezoneService: TimezoneService) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const { courseid, fsname } = queryParams;
      const paramMap: { [key: string]: string } = {
        courseid,
        fsname,
        intent: Intent.STUDENT_RESULT,
      };
      this.httpRequestService.get('/session', paramMap).subscribe((resp: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = resp;
        this.formattedSessionOpeningTime =
            moment(this.session.submissionStartTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.formattedSessionClosingTime =
              moment(this.session.submissionEndTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.httpRequestService.get('/result', paramMap).subscribe((resp2: any) => {
          this.questions = resp2.questions.sort((a: any, b: any) => a.questionNumber - b.questionNumber);
        }, (resp2: ErrorMessageOutput) => {
        });
      }, (resp: ErrorMessageOutput) => {
      });
    });
  }

}
