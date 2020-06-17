import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { ClipboardModule } from 'ngx-clipboard';
import { Course, Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentListRowModel } from '../student-list/student-list.component';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';
import { InstructorCourseDetailsPageModule } from './instructor-course-details-page.module';

const course: Course = {
  courseId: 'CS101',
  courseName: 'Introduction to CS',
  timeZone: '',
  creationTimestamp: 0,
  deletionTimestamp: 0,
};

const student: any = {
  name: 'Jamie',
  email: 'jamie@gmail.com',
  status: 'Yet to join',
  team: 'Team 1',
};

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TeammatesCommonModule,
        RouterTestingModule,
        ClipboardModule,
        MatSnackBarModule,
        InstructorCourseDetailsPageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a course with one co-owner and no students, and populated course student list', () => {
    const stats: any = {
      sectionsTotal: 0,
      teamsTotal: 0,
      studentsTotal: 0,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      joinState: JoinState.JOINED,
      googleId: 'Hodor',
      name: 'Hodor',
      email: 'hodor@gmail.com',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      displayedToStudentsAs: 'Hodor',
      isDisplayedToStudents: true,
    };
    const courseDetails: any = {
      course,
      stats,
    };
    component.courseDetails = courseDetails;
    component.instructors = [coOwner];
    component.courseStudentListAsCsv = 'a,b';
    component.loading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a course with one co-owner and one student, and ajax failure', () => {
    const stats: any = {
      sectionsTotal: 1,
      teamsTotal: 1,
      studentsTotal: 1,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      joinState: JoinState.JOINED,
      googleId: 'Bran',
      name: 'Bran',
      email: 'bran@gmail.com',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      displayedToStudentsAs: 'Bran',
      isDisplayedToStudents: false,
    };
    const courseDetails: any = {
      course,
      stats,
    };
    const studentListRowModel: StudentListRowModel = {
      sectionName: 'Tutorial Group 1',
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
      name: student.name,
      email: student.email,
      status: student.status,
      team: student.team,
    };
    component.students = [studentListRowModel];
    component.courseDetails = courseDetails;
    component.instructors = [coOwner];
    component.isAjaxSuccess = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
