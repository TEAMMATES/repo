import {
  Component,
  DoCheck,
  EventEmitter,
  Input,
  IterableDiffer,
  IterableDiffers,
  OnInit,
  Output,
} from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { JoinState, MessageOutput } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { JoinStatePipe } from './join-state.pipe';
import {
  StudentListSectionData,
  StudentListStudentData,
} from './student-list-section-data';

/**
 * Flattened data which contains details about a student and their section.
 * The data is flattened to allow sorting of the table.
 */
interface FlatStudentListData {
  name: string;
  email: string;
  status: JoinState;
  team: string;
  photoUrl?: string;
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
}

/**
 * A table displaying a list of students from a course, with buttons to view/edit/delete students etc.
 */
@Component({
  selector: 'tm-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss'],
})
export class StudentListComponent implements OnInit, DoCheck {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() listOfStudentsToHide: string[] = [];
  @Input() isHideTableHead: boolean = false;
  @Input() enableRemindButton: boolean = false;

  // The input sections data from parent.
  @Input() sections: StudentListSectionData[] = [];

  @Output() removeStudentFromCourseEvent: EventEmitter<string> = new EventEmitter();

  // The flattened students list derived from the sections list.
  // The sections data is flattened to allow sorting of the list.
  students: FlatStudentListData[] = [];
  tableSortOrder: SortOrder = SortOrder.ASC;
  tableSortBy: SortBy = SortBy.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState =  JoinState;

  private sectionNameToStudentDataDiffer: Record<string, IterableDiffer<StudentListStudentData>> = {};
  private readonly sectionDataDiffer: IterableDiffer<StudentListSectionData>;

  constructor(private router: Router,
              private statusMessageService: StatusMessageService,
              private navigationService: NavigationService,
              private courseService: CourseService,
              private tableComparatorService: TableComparatorService,
              private ngbModal: NgbModal,
              private differs: IterableDiffers) {
    this.sectionDataDiffer = this.differs.find(this.sections).create();
  }

  ngOnInit(): void {
  }

  ngDoCheck(): void {
    // Check for changes in section list
    if (this.sectionDataDiffer) {
      const sectionChanges = this.sectionDataDiffer.diff(this.sections);
      if (sectionChanges) {
        sectionChanges.forEachAddedItem(addedItem => {
          this.sectionNameToStudentDataDiffer[addedItem.item.sectionName] =
              this.differs.find(addedItem.item.students).create();
        });
        sectionChanges.forEachRemovedItem(() => {
          this.students = this.mapStudentsFromSectionData(this.sections);
          return;
        });
      }
    }

    // Check for changes in student lists of each section
    for (const section of this.sections) {
      const studentDataDiffer = this.sectionNameToStudentDataDiffer[section.sectionName];
      if (!studentDataDiffer) {
        continue;
      }

      const studentChanges = studentDataDiffer.diff(section.students);
      if (!studentChanges) {
        continue;
      }

      this.students = this.mapStudentsFromSectionData(this.sections);
      break;
    }
  }

  /**
   * Flatten section data.
   */
  mapStudentsFromSectionData(sections: StudentListSectionData[]): FlatStudentListData[] {
    const students: FlatStudentListData[] = [];
    sections.forEach((section: StudentListSectionData) =>
        section.students.forEach((student: StudentListStudentData) =>
            students.push({
              name: student.name,
              email: student.email,
              status: student.status,
              team: student.team,
              photoUrl: student.photoUrl,
              sectionName: section.sectionName,
              isAllowedToModifyStudent: section.isAllowedToModifyStudent,
              isAllowedToViewStudentInSection: section.isAllowedToViewStudentInSection,
            }),
        ),
    );
    return students;
  }

  /**
   * Returns whether this course are divided into sections
   */
  hasSection(): boolean {
    return (this.students.some((student: FlatStudentListData) =>
        student.sectionName !== 'None'));
  }

  /**
   * Function to be passed to ngFor, so that students in the list is tracked by email
   */
  trackByFn(_index: number, item: FlatStudentListData): any {
    return item.email;
  }

  /**
   * Open the student delete confirmation modal.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

  /**
   * Remind the student from course.
   */
  remindStudentFromCourse(studentEmail: string): void {
    this.courseService.remindStudentForJoin(this.courseId, studentEmail)
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessage(this.router,
            `/web/instructor/courses/details?courseid=${this.courseId}`, resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentEmail: string): void {
    this.removeStudentFromCourseEvent.emit(studentEmail);
  }

  /**
   * Determines which row in the studentTable should be hidden.
   */
  isStudentToHide(studentEmail: string): boolean {
    return this.listOfStudentsToHide.indexOf(studentEmail) > -1;
  }

  /**
   * Sorts the student list
   */
  sortStudentListEvent(by: SortBy): void {
    this.tableSortBy = by;
    this.tableSortOrder =
        this.tableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.students.sort(this.sortBy(by));
  }

  /**
   * Returns a function to determine the order of sort
   */
  sortBy(by: SortBy):
      ((a: FlatStudentListData , b: FlatStudentListData) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();

    return (a: FlatStudentListData, b: FlatStudentListData): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.STUDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.team;
          strB = b.team;
          break;
        case SortBy.EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.JOIN_STATUS:
          strA = joinStatePipe.transform(a.status);
          strB = joinStatePipe.transform(b.status);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, this.tableSortOrder, strA, strB);
    };
  }
}
