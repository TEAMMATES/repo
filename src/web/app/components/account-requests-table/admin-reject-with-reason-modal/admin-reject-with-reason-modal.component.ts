import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RejectWithReasonModalComponentResult } from './admin-reject-with-reason-modal-model';
import { environment } from '../../../../environments/environment';
import { StatusMessageService } from '../../../../services/status-message.service';

/**
 * Modal to select reject account requests with reason.
 */
@Component({
  selector: 'tm-reject-with-reason-modal',
  templateUrl: './admin-reject-with-reason-modal.component.html',
  styleUrls: ['./admin-reject-with-reason-modal.component.scss'],
})

export class RejectWithReasonModalComponent implements OnInit {

  @Input()
  accountRequestName: string = '';

  @Input()
  accountRequestEmail: string = '';

  rejectionReasonBody: string = '<p>Hi, {accountRequestName} </p>\n\n'
  + '<p>Thanks for your interest in using TEAMMATES. '
  + 'We are unable to create a TEAMMATES instructor account for you.</p>'
  + '<p><strong>Reason:</strong> The email address you provided is not an &#39;official&#39; '
  + 'email address provided by your institution.<br />'
  + '<strong>Remedy:</strong> Please re-submit an account request with your &#39;official&#39; '
  + 'institution email address.</p>\n\n'
  + '<p><strong>Reason:</strong> The email address you have provided seems like it belongs to a student '
  + '(i.e., not a staff member) of your institution.<br />'
  + '<strong>Remedy:</strong> If you are a student but you still need an instructor account, '
  + 'please send your justification to {supportEmail}</p>\n\n'
  + '<p><strong>Reason:</strong> You already have an account for this email address and this institution.<br />'
  + '<strong>Remedy:</strong> You can login to TEAMMATES using your Google account {existingEmail} </p>\n\n'
  + '<p>If you need further clarification or would like to appeal this decision, please '
  + 'feel free to contact us at {supportEmail}</p>'
  + '<p>Regards,<br />TEAMMATES Team.</p>';
  rejectionReasonTitle: string = 'We are Unable to Create an Account for you';

  constructor(public activeModal: NgbActiveModal, public statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    this.rejectionReasonBody = this.rejectionReasonBody.replace('{accountRequestName}', this.accountRequestName);
    this.rejectionReasonBody = this.rejectionReasonBody.replace('{existingEmail}', this.accountRequestEmail);
    this.rejectionReasonBody = this.rejectionReasonBody.replaceAll('{supportEmail}', environment.supportEmail);
  }

  onRejectionReasonBodyChange(updatedText: string): void {
    this.rejectionReasonBody = updatedText;
  }

  /**
   * Fires the reject event.
   */
  reject(): void {

    if (!this.rejectionReasonBody || this.rejectionReasonBody.length === 0) {
      this.statusMessageService.showErrorToast('Please provide an email body for the rejection email.');
      return;
    }

    if (!this.rejectionReasonTitle || this.rejectionReasonTitle.length === 0) {
      this.statusMessageService.showErrorToast('Please provide a title for the rejection email.');
      return;
    }

      const result: RejectWithReasonModalComponentResult = {
        rejectionReasonTitle: this.rejectionReasonTitle,
        rejectionReasonBody: this.rejectionReasonBody,
      };

      this.activeModal.close(result);
  }
}
