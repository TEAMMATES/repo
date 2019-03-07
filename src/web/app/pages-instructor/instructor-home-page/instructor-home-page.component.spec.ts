import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {
  FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { SessionsTableRowModel, SortBy, SortOrder } from '../../components/sessions-table/sessions-table-model';
import { InstructorPrivilege } from '../../instructor-privilege';
import { InstructorHomePageComponent } from './instructor-home-page.component';
import { InstructorHomePageModule } from './instructor-home-page.module';

describe('InstructorHomePageComponent', () => {
  let component: InstructorHomePageComponent;
  let fixture: ComponentFixture<InstructorHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorHomePageModule,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course without feedback session', () => {
    const instructorName: string = '';
    const courseTabModels: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        creationDate: '26 Feb 23:59 PM',
        timeZone: 'Asia/Singapore',
      },
      instructorPrivilege: {
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canSubmitSessionInSections: true,
      },
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.user = instructorName;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with one feedback session', () => {
    const instructorName: string = '';
    const feedbackSession: any = {
      courseId: 'CS3281',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Feedback 1',
      instructions: 'Answer all questions',
      submissionStartTimestamp: 0,
      submissionEndTimestamp: 1,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      customSessionVisibleTimestamp: 0,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      customResponseVisibleTimestamp: 0,
      submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
      publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
    };
    const instructorPrivilege: any = {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canSubmitSessionInSections: true,
    };
    const sessionsTableRowModels: any = {
      feedbackSession,
      instructorPrivilege,
      responseRate: '0 / 6',
      isLoadingResponseRate: false,
    };
    const courseTabModels: any = {
      instructorPrivilege,
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        creationDate: '26 Feb 23:59 PM',
        timeZone: 'Asia/Singapore',
      },
      sessionsTableRowModels: [sessionsTableRowModels],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.user = instructorName;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
