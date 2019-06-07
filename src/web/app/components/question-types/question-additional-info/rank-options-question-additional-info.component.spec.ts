import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackQuestionType, FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { RankOptionsQuestionAdditionalInfoComponent } from './rank-options-question-additional-info.component';

describe('RankOptionsQuestionAdditionalInfoComponent', () => {
  let component: RankOptionsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<RankOptionsQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const rankOptionsQuestionDetails: FeedbackRankOptionsQuestionDetails = {
    minOptionsToBeRanked: Number.MIN_VALUE,
    maxOptionsToBeRanked: Number.MIN_VALUE,
    areDuplicatesAllowed: false,
    options: ['1', '2', '3'],
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionText: '',
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show options', () => {
    component.questionDetails = rankOptionsQuestionDetails;
    fixture.detectChanges();
    const options: HTMLElement[] = fixture.nativeElement.querySelectorAll('li');
    expect(options.length).toEqual(3);
    expect(options[0].textContent).toEqual('1');
    expect(options[1].textContent).toEqual('2');
    expect(options[2].textContent).toEqual('3');
    expect(fixture).toMatchSnapshot();
  });

});
