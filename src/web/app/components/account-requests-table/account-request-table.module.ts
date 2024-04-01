import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestTableComponent } from './account-request-table.component';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import { Pipes } from '../../pipes/pipes.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';

/**
 * Module for account requests table.
 */
@NgModule({
  declarations: [
    AccountRequestTableComponent,
    EditRequestModalComponent,
  ],
  exports: [
    AccountRequestTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    NgbDropdownModule,
    Pipes,
    RichTextEditorModule,
  ],
})
export class AccountRequestTableModule { }
