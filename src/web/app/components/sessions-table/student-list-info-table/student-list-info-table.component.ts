import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { StudentListInfoTableRowModel } from './student-list-info-table-model';

/**
 * Student list for users to make selection.
 */
@Component({
  selector: 'tm-student-list-info-table',
  templateUrl: './student-list-info-table.component.html',
  styleUrls: ['./student-list-info-table.component.scss'],
})
export class StudentListInfoTableComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  shouldDisplayHasSubmittedSessionColumn: boolean = false;

  @Input()
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];

  @Output()
  studentListInfoTableRowModelsChange: EventEmitter<StudentListInfoTableRowModel[]> = new EventEmitter();

  studentListInfoTableSortBy: SortBy = SortBy.NONE;
  studentListInfoTableSortOrder: SortOrder = SortOrder.ASC;

  constructor(private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
  }

  /**
   * Sorts the students according to selection option.
   */
  sortStudentsTableRows(by: SortBy): void {
    // reverse the sort order
    this.studentListInfoTableSortOrder =
        this.studentListInfoTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;

    this.studentListInfoTableRowModelsChange.emit(
        this.studentListInfoTableRowModels.map((oldModel: StudentListInfoTableRowModel) => Object.assign({}, oldModel))
            .sort(this.sortRowsBy(by, this.studentListInfoTableSortOrder)),
    );
  }

  /**
   * Handles the event when a row of the table is selected.
   */
  handleSelectionOfRow(model: StudentListInfoTableRowModel): void {
    this.studentListInfoTableRowModelsChange.emit(
        this.studentListInfoTableRowModels.map((oldModel: StudentListInfoTableRowModel) => {
          if (oldModel === model) {
            return Object.assign({}, oldModel, {
              isSelected: !oldModel.isSelected,
            });
          }
          return Object.assign({}, oldModel);
        }),
    );
  }

  /**
   * Sorts the rows of students in order.
   */
  sortRowsBy(by: SortBy, order: SortOrder):
      ((a: StudentListInfoTableRowModel, b: StudentListInfoTableRowModel) => number) {
    return ((a: StudentListInfoTableRowModel, b: StudentListInfoTableRowModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.teamName;
          strB = b.teamName;
          break;
        case SortBy.STUDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_COMPLETION_STATUS:
          strA = a.hasSubmittedSession.toString();
          strB = b.hasSubmittedSession.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Checks whether all students are selected.
   */
  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModelsChange.emit(
        this.studentListInfoTableRowModels.map((model: StudentListInfoTableRowModel) => Object.assign({}, model, {
          isSelected: shouldSelect,
        })),
    );
  }

}
