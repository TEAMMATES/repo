import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { QuestionEditFormModule } from '../../components/question-edit-form/question-edit-form.module';
import { StudentProfileModule } from '../../pages-instructor/student-profile/student-profile.module';

import { InstructorHelpPageComponent } from './instructor-help-page.component';

import {
  CommentBoxModule,
} from '../../components/comment-box/comment-box.module';
import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import { StudentViewResponsesModule,
} from '../../components/question-responses/student-view-responses/student-view-responses.module';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import {
  SessionEditFormModule,
} from '../../components/session-edit-form/session-edit-form.module';
import {
  SessionsRecycleBinTableModule,
} from '../../components/sessions-recycle-bin-table/sessions-recycle-bin-table.module';
import { ExampleBoxComponent } from './example-box/example-box.component';
import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpQuestionsSectionComponent,
} from './instructor-help-questions-section/instructor-help-questions-section.component';
import {
  InstructorHelpSessionsSectionComponent,
} from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import {
  InstructorHelpStudentsSectionComponent,
} from './instructor-help-students-section/instructor-help-students-section.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorHelpPageComponent,
  },
];

/**
 * Module for instructor help page.
 */
@NgModule({
  imports: [
    CommentBoxModule,
    CommonModule,
    FormsModule,
    NgbModule,
    RouterModule.forChild(routes),
    StudentProfileModule,
    QuestionEditFormModule,
    ReactiveFormsModule,
    SessionEditFormModule,
    QuestionEditFormModule,
    QuestionSubmissionFormModule,
    SessionsRecycleBinTableModule,
    QuestionTextWithInfoModule,
    SingleStatisticsModule,
    StudentViewResponsesModule,
  ],
  declarations: [
    InstructorHelpPageComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpSessionsSectionComponent,
    InstructorHelpQuestionsSectionComponent,
    InstructorHelpCoursesSectionComponent,
    ExampleBoxComponent,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
