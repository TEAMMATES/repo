import { Directive, Input, OnInit } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract question response.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export abstract class QuestionResponse<R extends FeedbackResponseDetails, Q extends FeedbackQuestionDetails>
    implements OnInit {

  @Input() responseDetails: R;
  @Input() questionDetails: Q;
  @Input() isStudentPage: boolean = false;

  protected constructor(responseDetails: R, questionDetails: Q) {
    this.responseDetails = responseDetails;
    this.questionDetails = questionDetails;
  }

  ngOnInit(): void {
  }

}
