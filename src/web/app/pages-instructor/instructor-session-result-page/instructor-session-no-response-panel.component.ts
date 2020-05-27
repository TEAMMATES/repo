import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import {
  SendRemindersToStudentModalComponent,
} from '../../components/sessions-table/send-reminders-to-student-modal/send-reminders-to-student-modal.component';
import {
  StudentListInfoTableRowModel,
} from '../../components/sessions-table/student-list-info-table/student-list-info-table-model';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {

  @Input() allStudents: Student[] = [];
  @Input() noResponseStudents: Student[] = [];
  @Input() section: string = '';
  @Input() session: FeedbackSession = {
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  isTabExpanded: boolean = false;

  noResponseStudentsInSection: Student[] = [];

  @Output() studentsToRemindEvent: EventEmitter<StudentListInfoTableRowModel[]> = new EventEmitter();

  constructor(
    private modalService: NgbModal) { }

  ngOnInit(): void {
    this.filterStudentsBySection();
  }

  ngOnChanges(): void {
    this.filterStudentsBySection();
  }

  private filterStudentsBySection(): void {
    if (this.section) {
      this.noResponseStudentsInSection =
          this.noResponseStudents.filter((student: Student) => student.sectionName === this.section);
    } else {
      this.noResponseStudentsInSection = this.noResponseStudents;
    }
  }

  openSendReminderModal(event: any): void {
    event.stopPropagation();

    const courseId: string = this.session.courseId;
    const feedbackSessionName: string = this.session.feedbackSessionName;

    const nonResponseStudentEmails: string[] = this.noResponseStudents.map((student: Student) => student.email);
    const nonResponseStudentEmailSet: Set<string> = new Set(nonResponseStudentEmails);

    const modalRef: NgbModalRef = this.modalService.open(SendRemindersToStudentModalComponent);
    modalRef.componentInstance.courseId = courseId;
    modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
    modalRef.componentInstance.studentListInfoTableRowModels
      = this.allStudents.map((student: Student) => ({
        email: student.email,
        name: student.name,
        teamName: student.teamName,
        sectionName: student.sectionName,

        hasSubmittedSession: !nonResponseStudentEmailSet.has(student.email),
        isSelected: nonResponseStudentEmailSet.has(student.email),
      } as StudentListInfoTableRowModel));

    modalRef.result.then((studentsToRemind: StudentListInfoTableRowModel[]) => {
      this.studentsToRemindEvent.emit(studentsToRemind);
    }, () => {});
  }

  /**
   * Toggles the tab of the no response panel.
   */
  toggleTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

  /**
   * Expands the tab of the no response panel.
   */
  expandTab(): void {
    this.isTabExpanded = true;
  }

  /**
   * Collapses the tab of the no response panel.
   */
  collapseTab(): void {
    this.isTabExpanded = false;
  }

}
