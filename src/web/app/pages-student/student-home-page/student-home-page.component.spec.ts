import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { StudentHomePageComponent } from './student-home-page.component';

import { MatSnackBarModule } from '@angular/material';
import { ResponseStatusPipe } from '../../pipes/session-response-status.pipe';
import { SubmissionStatusPipe } from '../../pipes/session-submission-status.pipe';

describe('StudentHomePageComponent', () => {
  let component: StudentHomePageComponent;
  let fixture: ComponentFixture<StudentHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentHomePageComponent,
        ResponseStatusPipe,
        SubmissionStatusPipe,
      ],
      imports: [
        HttpClientTestingModule,
        NgbModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback sessions', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        timeZone: 'UTC',
        creationTimestamp: new Date('2019-02-02T08:15:30').getTime(),
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };
    component.courses = [studentCourse];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all feedback sessions over 2 courses', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS2103',
        courseName: 'Software Engineering',
        timeZone: 'UTC',
        creationTimestamp: new Date('2019-02-02T08:15:30').getTime(),
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS2103',
            timeZone: '',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1200,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          endTime: new Date('2019-02-02T08:15:30').getTime(),
          isOpened: true,
          isWaitingToOpen: true,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS2103',
            timeZone: '',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1200,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          endTime: new Date('2019-02-02T08:15:30').getTime(),
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: false,
        },
      ],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'CS2102',
        courseName: 'Databases',
        timeZone: 'UTC',
        creationTimestamp: new Date('2019-02-02T08:15:30').getTime(),
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Third Session',
            courseId: 'CS2102',
            timeZone: '',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1200,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          endTime: new Date('2019-02-02T08:15:30').getTime(),
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Fourth Session',
            courseId: 'CS2102',
            timeZone: '',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 0,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          endTime: new Date('2019-02-02T08:15:30').getTime(),
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourseA, studentCourseB];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
