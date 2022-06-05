import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import {
  FeedbackResponseRecipientSubmissionFormModel,
} from '../../question-submission-form/question-submission-form-model';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of contribution question.
 */
@Component({
  selector: 'tm-contribution-question-constraint',
  templateUrl: './contribution-question-constraint.component.html',
  styleUrls: ['./contribution-question-constraint.component.scss'],
})
export class ContributionQuestionConstraintComponent
    extends QuestionConstraintComponent<FeedbackContributionQuestionDetails> {

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

  /**
   * Checks if all forms are answered.
   */
  get isAllFormsAnswered(): boolean {
    return this.recipientSubmissionForms.every((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackContributionResponseDetails = form.responseDetails as FeedbackContributionResponseDetails;
      return details.answer !== CONTRIBUTION_POINT_NOT_SUBMITTED;
    });
  }

  /**
   * Checks if all forms are not answered.
   */
  get isAllFormsNotAnswered(): boolean {
    return this.recipientSubmissionForms.every((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackContributionResponseDetails = form.responseDetails as FeedbackContributionResponseDetails;
      return details.answer === CONTRIBUTION_POINT_NOT_SUBMITTED;
    });
  }

  /**
   * Gets total required shares.
   */
  get totalRequiredContributions(): number {
    return 100 * this.recipientSubmissionForms.length;
  }

  /**
   * Get answers for the recipients.
   */
  get allAnswers(): number[] {
    return this.recipientSubmissionForms.map(
        (form: FeedbackResponseRecipientSubmissionFormModel) => {
          const details: FeedbackContributionResponseDetails =
              form.responseDetails as FeedbackContributionResponseDetails;
          if (details.answer === CONTRIBUTION_POINT_NOT_SUBMITTED) {
            return 0;
          }
          return details.answer;
        });
  }

  /**
   * Gets total answer shares.
   */
  get totalAnsweredContributions(): number {
    return this.allAnswers.reduce((total: number, curr: number) => total + curr, 0);
  }

  /**
   * Checks if all shares have been distributed.
   */
  get isAllContributionsDistributed(): boolean {
    return this.totalAnsweredContributions === this.totalRequiredContributions;
  }

  /**
   * Checks if the shares have been insufficiently distributed.
   */
  get isInsufficientContributionsDistributed(): boolean {
    return this.totalAnsweredContributions < this.totalRequiredContributions;
  }

  /**
   * Checks if the shares have been over allocated.
   */
  get isContributionsOverAllocated(): boolean {
    return this.totalAnsweredContributions > this.totalRequiredContributions;
  }

  get isValid(): boolean {
    return !this.questionDetails.isZeroSum || this.isAllFormsNotAnswered
        || (this.isAllFormsAnswered && this.isAllContributionsDistributed);
  }

  toFloor(num: number): number {
    return Math.floor(num);
  }
}
