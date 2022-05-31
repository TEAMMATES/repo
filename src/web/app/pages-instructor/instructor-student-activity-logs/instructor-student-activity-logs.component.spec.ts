import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { CourseService } from '../../../services/course.service';
import { LogService } from '../../../services/log.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionLog,
  FeedbackSessionLogType,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { ColumnData } from '../../components/sortable-table/sortable-table.component';
import { InstructorStudentActivityLogsComponent } from './instructor-student-activity-logs.component';
import { InstructorStudentActivityLogsModule } from './instructor-student-activity-logs.module';

describe('InstructorStudentActivityLogsComponent', () => {
  let component: InstructorStudentActivityLogsComponent;
  let fixture: ComponentFixture<InstructorStudentActivityLogsComponent>;
  let courseService: CourseService;
  let studentService: StudentService;
  let logService: LogService;
  let timezoneService: TimezoneService;

  const resultColumns: ColumnData[] = [
    { header: 'Time', sortBy: SortBy.LOG_DATE },
    { header: 'Name', sortBy: SortBy.GIVER_NAME },
    { header: 'Activity', sortBy: SortBy.LOG_TYPE },
    { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
    { header: 'Section', sortBy: SortBy.SECTION_NAME },
    { header: 'Team', sortBy: SortBy.TEAM_NAME },
  ];
  const testCourse1: Course = {
    courseId: 'CS9999',
    courseName: 'CS9999',
    institute: 'Test Institute',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
    privileges: {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    },
  };
  const testCourse2: Course = {
    courseId: 'MA1234',
    courseName: 'MA1234',
    institute: 'Test Institute',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
    privileges: {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    },
  };
  const testCourse3: Course = {
    courseId: 'EE1111',
    courseName: 'EE1111',
    institute: 'Test Institute',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
    privileges: {
      canModifyCourse: false,
      canModifySession: false,
      canModifyStudent: false,
      canModifyInstructor: false,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    },
  };
  const emptyStudent: Student = {
    courseId: '', email: '', name: '', sectionName: '', teamName: '',
  };
  const testStudent: Student = {
    email: 'doejohn@email.com',
    courseId: 'CS9999',
    name: 'Doe John',
    teamName: 'team 1',
    sectionName: 'section 1',
  };
  const testFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'Feedback Session 1',
    courseId: 'CS9999',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1549095330000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };
  const testLogs1: FeedbackSessionLog = {
    feedbackSessionData: testFeedbackSession,
    feedbackSessionLogEntries: [
      {
        studentData: testStudent,
        feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
        timestamp: 0,
      },
    ],
  };
  const testLogs2: FeedbackSessionLog = {
    feedbackSessionData: testFeedbackSession,
    feedbackSessionLogEntries: [
      {
        studentData: testStudent,
        feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
        timestamp: 0,
      },
    ],
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [InstructorStudentActivityLogsModule, HttpClientTestingModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentActivityLogsComponent);
    courseService = TestBed.inject(CourseService);
    studentService = TestBed.inject(StudentService);
    logService = TestBed.inject(LogService);
    timezoneService = TestBed.inject(TimezoneService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when page is still loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when searching for details in search form', () => {
    component.courses = [testCourse1, testCourse2];
    component.formModel = {
      logsDateFrom: { year: 1997, month: 9, day: 11 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 1998, month: 9, day: 11 },
      logsTimeTo: { hour: 15, minute: 0 },
      courseId: 'CS9999',
      studentEmail: 'doejohn@email.com',
    };
    component.courseToStudents = {
      CS9999: [testStudent],
      MA1234: [],
    };
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with results of a search', () => {
    component.searchResults = [
      {
        feedbackSessionName: 'Feedback session 1',
        logColumnsData: resultColumns,
        logRowsData: [[
          { value: '15 January 2021' },
          { value: 'Doe John' },
          { value: 'Viewed the submission page' },
          { value: 'doejohn@email.com' },
          { value: 'section 1' },
          { value: 'team 1' },
        ]],
        isTabExpanded: true,
      },
      {
        feedbackSessionName: 'Feedback session 2',
        logColumnsData: resultColumns,
        logRowsData: [],
        isTabExpanded: true,
      },
    ];
    component.isLoading = false;
    component.isSearching = false;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should load all courses that instructor has on init', () => {
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'getAllCoursesAsInstructor')
        .mockReturnValue(of({
          courses: [
            testCourse1, testCourse2, testCourse3,
          ],
        }));

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
    expect(courseSpy).toBeCalledWith('active');
    expect(component.courses.length).toEqual(2);
    expect(component.courses).toContainEqual(testCourse1);
    expect(component.courses).toContainEqual(testCourse2);
    expect(component.courses).not.toContainEqual(testCourse3);

    // courseToStudents not loaded on init
    expect(component.courseToStudents).toMatchObject({});
  });

  it('should load all students of selected course has on select', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            testStudent,
          ],
        }));

    component.formModel.courseId = testCourse1.courseId;
    component.loadStudents();

    expect(component.courseToStudents[testCourse1.courseId][0]).toEqual(emptyStudent);
    expect(component.courseToStudents[testCourse1.courseId][1]).toEqual(testStudent);
    expect(studentSpy).toHaveBeenNthCalledWith(1, { courseId: testCourse1.courseId });
  });

  it('should load students from cache if present', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            testStudent,
          ],
        }));

    component.formModel.courseId = testCourse1.courseId;
    component.courseToStudents[testCourse1.courseId] = [emptyStudent];
    component.loadStudents();

    expect(component.courseToStudents[testCourse1.courseId].length).toEqual(1);
    expect(component.courseToStudents[testCourse1.courseId][0]).toEqual(emptyStudent);
    expect(studentSpy).not.toHaveBeenCalled();
  });

  it('should search for logs using feedback course timezone when search button is clicked', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchFeedbackSessionLog')
        .mockReturnValue(of({ feedbackSessionLogs: [testLogs1, testLogs2] }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime');
    const tzOffset: number = timezoneService.getTzOffsets()[testCourse1.timeZone];

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsDateFrom: { year: 2020, month: 12, day: 30 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2020, month: 12, day: 31 },
      logsTimeTo: { hour: 23, minute: 59 },
      courseId: testCourse1.courseId,
      studentEmail: testStudent.email,
    };
    component.courses = [testCourse1];
    component.courseToStudents = { CS9999: [testStudent] };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#search-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(timeSpy).toHaveBeenCalledWith(
      component.formModel.logsDateFrom,
      component.formModel.logsTimeFrom,
      testCourse1.timeZone,
      true,
    );
    expect(logSpy).toHaveBeenCalled();
    expect(logSpy).toHaveBeenCalledWith({
      courseId: testCourse1.courseId,
      searchFrom: (new Date('2020-12-31T00:00+00:00').getTime() - tzOffset * 60 * 1000).toString(),
      searchUntil: (new Date('2021-01-01T00:00+00:00').getTime() - tzOffset * 60 * 1000).toString(),
      studentEmail: testStudent.email,
    });

    expect(component.searchResults.length).toEqual(2);

    for (let i: number = 0; i < 2; i += 1) {
      expect(component.searchResults[i].isTabExpanded).toBeFalsy();
      expect(component.searchResults[i].logColumnsData).toEqual(resultColumns);
      // Testing that the LogType is converted correctly.
      expect(component.searchResults[i].logRowsData[0][2].value).toEqual('Submitted responses');
    }
  });
});
