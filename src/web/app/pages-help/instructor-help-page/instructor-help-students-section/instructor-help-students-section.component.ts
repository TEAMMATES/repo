import { Component, ElementRef, Input, OnChanges, OnInit, QueryList, SimpleChanges, ViewChildren } from '@angular/core';

interface QuestionDetail {
  tag: number;
  keywords: String[];
}

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent implements OnInit, OnChanges, AfterViewInit {

  @Input() key: String;
  @ViewChildren('question') questionHTML: QueryList<ElementRef>;

  showQuestion: Boolean[];
  searchedTerms: number;
  questionDetails: QuestionDetail[];

  constructor() {
    this.key = '';
    this.showQuestion = [];
    this.searchedTerms = -1;
    this.questionDetails = [];
  }

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.key === '') {
      this.resetFaq();
    } else {
      this.filterFaq(changes.key.currentValue);
    }
  }

  /**
   * Retrieves HTML components from DOM after its rendered.
   */
  ngAfterViewInit(): void {
    if (this.questionDetails.length === 0) {
      this.generateTerms();

      const size: number = this.questionDetails.length;
      for (let i: number = 0; i < size; i += 1) {
        this.showQuestion.push(true);
      }
    }
  }

  private generateTerms(): void {
    this.questionHTML.forEach((question: ElementRef) => {
      const className: String = question.nativeElement.className;
      const text: String = question.nativeElement.textContent;

      const tag: number = Number(className.replace(/[^0-9]/g, ''));

        // filter small words away
      let keywords: String[] = text.split(' ').filter((word: String) => word.length > 3);

        // convert to lower case
      keywords = keywords.map((word: String) => word.toLowerCase());

        // remove punctuation
      keywords = keywords.map((word: String) =>
        word.replace(/\b[-.,()&$#!\[\]{}"']+\B|\B[-.,()&$#!\[\]{}"']+\b/g, ''));

      const newQuestion: QuestionDetail = {
        tag,
        keywords,
      };

      this.questionDetails.push(newQuestion);
    },
    );
  }

  private resetFaq(): void {
    for (let i: number = 0; i < this.questionDetails.length; i += 1) {
      this.showQuestion[i] = true;
    }
    this.searchedTerms = -1;
  }

  private filterFaq(searchTerm: String): void {
    this.searchedTerms = 0;
    for (const questionDetail of this.questionDetails) {

      const tag: number = questionDetail.tag;
      const terms: String[] = questionDetail.keywords;

      this.showQuestion[tag] = terms.includes(searchTerm);

      if (this.showQuestion[tag]) {
        this.searchedTerms += 1;
      }
    }
  }
}
