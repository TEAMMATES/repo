import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for Rank Options question.
 */
@Component({
  selector: 'tm-rank-options-question-edit-details-form',
  templateUrl: './rank-options-question-edit-details-form.component.html',
  styleUrls: ['./rank-options-question-edit-details-form.component.scss'],
})
export class RankOptionsQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackRankOptionsQuestionDetails> {

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

  /**
   * Increases number of Rank options.
   */
  increaseNumberOfRankOptions(): void {
    const newOptions: string[] = this.model.options.slice();
    newOptions.push('');
    this.triggerModelChange('options', newOptions);
  }

  /**
   * Reorders the list on dragging the Rank options.
   */
  onRankOptionDropped(event: CdkDragDrop<string[]>): void {
    const newOptions: string[] = this.model.options.slice();
    moveItemInArray(newOptions, event.previousIndex, event.currentIndex);
    this.triggerModelChange('options', newOptions);
  }

  /**
   * Tracks the Rank option by index.
   */
  trackRankOption(index: number, item: string[]): string {
    return item[index];
  }

  /**
   * Deletes a Rank option.
   */
  onRankOptionDeleted(event: number): void {
    const newOptions: string[] = this.model.options.slice();
    newOptions.splice(event, 1);
    this.triggerModelChange('options', newOptions);
    if (this.model.maxOptionsToBeRanked > this.model.options.length) {
      this.triggerModelChange('maxOptionsToBeRanked', this.model.options.length);
    }
  }

  /**
   * Displays new Rank option at specified index.
   */
  onRankOptionEntered(event: string, index: number): void {
    const newOptions: string[] = this.model.options.slice();
    newOptions[index] = event;
    this.triggerModelChange('options', newOptions);
  }

  /**
   * Assigns a default value to minOptionsToBeRanked when checkbox is clicked.
   */
  triggerMinOptionsToBeRankedChange(event: any): void {
    const minOptionsToBeRanked: number = event.target.checked ? 1 : NO_VALUE;
    this.triggerModelChange('minOptionsToBeRanked', minOptionsToBeRanked);
  }

  /**
   * Assigns a default value to maxOptionsToBeRanked when checkbox is clicked.
   */
  triggerMaxOptionsToBeRankedChange(event: any): void {
    const maxOptionsToBeRanked: number = event.target.checked ? 1 : NO_VALUE;
    this.triggerModelChange('maxOptionsToBeRanked', maxOptionsToBeRanked);
  }

  /**
   * Checks if the minOptionsToBeRanked checkbox is enabled.
   */
  get isMinOptionsToBeRankedEnabled(): boolean {
    return this.model.minOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if the maxOptionsToBeRanked checkbox is enabled.
   */
  get isMaxOptionsToBeRankedEnabled(): boolean {
    return this.model.maxOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMinOptionsToBeRanked(): number {
    return this.model.minOptionsToBeRanked === NO_VALUE ? 1 : this.model.minOptionsToBeRanked;
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMaxOptionsToBeRanked(): number {
    return this.model.maxOptionsToBeRanked === NO_VALUE ? 1 : this.model.maxOptionsToBeRanked;
  }

  /**
   * Returns the maximum possible value for minOptionsToBeRanked.
   */
  get maxMinOptionsValue(): number {
    return this.model.maxOptionsToBeRanked === NO_VALUE ? this.model.options.length : this.model.maxOptionsToBeRanked;
  }

}
