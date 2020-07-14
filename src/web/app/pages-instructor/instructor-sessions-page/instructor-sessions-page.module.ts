import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RouterModule, Routes } from '@angular/router';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import {
  SessionsRecycleBinTableModule,
} from '../../components/sessions-recycle-bin-table/sessions-recycle-bin-table.module';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionsPageComponent,
  },
];

/**
 * Module for instructor sessions page.
 */
@NgModule({
  imports: [
    CommonModule,
    SessionEditFormModule,
    TeammatesCommonModule,
    FormsModule,
    SessionsTableModule,
    SessionsRecycleBinTableModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    InstructorSessionsPageComponent,
    CopyFromOtherSessionsModalComponent,
    SessionPermanentDeletionConfirmModalComponent,
    SessionsPermanentDeletionConfirmModalComponent,
  ],
  exports: [
    InstructorSessionsPageComponent,
  ],
  entryComponents: [
    CopyFromOtherSessionsModalComponent,
    SessionPermanentDeletionConfirmModalComponent,
    SessionsPermanentDeletionConfirmModalComponent,
  ],
})
export class InstructorSessionsPageModule { }
