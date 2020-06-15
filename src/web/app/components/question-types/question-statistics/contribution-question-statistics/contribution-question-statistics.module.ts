import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';

/**
 * Module for contribution question statistics
 */
@NgModule({
  declarations: [ContributionComponent, ContributionRatingsListComponent],
  imports: [
    NgbModule, CommonModule,
  ],
  exports: [
    ContributionComponent, ContributionRatingsListComponent,
  ],
})
export class ContributionQuestionStatisticsModule { }
