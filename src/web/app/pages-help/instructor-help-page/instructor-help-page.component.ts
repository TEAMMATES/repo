import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Sections } from './sections';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
})
export class InstructorHelpPageComponent implements OnInit {
  // enum
  Sections: typeof Sections = Sections;
  readonly supportEmail: string = environment.supportEmail;
  searchTerm: String = '';
  key: String = '';

  @ViewChild('helpPage') bodyRef ?: ElementRef;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.fragment.subscribe((f: string) => {
      setTimeout((): void => {
        this.scroll(f);
      }, 10);
    });
  }

  /**
   * Filters the help contents and displays only those that matches the filter.
   */
  search(): void {
    if (this.searchTerm !== '') {
      this.key = this.searchTerm.toLowerCase();
    } else {
      this.clear();
    }
  }

  /**
   * Scrolls to the section passed in
   */
  scroll(section: string): void {
    if (this.bodyRef) {
      const el: any = Array.prototype.slice
          .call(this.bodyRef.nativeElement.childNodes).find((x: any) => x.id === section);
      if (el) {
        el.scrollIntoView();
        window.scrollBy(0, -50);
      }
    }
  }

  /**
   * Clears the filter used for search.
   */
  clear(): void {
    this.searchTerm = '';
    this.key = '';
  }
}
