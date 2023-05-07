import { Component, EventEmitter, Input, Output,  OnInit, OnChanges } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  StudentExtensionTableColumnModel,
  InstructorExtensionTableColumnModel,
} from '../../pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { TimezoneService } from '../../../services/timezone.service';


export enum ExtensionModalType {
  EXTEND,
  DELETE,
  SESSION_DELETE,
}

@Component({
  selector: 'tm-extension-confirm-modal',
  templateUrl: './extension-confirm-modal.component.html',
  styleUrls: ['./extension-confirm-modal.component.scss'],
})
export class ExtensionConfirmModalComponent implements OnInit, OnChanges {
  @Input()
  modalType: ExtensionModalType = ExtensionModalType.EXTEND;

  @Input()
  selectedStudents: StudentExtensionTableColumnModel[] = [];

  @Input()
  selectedInstructors: InstructorExtensionTableColumnModel[] = [];

  @Input()
  extensionTimestamp: number = 0;

  @Input()
  feedbackSessionTimeZone: string = '';

  @Output()
  confirmExtensionCallbackEvent: EventEmitter<boolean> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal, 
              private tableComparatorService: TableComparatorService,
              private timezoneService: TimezoneService) {}

  studentsColumnsData: ColumnData[] = [];
  studentsRowsData: SortableTableCellData[][] = [];

  instructorsColumnsData: ColumnData[] = [];
  instructorsRowsData: SortableTableCellData[][] = [];

  ngOnInit(): void {
    this.getTableData();
  }

  ngOnChanges(): void {
    this.getTableData();
  }

  private formatTimestamp(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM YYYY h:mm A');
  }

  private getTableData(): void {
    var deadlineType = '';
    if (this.isExtendModal()) var deadlineType = 'Original Deadline';
    if (this.isDeleteModal() || this.isSessionDeleteModal()) var deadlineType = 'Current Deadline';

    this.studentsColumnsData = [
      { header: 'Section', sortBy: SortBy.SECTION_NAME },
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Name', sortBy: SortBy.RESPONDENT_NAME },
      { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL},
      { header: deadlineType, sortBy: SortBy.SESSION_END_DATE },
    ]

    this.instructorsColumnsData = [
      { header: 'Name', sortBy: SortBy.RESPONDENT_NAME },
      { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
      { header: 'Role', sortBy: SortBy.INSTRUCTOR_PERMISSION_ROLE },
      { header: deadlineType, sortBy: SortBy.SESSION_END_DATE },
    ]

    for (const student of this.selectedStudents) {
      this.studentsRowsData.push([
        { value: student.sectionName },
        { value: student.teamName } ,
        { value: student.name },
        { value: student.email },
        { value: this.formatTimestamp(student.extensionDeadline, 'America/New_York') },
      ])

    }

    for (const instructor of this.selectedInstructors) {  
      this.instructorsRowsData.push([
        { value: instructor.name },
        { value: instructor.email } ,
        { value: instructor.role },
        { value: this.formatTimestamp(instructor.extensionDeadline, 'America/New_York') },
      ])
    }
  }

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SESSION_END_DATE;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SESSION_END_DATE;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  isSubmitting: boolean = false;
  isNotifyDeadlines: boolean = false;

  onConfirm(): void {
    this.isSubmitting = true;
    this.confirmExtensionCallbackEvent.emit(this.isNotifyDeadlines);
  }

  isDeleteModal(): boolean {
    return this.modalType === ExtensionModalType.DELETE;
  }

  isExtendModal(): boolean {
    return this.modalType === ExtensionModalType.EXTEND;
  }

  isSessionDeleteModal(): boolean {
    return this.modalType === ExtensionModalType.SESSION_DELETE;
  }

  sortStudentColumnsBy(by: SortBy): void {
    this.sortStudentsBy = by;
    this.sortStudentOrder = this.sortStudentOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.selectedStudents.sort(this.sortStudentPanelsBy(by));
  }

  getAriaSortStudent(by: SortBy): String {
    if (by !== this.sortStudentsBy) {
      return 'none';
    }
    return this.sortStudentOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  sortStudentPanelsBy(
    by: SortBy,
  ): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel) => number {
    return (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortStudentOrder, strA, strB);
    };
  }

  sortInstructorsColumnsBy(by: SortBy): void {
    this.sortInstructorsBy = by;
    this.sortInstructorOrder = this.sortInstructorOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.selectedInstructors.sort(this.sortInstructorPanelsBy(by));
  }

  getAriaSortInstructor(by: SortBy): String {
    if (by !== this.sortInstructorsBy) {
      return 'none';
    }
    return this.sortInstructorOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  sortInstructorPanelsBy(
    by: SortBy,
  ): (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel) => number {
    return (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.INSTRUCTOR_PERMISSION_ROLE:
          strA = a.role || '';
          strB = b.role || '';
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortInstructorOrder, strA, strB);
    };
  }
}
