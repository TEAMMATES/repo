import { Component, Input, Output, EventEmitter } from '@angular/core';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import {
  FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_RUBRIC_QUESTION_DETAILS, DEFAULT_RUBRIC_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { RUBRIC_ANSWER_NOT_CHOSEN } from '../../../../types/feedback-response-details';

/**
 * The rubric question submission form for a recipient.
 */
@Component({
  selector: 'tm-rubric-question-edit-answer-form',
  templateUrl: './rubric-question-edit-answer-form.component.html',
  styleUrls: ['./rubric-question-edit-answer-form.component.scss'],
})
export class RubricQuestionEditAnswerFormComponent extends QuestionEditAnswerFormComponent
    <FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails> {

  /**
   * The unique ID in the page where the component is used.
   *
   * <p>This is to ensure that only one rubric answer per sub question can be selected.
   */
  @Input()
  id: string = '';

  // constant
  readonly RUBRIC_ANSWER_NOT_CHOSEN: number = RUBRIC_ANSWER_NOT_CHOSEN;

  @Output()
  resetWarningTriggered: EventEmitter<void> = new EventEmitter<void>();

  showResetWarning: boolean = false;

  constructor() {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS(), DEFAULT_RUBRIC_RESPONSE_DETAILS());
  }

  /**
   * Selects an answer.
   */
  selectAnswer(subQuestionIndex: number, answerIndex: number): void {
    if (this.isDisabled) {
      return;
    }

    let newAnswer: number[] = this.responseDetails.answer.slice();
    if (newAnswer.length !== this.questionDetails.rubricSubQuestions.length) {
      // initialize new answer on the fly
      newAnswer = Array(this.questionDetails.rubricSubQuestions.length).fill(RUBRIC_ANSWER_NOT_CHOSEN);
    }

    if (newAnswer[subQuestionIndex] === answerIndex) {
      // same answer is selected: toggle as unselected
      newAnswer[subQuestionIndex] = RUBRIC_ANSWER_NOT_CHOSEN;
    } else {
      newAnswer[subQuestionIndex] = answerIndex;
    }

    this.triggerResponseDetailsChange('answer', newAnswer);
  }

  getAriaLabelForChoice(choice: string, choiceDescription: string, criteria: string): string {
    const baseAriaLabel: string = this.getAriaLabel();
    const choiceWithDescription: string = this.getChoiceWithDescription(choice, choiceDescription);
    return `${choiceWithDescription} ${baseAriaLabel} under Criteria of ${criteria}`;
  }

  getChoiceWithDescription(choice: string, choiceDescription: string): string {
    return choiceDescription ? `${choice} - ${choiceDescription}` : choice;
  }

  getInputId(id: string, row: number, col: number, platform: string): string {
    return `${id}-row${row}-col${col}-${platform}`;
  }

  triggerResetWarning(): void {
    this.showResetWarning = true;
    this.resetWarningTriggered.emit();
  }

  confirmReset(confirm: boolean): void {
    if (confirm) {
      this.resetRubricAnswer();
    }
    this.showResetWarning = false;
  }

  resetRubricAnswer(): void {
    const resettedAnswer: number[] =
        Array(this.questionDetails.rubricSubQuestions.length).fill(RUBRIC_ANSWER_NOT_CHOSEN);
    this.triggerResponseDetailsChange('answer', resettedAnswer);
  }
}
