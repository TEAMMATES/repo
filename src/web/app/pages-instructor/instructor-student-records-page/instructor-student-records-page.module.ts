import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import {
    GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { StudentProfileModule } from '../student-profile/student-profile.module';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorStudentRecordsPageComponent,
  },
];

/**
 * Module for instructor student records page.
 */
@NgModule({
  declarations: [
    InstructorStudentRecordsPageComponent,
  ],
  exports: [
    InstructorStudentRecordsPageComponent,
  ],
  imports: [
    CommonModule,
    StudentProfileModule,
    NgbModule,
    GrqRgqViewResponsesModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
})
export class InstructorStudentRecordsPageModule { }
