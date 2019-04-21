import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { Course, Instructor, JoinState, Student } from '../../../types/api-output';
import { Gender } from '../../../types/gender';
import { StudentCourseDetailsPageComponent, StudentProfileWithPicture } from './student-course-details-page.component';

describe('StudentCourseDetailsPageComponent', () => {
  let component: StudentCourseDetailsPageComponent;
  let fixture: ComponentFixture<StudentCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentCourseDetailsPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentCourseDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    const student: Student = {
      courseId: '1.1.c-demo2',
      email: '1@1.com',
      lastName: '1',
      name: '1',
      comments: '',
      joinState: JoinState.NOT_JOINED,
      sectionName: 'Tutorial Group 2',
      teamName: 'Team 2',
    };

    const instructorDetails: Instructor[] = [{
      courseId: '1.1.c-demo2',
      displayedToStudentsAs: 'Instructor',
      isDisplayedToStudents: true,
      email: '1@1.com',
      name: '1',
      joinState: JoinState.JOINED,
    }];

    const course: Course = {
      courseId: '1.1.c-demo2',
      courseName: 'Sample Course 101',
      creationDate: '13 Mar 2019',
      deletionDate: 'Not Applicable',
      timeZone: 'UTC',
    };

    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        studentProfile: {
          email: 'iam2@hello.com',
          gender: Gender.MALE,
          institute: 'nus',
          moreInfo: 'Misc',
          name: '2',
          nationality: 'Andorran',
          shortName: 'I am 2',
        },
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
