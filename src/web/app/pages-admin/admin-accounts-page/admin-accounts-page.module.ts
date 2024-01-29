import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

const routes: Routes = [
  {
    path: '',
    component: AdminAccountsPageComponent,
  },
];

/**
 * Module for admin accounts page.
 */
@NgModule({
  declarations: [
    AdminAccountsPageComponent,
  ],
  exports: [
    AdminAccountsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class AdminAccountsPageModule { }
