import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
} from '../../../../types/feedback-response-details';
import { QuestionResponse } from './question-response';

/**
 * Contribution question response.
 */
@Component({
  selector: 'tm-contribution-question-response',
  templateUrl: './contribution-question-response.component.html',
  styleUrls: ['./contribution-question-response.component.scss'],
})
export class ContributionQuestionResponseComponent
    extends QuestionResponse<FeedbackContributionResponseDetails, FeedbackContributionQuestionDetails> {

  constructor() {
    super({
      answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
      questionType: FeedbackQuestionType.CONTRIB,
    }, {
      isNotSureAllowed: true,
      questionType: FeedbackQuestionType.CONTRIB,
      questionText: '',
    });
  }

}
