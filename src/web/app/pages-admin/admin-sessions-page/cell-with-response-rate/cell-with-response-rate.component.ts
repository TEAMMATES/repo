import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { OngoingSession } from '../../../../types/api-output';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';

export interface OngoingSessionModel {
  ongoingSession: OngoingSession;
  startTimeString: string;
  endTimeString: string;
  responseRate?: string;
}
@Component({
    selector: 'tm-response-rate',
    templateUrl: './cell-with-response-rate.component.html',
    imports: [AjaxLoadingModule, CommonModule],
    standalone: true,
})
export class ResponseRateComponent {
    @Input() session!: OngoingSessionModel;
    empty: boolean = true;
    isLoading: boolean = false;
    @Input() getResponseRate!: () => Promise<void>;

    async callGetResponseRate(): Promise<void> {
      this.isLoading = true;
      await this.getResponseRate().then(() => {
        this.empty = false;
        this.isLoading = false;
      });
    }
}
