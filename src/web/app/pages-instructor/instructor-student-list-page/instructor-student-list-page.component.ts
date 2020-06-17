import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { LoadingBarService } from '../../../services/loading-bar.service';
import { CourseStatistics, StatisticsCalculatorService } from '../../../services/statistics-calculator.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Course, Courses, InstructorPrivilege, Student, Students } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListRowModel } from '../student-list/student-list.component';

interface StudentIndexedData {
  [key: string]: Student[];
}

interface CourseTab {
  course: Course;
  studentList: StudentListRowModel[];
  hasTabExpanded: boolean;
  hasStudentLoaded: boolean;
  stats: CourseStatistics;
}

/**
 * Instructor student list page.
 */
@Component({
  selector: 'tm-instructor-student-list-page',
  templateUrl: './instructor-student-list-page.component.html',
  styleUrls: ['./instructor-student-list-page.component.scss'],
})
export class InstructorStudentListPageComponent implements OnInit {

  courseTabList: CourseTab[] = [];

  constructor(private instructorService: InstructorService,
              private courseService: CourseService,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService,
              private loadingBarService: LoadingBarService,
              private statisticsCalculatorService: StatisticsCalculatorService) {
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.loadingBarService.showLoadingBar();
    this.courseService.getAllCoursesAsInstructor('active')
        .pipe(finalize(() => this.loadingBarService.hideLoadingBar()))
        .subscribe((courses: Courses) => {
          courses.courses.forEach((course: Course) => {
            const courseTab: CourseTab = {
              course,
              studentList: [],
              hasTabExpanded: false,
              hasStudentLoaded: false,
              stats: {
                numOfSections: 0,
                numOfStudents: 0,
                numOfTeams: 0,
              },
            };

            this.courseTabList.push(courseTab);
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Toggles specific card and loads students if needed
   */
  toggleCard(courseTab: CourseTab): void {
    courseTab.hasTabExpanded = !courseTab.hasTabExpanded;
    if (!courseTab.hasStudentLoaded) {
      this.loadStudents(courseTab);
      courseTab.hasStudentLoaded = true;
    }
  }

  /**
   * Loads students of a specified course.
   */
  loadStudents(courseTab: CourseTab): void {
    this.studentService.getStudentsFromCourse({ courseId: courseTab.course.courseId })
        .subscribe((students: Students) => {
          const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
            const term: string = x.sectionName;
            (acc[term] = acc[term] || []).push(x);
            return acc;
          }, {});

          Object.keys(sections).forEach((key: string) => {
            const studentsInSection: Student[] = sections[key];

            const studentList: StudentListRowModel[] = [];
            studentsInSection.forEach((student: Student) => {
              const studentData: StudentListRowModel = {
                name: student.name,
                status: student.joinState,
                email: student.email,
                team: student.teamName,
                sectionName: key,
                isAllowedToModifyStudent: false,
                isAllowedToViewStudentInSection: false,
              };
              studentList.push(studentData);
            });

            this.loadPrivilege(courseTab, key, studentList);
          });
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadPrivilege(courseTab: CourseTab, sectionName: string, students: StudentListRowModel[]): void {
    this.instructorService.loadInstructorPrivilege({ sectionName, courseId: courseTab.course.courseId })
        .subscribe((instructorPrivilege: InstructorPrivilege) => {
          students.forEach((student: StudentListRowModel) => {
            student.isAllowedToViewStudentInSection = instructorPrivilege.canViewStudentInSections;
            student.isAllowedToModifyStudent = instructorPrivilege.canModifyStudent;
          });

          courseTab.studentList.push(...students);
          courseTab.stats =
              this.statisticsCalculatorService.calculateCourseStatistics(courseTab.studentList);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(courseTab: CourseTab, studentEmail: string): void {
    this.courseService.removeStudentFromCourse(courseTab.course.courseId, studentEmail).subscribe(() => {
      this.statusMessageService
          .showSuccessMessage(`Student is successfully deleted from course "${courseTab.course.courseId}"`);
      courseTab.studentList =
          courseTab.studentList.filter((student: StudentListRowModel) => student.email !== studentEmail);
      courseTab.stats =
          this.statisticsCalculatorService.calculateCourseStatistics(courseTab.studentList);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
