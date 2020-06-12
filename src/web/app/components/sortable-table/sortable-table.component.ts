import { Component, Input, OnInit, Type } from '@angular/core';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';

/**
 * Column data for sortable table
 */
export interface ColumnData {
  header: string;
  headerToolTip?: string;
  sortBy?: SortBy; // optional if the column is not sortable
}

/**
 * Data provided for each table cell
 * Priority of display
 * 1. customComponent
 * 2. displayValue
 * 3. value
 */
export interface CustomTableCellData {
  value?: any; // Optional value used for sorting with sortBy provided in ColumnData
  displayValue?: string; // Raw string to be display in the cell
  customComponent?: {
    component: Type<any>;
    componentData: Record<string, any>; // @Input values for component
  };
}

/**
 * Displays a sortable table, sorting by clicking on the header
 * Optional sortBy option provided for each column
 * Columns and rows provided must be aligned
 */
@Component({
  selector: 'tm-sortable-table',
  templateUrl: './sortable-table.component.html',
  styleUrls: ['./sortable-table.component.scss'],
})
export class SortableTableComponent implements OnInit {

  // enum
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  columns: ColumnData[] = [];

  // Default to use supplied value for both sorting and displaying
  // Use CustomTableCellData if value used for sorting is different from displaying
  @Input()
  rows: CustomTableCellData[][] = [];

  columnToSortBy: string = '';
  sortOrder: SortOrder = SortOrder.ASC;
  tableRows: CustomTableCellData[][] = [];

  constructor(private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
  }

  ngOnChanges(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
    this.sortRows();
  }

  onClickHeader(columnHeader: string): void {
    this.sortOrder = (this.columnToSortBy === columnHeader) ?
        this.sortOrder === SortOrder.ASC ?
            SortOrder.DESC :
            SortOrder.ASC :
        SortOrder.ASC;
    this.columnToSortBy = columnHeader;
    this.sortRows();
  }

  sortRows(): void {
    if (!this.columnToSortBy) {
      return;
    }
    const columnIndex: number = this.columns.findIndex(
        (column: ColumnData) => column.header === this.columnToSortBy);
    if (columnIndex < 0) {
      return;
    }
    const sortBy: SortBy | undefined = this.columns[columnIndex].sortBy;
    if (!sortBy) {
      return;
    }

    this.tableRows.sort((row1: any[], row2: any[]) => {
      return this.tableComparatorService.compare(
          sortBy, this.sortOrder, String(row1[columnIndex].value), String(row2[columnIndex].value));
    });
  }

}
