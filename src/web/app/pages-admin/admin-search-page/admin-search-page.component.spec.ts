import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { EmailGenerationService } from '../../../services/email-generation.service';
import {
  FeedbackSessionsGroup, InstructorAccountSearchResult,
  SearchService, StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Email } from '../../../types/api-output';
import { AdminSearchPageComponent } from './admin-search-page.component';

const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  sessionName: {
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

describe('AdminSearchPageComponent', () => {
  let component: AdminSearchPageComponent;
  let fixture: ComponentFixture<AdminSearchPageComponent>;
  let accountService: AccountService;
  let searchService: SearchService;
  let studentService: StudentService;
  let statusMessageService: StatusMessageService;
  let emailGenerationService: EmailGenerationService;
  let ngbModal: NgbModal;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminSearchPageComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
        NgbTooltipModule,
        BrowserAnimationsModule,
      ],
      providers: [AccountService, SearchService, StatusMessageService, NgbModal],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSearchPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    searchService = TestBed.inject(SearchService);
    studentService = TestBed.inject(StudentService);
    statusMessageService = TestBed.inject(StatusMessageService);
    emailGenerationService = TestBed.inject(EmailGenerationService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', (() => {
    expect(fixture).toMatchSnapshot();
  }));

  it('should snap with a search key', () => {
    component.searchQuery = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded instructor table', () => {
    component.instructors = [
      {
        name: 'tester',
        email: 'tester@tester.com',
        googleId: 'instructor-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded student table', () => {
    component.students = [
      {
        name: 'Alice Betsy',
        email: 'alice.b.tmms@gmail.tmt',
        googleId: 'student-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
        section: 'section',
        team: 'team',
        comments: 'comments',
        recordsPageLink: 'records-page-link',
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display error message for invalid input', () => {
    spyOn(searchService, 'searchAdmin').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message');
        });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should display warning message for no results', () => {
    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: [],
      instructors: [],
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showWarningToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('No results found.');
        });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should display instructor results', () => {
    const instructorResults: InstructorAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
      },
      {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
      }];

    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: [],
      instructors: instructorResults,
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(2);
    expect(component.instructors).toEqual(instructorResults);
    expect(component.instructors[0].showLinks).toEqual(false);
    expect(component.instructors[1].showLinks).toEqual(false);
  });

  it('should display student results', () => {
    const studentResults: StudentAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        section: 'section1',
        team: 'team1',
        comments: 'comments1',
        recordsPageLink: 'recordsPageLink1',
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      }, {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        section: 'section2',
        team: 'team2',
        comments: 'comments2',
        recordsPageLink: 'recordsPageLink2',
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      }];

    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: studentResults,
      instructors: [],
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(2);
    expect(component.instructors.length).toEqual(0);
    expect(component.students).toEqual(studentResults);
    expect(component.students[0].showLinks).toEqual(false);
    expect(component.students[1].showLinks).toEqual(false);
  });

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-instructor-links');
    button.click();
    expect(component.instructors[0].showLinks).toEqual(true);
  });

  it('should show student links when expand all button clicked', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(true);
  });

  it('should show success message if successfully reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          name: 'dummy', course: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(accountService, 'resetInstructorAccount').and.returnValue(of('Success'));
    const spyStatusMessageService: any = spyOn(statusMessageService, 'showSuccessToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('The instructor\'s Google ID has been reset.');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          name: 'dummy', course: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(accountService, 'resetInstructorAccount').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show success message if successfully reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          name: 'dummy', course: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(accountService, 'resetStudentAccount').and.returnValue(of('success'));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showSuccessToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('The student\'s Google ID has been reset.');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          name: 'dummy', course: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(accountService, 'resetStudentAccount').and.returnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message.');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show success message and update all keys if successfully regenerated student registration key', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink?key=oldKey',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions:  {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          studentName: 'dummy', regenerateLinksCourseId: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(studentService, 'regenerateStudentCourseLinks').and.returnValue(of({
      message: 'success',
      newRegistrationKey: 'newKey',
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showSuccessToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('success');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toBeCalled();

    expect(studentResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
    expect(studentResult.openSessions.sessionName.feedbackSessionUrl).toEqual('openSession?key=newKey');
    expect(studentResult.notOpenSessions.sessionName.feedbackSessionUrl).toEqual('notOpenSession?key=newKey');
    expect(studentResult.publishedSessions.sessionName.feedbackSessionUrl).toEqual('publishedSession?key=newKey');
  });

  it('should show error message if fail to regenerate registration key for student in a course', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink?key=oldKey',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions:  {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(ngbModal, 'open').and.callFake(() => {
      return {
        componentInstance: {
          studentName: 'dummy', regenerateLinksCourseId: 'dummy',
        },
        result: Promise.resolve(),
      };
    });

    spyOn(studentService, 'regenerateStudentCourseLinks').and.returnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message.');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should generate email when course join email button clicked', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'jack',
      email: 'email@test.com',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: true,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const spyEmailGenerationService: any = spyOn(emailGenerationService, 'getCourseJoinEmail').and.callFake(
        (): Observable<Email> => of({
          recipient: 'Jacky Chan',
          subject: 'Course join email',
          content: 'Course join email content',
        }),
    );

    const sendButton: any = fixture.debugElement.nativeElement.querySelector('#send-course-join-button');
    sendButton.click();

    expect(spyEmailGenerationService).toBeCalled();
  });

  it('should show error message if fail to send course join email', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: true,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(emailGenerationService, 'getCourseJoinEmail').and.returnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message.');
        });

    const sendButton: any = fixture.debugElement.nativeElement.querySelector('#send-course-join-button');
    sendButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should generate email when send session reminder email button clicked', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'Jack Chan',
      email: 'email@test.com',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: true,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const spyEmailGenerationService: any = spyOn(emailGenerationService, 'getFeedbackSessionReminderEmail')
        .and.callFake(
            (): Observable<Email> => of({
              recipient: 'Jacky Chan',
              subject: 'Feedback reminder email',
              content: 'Feedback reminder email content',
            }),
        );

    const sendOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-open-session-reminder-button');
    sendOpenSessionReminderButton.click();

    expect(spyEmailGenerationService).toBeCalled();

    const sendNotOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-not-open-session-reminder-button');
    sendNotOpenSessionReminderButton.click();

    expect(spyEmailGenerationService).toBeCalled();

    const sendPublishedSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-published-session-reminder-button');
    sendPublishedSessionReminderButton.click();

    expect(spyEmailGenerationService).toBeCalled();

  });

  it('should show error message if fail to send session reminder email', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: true,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(emailGenerationService, 'getFeedbackSessionReminderEmail').and.returnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorToast').and.callFake(
        (args: string): void => {
          expect(args).toEqual('This is the error message.');
        });

    const sendOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-open-session-reminder-button');
    sendOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();

    const sendNotOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-not-open-session-reminder-button');
    sendNotOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();

    const sendPublishedSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector('#send-published-session-reminder-button');
    sendPublishedSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });
});
