import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for Mcq question.
 */
@Component({
  selector: 'tm-mcq-question-edit-details-form',
  templateUrl: './mcq-question-edit-details-form.component.html',
  styleUrls: ['./mcq-question-edit-details-form.component.scss'],
})
export class McqQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackMcqQuestionDetails> {

  readonly PARTICIPANT_TYPES: string[] = [FeedbackParticipantType.STUDENTS,
    FeedbackParticipantType.STUDENTS_EXCLUDING_SELF, FeedbackParticipantType.TEAMS,
    FeedbackParticipantType.TEAMS_EXCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS];

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
  }

  /**
   * Reorders the list on dragging the Mcq options.
   */
  onMcqOptionDropped(event: CdkDragDrop<string[]>): void {
    const newWeights: number[] = this.model.mcqWeights.slice();
    const newOptions: string[] = this.model.mcqChoices.slice();
    moveItemInArray(newOptions, event.previousIndex, event.currentIndex);
    moveItemInArray(newWeights, event.previousIndex, event.currentIndex);
    const fieldsToUpdate: any = {};
    fieldsToUpdate.mcqChoices = newOptions;
    fieldsToUpdate.mcqWeights = newWeights;
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Increases number of Mcq options.
   */
  increaseNumberOfOptions(): void {
    const fieldsToUpdate: any = {};
    fieldsToUpdate.numOfMcqChoices = this.model.numOfMcqChoices + 1;
    const newOptions: string[] = this.model.mcqChoices.slice();
    newOptions.push('');
    fieldsToUpdate.mcqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.mcqWeights.slice();
      newWeights.push(0);
      fieldsToUpdate.mcqWeights = newWeights;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Deletes a Mcq option.
   */
  onMcqOptionDeleted(event: number): void {
    const fieldsToUpdate: any = {};
    fieldsToUpdate.numOfMcqChoices = this.model.numOfMcqChoices - 1;
    const newOptions: string[] = this.model.mcqChoices.slice();
    newOptions.splice(event, 1);
    fieldsToUpdate.mcqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.mcqWeights.slice();
      newWeights.splice(event, 1);
      fieldsToUpdate.mcqWeights = newWeights;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Displays new Mcq option at specified index.
   */
  onMcqOptionEntered(event: string, index: number): void {
    const newOptions: string[] = this.model.mcqChoices.slice();
    newOptions[index] = event;
    this.triggerModelChange('mcqChoices', newOptions);
  }

  /**
   * Displays new Mcq weight at specified index.
   */
  onMcqWeightEntered(event: number, index: number): void {
    const newWeights: number[] = this.model.mcqWeights.slice();
    newWeights[index] = event;
    this.triggerModelChange('mcqWeights', newWeights);
  }

  /**
   * Tracks the Mcq option by index.
   */
  trackMcqOption(index: number): string {
    return index.toString();
  }

  /**
   * Tracks the Mcq weight by index.
   */
  trackMcqWeight(index: number): string {
    return index.toString();
  }

  /**
   * Triggers the display of the weight column for the Mcq options if weights option is checked/unchecked.
   */
  triggerWeightsColumn(checked: boolean): void {
    const fieldsToUpdate: any = {};
    if (checked) {
      fieldsToUpdate.mcqWeights = Array(this.model.numOfMcqChoices).fill(0);
      fieldsToUpdate.mcqOtherWeight = 0;
      fieldsToUpdate.hasAssignedWeights = true;
    } else {
      fieldsToUpdate.mcqWeights = [];
      fieldsToUpdate.mcqOtherWeight = 0;
      fieldsToUpdate.hasAssignedWeights = false;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Triggers the setting of choosing other option.
   */
  triggerOtherEnabled(checked: boolean): void {
    if (checked) {
      this.triggerModelChangeBatch({
        otherEnabled: true,
        mcqOtherWeight: 0,
      });
    } else {
      this.triggerModelChangeBatch({
        otherEnabled: false,
        mcqOtherWeight: 0,
      });
    }
  }

  /**
   * Assigns a default value to generateOptionsFor when checkbox is clicked.
   */
  triggerGeneratedOptionsChange(checked: boolean): void {
    if (checked) {
      this.triggerModelChangeBatch({
        generateOptionsFor: FeedbackParticipantType.STUDENTS,
        numOfMcqChoices: 0,
        mcqChoices: [],
        otherEnabled: false,
        hasAssignedWeights: false,
        mcqWeights: [],
        mcqOtherWeight: 0,
      });
    } else {
      this.triggerModelChangeBatch({
        generateOptionsFor: FeedbackParticipantType.NONE,
        mcqChoices: ['', ''],
        numOfMcqChoices: 2,
        otherEnabled: false,
        hasAssignedWeights: false,
        mcqWeights: [],
        mcqOtherWeight: 0,
      });
    }
  }

  /**
   * Checks if the generatedOptionsFor checkbox is enabled.
   */
  get isGeneratedOptionsEnabled(): boolean {
    return this.model.generateOptionsFor !== FeedbackParticipantType.NONE;
  }
}
