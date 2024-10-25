import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course } from '../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';

/**
 * Copy current session modal.
 */
@Component({
  selector: 'tm-copy-session-modal',
  templateUrl: './copy-session-modal.component.html',
  styleUrls: ['./copy-session-modal.component.scss'],
})
export class CopySessionModalComponent {

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  sessionToCopyCourseId: string = '';

  newFeedbackSessionName: string = '';
  copyToCourseSet: Set<string> = new Set<string>();

  constructor(public activeModal: NgbActiveModal, private statusMessageService: StatusMessageService) {}

  /**
   * Fires the copy event.
   */
  copy(): void {
    if (this.newFeedbackSessionName.trim().length === 0) {
      this.statusMessageService.showErrorToast('The field "Name for copied session" should not be whitespace.');
      return;
    }
        else if(this.newFeedbackSessionName.length > this.FEEDBACK_SESSION_NAME_MAX_LENGTH){
          this.statusMessageService.showErrorToast('The field "Name for copied session" should less than ${this.FEEDBACK_SESSION_NAME_MAX_LENGTH}.');
          return;
        }

    this.activeModal.close({
      newFeedbackSessionName: this.newFeedbackSessionName,
      sessionToCopyCourseId: this.sessionToCopyCourseId,
      copyToCourseList: Array.from(this.copyToCourseSet),
    });
  }

  /**
   * Toggles selection of course to copy to in set.
   */
  select(courseId: string): void {
    if (this.copyToCourseSet.has(courseId)) {
      this.copyToCourseSet.delete(courseId);
    } else {
      this.copyToCourseSet.add(courseId);
    }
  }
}
