import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { McqFieldComponent } from './mcq-field/mcq-field.component';
import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';
import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';
import { WeightFieldComponent } from './weight-field/weight-field.component';

/**
 * Module for all different types of question edit details forms.
 */
@NgModule({
  declarations: [
    ContributionQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
    McqQuestionEditDetailsFormComponent,
    McqFieldComponent,
    WeightFieldComponent,
  ],
  exports: [
    ContributionQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
    McqQuestionEditDetailsFormComponent,
    McqFieldComponent,
    WeightFieldComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
})
export class QuestionEditDetailsFormModule { }
