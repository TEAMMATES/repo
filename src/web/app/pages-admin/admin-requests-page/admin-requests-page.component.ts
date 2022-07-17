import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequest, AccountRequests, AccountRequestStatusUpdateResponse } from '../../../types/api-output';
import { AccountRequestUpdateRequest } from '../../../types/api-request';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { removeAnim } from '../../components/teammates-common/remove-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  EditedAccountRequestInfoModel,
  ProcessAccountRequestPanelStatus,
} from './process-account-request-panel/process-account-request-panel.component';

export interface AccountRequestTab {
  accountRequest: AccountRequest;
  isTabExpanded: boolean;
  panelStatus: ProcessAccountRequestPanelStatus;
  isSavingChanges: boolean;
  errorMessage: string;
}

/**
 * Admin requests page.
 */
@Component({
  selector: 'tm-admin-requests-page',
  templateUrl: './admin-requests-page.component.html',
  styleUrls: ['./admin-requests-page.component.scss'],
  animations: [collapseAnim, removeAnim],
})
export class AdminRequestsPageComponent implements OnInit {

  accountRequestPendingProcessingTabs: AccountRequestTab[] = [];
  hasAccountRequestsPendingProcessingLoadingFailed: boolean = false;
  isLoadingAccountRequestsPendingProcessing: boolean = false;

  constructor(private accountService: AccountService,
              private statusMessageService: StatusMessageService) {
  }

  ngOnInit(): void {
    this.loadAccountRequestsPendingProcessing();
  }

  /**
   * Loads account requests pending processing.
   */
  loadAccountRequestsPendingProcessing(): void {
    this.hasAccountRequestsPendingProcessingLoadingFailed = false;
    this.isLoadingAccountRequestsPendingProcessing = true;

    this.accountService.getAccountRequestsPendingProcessing()
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsPendingProcessing = false;
      }))
      .subscribe((resp: AccountRequests) => {
        resp.accountRequests.forEach((ar: AccountRequest) => {
          const accountRequestTab: AccountRequestTab = {
            accountRequest: ar,
            isTabExpanded: true,
            panelStatus: ProcessAccountRequestPanelStatus.SUBMITTED,
            isSavingChanges: false,
            errorMessage: '',
          };
          this.accountRequestPendingProcessingTabs.push(accountRequestTab);
        });
        // TODO: sort courses
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestPendingProcessingTabs = [];
        this.hasAccountRequestsPendingProcessingLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Sets the panel status to EDITING.
   */
  editAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.errorMessage = '';
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
  }

  /**
   * Sets the panel status to SUBMITTED.
   */
  cancelEditAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.errorMessage = '';
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Updates the account request in the tab.
   */
  saveAccountRequest(accountRequestTab: AccountRequestTab, editedInfo: EditedAccountRequestInfoModel): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    const updateRequest: AccountRequestUpdateRequest = {
      instructorName: editedInfo.editedName,
      instructorEmail: editedInfo.editedEmail,
      instructorInstitute: editedInfo.editedInstitute,
    };
    this.accountService.updateAccountRequest(accountRequest.email, accountRequest.institute, updateRequest, false)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
      }))
      .subscribe((resp: AccountRequest) => {
        accountRequestTab.accountRequest = resp;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully updated.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to update account request.');
      });
  }

  /**
   * Approves the account request in the tab.
   */
  approveAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.approveAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequestStatusUpdateResponse) => {
        accountRequestTab.accountRequest = resp.accountRequest;
        accountRequestTab.errorMessage = '';
        accountRequestTab.isTabExpanded = false;
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
        this.statusMessageService.showSuccessToast('Account request successfully approved.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to approve account request.');
      });
  }

  /**
   * Rejects the account request in the tab.
   */
  rejectAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.rejectAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequestStatusUpdateResponse) => {
        accountRequestTab.accountRequest = resp.accountRequest;
        accountRequestTab.errorMessage = '';
        accountRequestTab.isTabExpanded = false;
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
        this.statusMessageService.showSuccessToast('Account request successfully rejected.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reject account request.');
      });
  }

  /**
   * Deletes the account request in the tab.
   */
  deleteAccountRequest(accountRequestTab: AccountRequestTab, index: number): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.deleteAccountRequest(accountRequest.email, accountRequest.institute)
      .subscribe(() => {
        accountRequestTab.errorMessage = '';
        this.accountRequestPendingProcessingTabs.splice(index, 1);
        this.statusMessageService.showSuccessToast('Account request successfully deleted.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.isSavingChanges = false;
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to delete account request.');
      });
  }

  /**
   * Resets the account request in the tab.
   */
  resetAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.resetAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequestStatusUpdateResponse) => {
        accountRequestTab.accountRequest = resp.accountRequest;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully reset.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reset account request.');
      });
  }

  /**
   * Toggles the specific account request card.
   */
  toggleCard(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isTabExpanded = !accountRequestTab.isTabExpanded;
  }

}
