import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSessionsPageComponent } from './admin-sessions-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminSessionsPageComponent,
  },
];

/**
 * Module for admin sessions page.
 */
@NgModule({
  declarations: [
    AdminSessionsPageComponent,
  ],
  exports: [
    AdminSessionsPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbDatepickerModule,
    NgbTimepickerModule,
    RouterModule.forChild(routes),
  ],
})
export class AdminSessionsPageModule { }
