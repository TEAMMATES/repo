import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from '../page-not-found/page-not-found.component';
import { PageNotFoundModule } from '../page-not-found/page-not-found.module';
import { InstructorHelpPageComponent } from '../pages-help/instructor-help-page/instructor-help-page.component';
import { InstructorHelpPageModule } from '../pages-help/instructor-help-page/instructor-help-page.module';
import {
  InstructorCourseDetailsPageComponent,
} from './instructor-course-details-page/instructor-course-details-page.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page/instructor-course-edit-page.component';
import {
  InstructorCourseEnrollPageComponent,
} from './instructor-course-enroll-page/instructor-course-enroll-page.component';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page/instructor-course-student-details-page.component';
import {
  InstructorCourseStudentEditPageComponent,
} from './instructor-course-student-edit-page/instructor-course-student-edit-page.component';
import { InstructorCoursesPageComponent } from './instructor-courses-page/instructor-courses-page.component';
import { InstructorHomePageComponent } from './instructor-home-page/instructor-home-page.component';
import { InstructorSearchPageComponent } from './instructor-search-page/instructor-search-page.component';
import {
  InstructorSessionsEditPageComponent,
} from './instructor-sessions-edit-page/instructor-sessions-edit-page.component';
import { InstructorSessionsPageComponent } from './instructor-sessions-page/instructor-sessions-page.component';
import {
  InstructorSessionsResultPageComponent,
} from './instructor-sessions-result-page/instructor-sessions-result-page.component';
import {
  InstructorStudentListPageComponent,
} from './instructor-student-list-page/instructor-student-list-page.component';
import {
  InstructorStudentRecordsPageComponent,
} from './instructor-student-records-page/instructor-student-records-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: InstructorHomePageComponent,
  },
  {
    path: 'courses',
    children: [
      {
        path: '',
        component: InstructorCoursesPageComponent,
      },
      {
        path: 'edit',
        component: InstructorCourseEditPageComponent,
      },
      {
        path: 'details',
        component: InstructorCourseDetailsPageComponent,
      },
      {
        path: 'enroll',
        component: InstructorCourseEnrollPageComponent,
      },
      {
        path: 'student',
        children: [
          {
            path: 'details',
            component: InstructorCourseStudentDetailsPageComponent,
          },
          {
            path: 'edit',
            component: InstructorCourseStudentEditPageComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'sessions',
    children: [
      {
        path: '',
        component: InstructorSessionsPageComponent,
      },
      {
        path: 'edit',
        component: InstructorSessionsEditPageComponent,
      },
      {
        path: 'result',
        component: InstructorSessionsResultPageComponent,
      },
    ],
  },
  {
    path: 'students',
    children: [
      {
        path: '',
        component: InstructorStudentListPageComponent,
      },
      {
        path: 'records',
        component: InstructorStudentRecordsPageComponent,
      },
    ],
  },
  {
    path: 'search',
    component: InstructorSearchPageComponent,
  },
  {
    path: 'help',
    component: InstructorHelpPageComponent,
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: '**',
    pathMatch: 'full',
    component: PageNotFoundComponent,
  },
];

/**
 * Module for instructor pages.
 */
@NgModule({
  imports: [
    CommonModule,
    PageNotFoundModule,
    InstructorHelpPageModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    InstructorHomePageComponent,
    InstructorSearchPageComponent,
    InstructorSessionsPageComponent,
    InstructorSessionsEditPageComponent,
    InstructorSessionsResultPageComponent,
    InstructorStudentListPageComponent,
    InstructorStudentRecordsPageComponent,
    InstructorCoursesPageComponent,
    InstructorCourseDetailsPageComponent,
    InstructorCourseEditPageComponent,
    InstructorCourseEnrollPageComponent,
    InstructorCourseStudentEditPageComponent,
    InstructorCourseStudentDetailsPageComponent,
  ],
})
export class InstructorPagesModule {}
