import { Component, Input, OnInit } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackQuestionType, ResponseOutput } from '../../../../types/api-output';
import { CommentRowMode } from '../../comment-box/comment-row/comment-row.component';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  styleUrls: ['./student-view-responses.component.scss'],
})
export class StudentViewResponsesComponent implements OnInit {
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input() questionDetails: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };
  @Input() responses: ResponseOutput[] = [];
  @Input() isSelfResponses: boolean = false;

  recipient: string = '';

  constructor() { }

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }

}
