import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import moment from 'moment-timezone';

import { FormGroup } from '@angular/forms';
import { forkJoin, Observable, of } from 'rxjs';
import { concatAll, tap } from 'rxjs/operators';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  Course,
  FeedbackSession,
  FeedbackSessions,
  Instructor,
  InstructorPermissionRole,
  InstructorPrivilege,
  Instructors,
  JoinState,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { InstructorCreateRequest, InstructorPrivilegeUpdateRequest, Intent } from '../../../types/api-request';
import { FormValidator } from '../../../types/form-validator';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  InstructorOverallPermission,
  InstructorSectionLevelPermission,
  InstructorSessionLevelPermission,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import {
  DeleteInstructorConfirmModalComponent,
} from './delete-instructor-confirm-model/delete-instructor-confirm-modal.component';
import { EditMode, InstructorEditPanel } from './instructor-edit-panel/instructor-edit-panel.component';
import {
  ResendInvitationEmailModalComponent,
} from './resend-invitation-email-modal/resend-invitation-email-modal.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

interface InstructorEditPanelDetail {
  originalInstructor: Instructor;
  originalPanel: InstructorEditPanel;
  editPanel: InstructorEditPanel;
}

/**
 * Instructor course edit page.
 */
@Component({
  selector: 'tm-instructor-course-edit-page',
  templateUrl: './instructor-course-edit-page.component.html',
  styleUrls: ['./instructor-course-edit-page.component.scss'],
})
export class InstructorCourseEditPageComponent implements OnInit {

  @ViewChild('courseForm', { static: false }) form!: FormGroup;

  // enum
  EditMode: typeof EditMode = EditMode;
  FormValidator: typeof FormValidator = FormValidator;

  courseId: string = '';
  timezones: string[] = [];
  isEditingCourse: boolean = false;
  course: Course = {
    courseName: '',
    courseId: '',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  originalCourse: Course = {
    courseName: '',
    courseId: '',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  currInstructorGoogleId: string = '';
  currInstructorCoursePrivilege: InstructorPrivilege = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  };

  instructorDetailPanels: InstructorEditPanelDetail[] = [];

  isAddingNewInstructor: boolean = false;
  newInstructorPanel: InstructorEditPanel = {
    googleId: '',
    courseId: '',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.NOT_JOINED,

    permission: {
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudentInSections: false,
        canModifySessionCommentsInSections: false,
        canViewSessionInSections: false,
        canSubmitSessionInSections: false,
      },
      sectionLevel: [],
    },

    isEditing: true,
  };

  // for fine-grain permission setting
  allSections: string[] = [];
  allSessions: string[] = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private timezoneService: TimezoneService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private feedbackSessionsService: FeedbackSessionsService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private authService: AuthService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;

      this.loadCourseInfo();
      this.loadCurrInstructorInfo();

      // load all section and session name
      forkJoin(
          this.studentService.getStudentsFromCourse({ courseId: this.courseId }),
          this.feedbackSessionsService.getFeedbackSessionsForInstructor(this.courseId),
      ).subscribe((vals: any[]) => {
        const students: Students = vals[0] as Students;
        const sessions: FeedbackSessions = vals[1] as FeedbackSessions;

        this.allSections =
            Array.from(new Set(students.students.map((value: Student) => value.sectionName)));
        this.allSessions =
            sessions.feedbackSessions.map((session: FeedbackSession) => session.feedbackSessionName);

        this.loadCourseInstructors();
      });
    });

    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
  }

  /**
   * Replaces the timezone value with the detected timezone.
   */
  detectTimezone(): void {
    this.course.timeZone = moment.tz.guess();
  }

  /**
   * Loads the course being edited.
   */
  loadCourseInfo(): void {
    this.courseService.getCourseAsInstructor(this.courseId).subscribe((resp: Course) => {
      this.course = resp;
      this.originalCourse = Object.assign({}, resp);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Loads the information of the current logged-in instructor.
   */
  loadCurrInstructorInfo(): void {
    // privilege
    this.instructorService.loadInstructorPrivilege({
      courseId: this.courseId,
    }).subscribe((resp: InstructorPrivilege) => {
      this.currInstructorCoursePrivilege = resp;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });

    // login credential
    this.authService.getAuthUser().subscribe((res: AuthInfo) => {
      this.currInstructorGoogleId = res.user === undefined ? '' : res.user.id;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Deletes the current course and redirects to 'Courses' page if action is successful.
   */
  deleteCourse(): void {
    this.courseService.binCourse(this.courseId).subscribe((course: Course) => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses',
          `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Saves the updated course details.
   */
  onSaveCourse(): void {
    if (this.form.invalid) {
      Object.values(this.form.controls).forEach((control: any) => control.markAsTouched());
      return;
    }
    this.courseService.updateCourse(this.courseId, {
      courseName: this.course.courseName,
      timeZone: this.course.timeZone,
    }).subscribe((resp: Course) => {
      this.statusMessageService.showSuccessToast('The course has been edited.');
      this.isEditingCourse = false;
      this.course = resp;
      this.originalCourse = Object.assign({}, resp);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
    Object.values(this.form.controls).forEach((control: any) => control.markAsUntouched());
    Object.values(this.form.controls).forEach((control: any) => control.markAsPristine());
  }

  /**
   * Cancels editing the course details.
   */
  cancelEditingCourse(): void {
    this.course = Object.assign({}, this.originalCourse);
    this.isEditingCourse = false;
    Object.values(this.form.controls).forEach((control: any) => control.markAsPristine());
    Object.values(this.form.controls).forEach((control: any) => control.markAsUntouched());
  }

  /**
   * Loads all instructors in the course.
   */
  loadCourseInstructors(): void {
    this.instructorService.loadInstructors({
      courseId: this.courseId,
      intent: Intent.FULL_DETAIL,
    })
        .subscribe((resp: Instructors) => {
          this.instructorDetailPanels = resp.instructors.map((i: Instructor) => ({
            originalInstructor: Object.assign({}, i),
            originalPanel: this.getInstructorEditPanelModel(i),
            editPanel: this.getInstructorEditPanelModel(i),
          }));
          this.instructorDetailPanels.forEach((panel: InstructorEditPanelDetail) => {
            this.loadPermissionForInstructor(panel);
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Gets the default edit panel model of an instructor.
   */
  getInstructorEditPanelModel(i: Instructor): InstructorEditPanel {
    /**
     * The non-null assertion operator (!) is used below in `isDisplayedToStudents`,
     * `displayedToStudentsAs` and `role`. These attributes should never be undefined and are only
     * typed as such to accomodate for a use case in SearchService.
     */
    return {
      googleId: i.googleId,
      courseId: i.courseId,
      email: i.email,
      // tslint:disable-next-line
      isDisplayedToStudents: i.isDisplayedToStudents!,
      // tslint:disable-next-line
      displayedToStudentsAs: i.displayedToStudentsAs!,
      name: i.name,
      // tslint:disable-next-line
      role: i.role!,
      joinState: i.joinState,

      permission: {
        privilege: {
          canModifyCourse: true,
          canModifySession: true,
          canModifyStudent: true,
          canModifyInstructor: true,
          canViewStudentInSections: true,
          canModifySessionCommentsInSections: true,
          canViewSessionInSections: true,
          canSubmitSessionInSections: true,
        },
        sectionLevel: [],
      },

      isEditing: false,
    };
  }

  /**
   * Shows the model of details permission for a role.
   */
  viewRolePrivilegeModel(role: InstructorPermissionRole): void {
    const modalRef: NgbModalRef = this.modalService.open(ViewRolePrivilegesModalComponent);
    modalRef.result.then(() => {}, () => {});
    this.instructorService.loadInstructorPrivilege({
      courseId: this.courseId,
      instructorRole: role,
    }).subscribe((resp: InstructorPrivilege) => {
      modalRef.componentInstance.instructorPrivilege = resp;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Cancels editing an instructor.
   */
  cancelEditingInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    panelDetail.editPanel = JSON.parse(JSON.stringify(panelDetail.originalPanel));
  }

  /**
   * Saves instructor at index.
   */
  saveInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const reqBody: InstructorCreateRequest = {
      id: panelDetail.originalInstructor.joinState === JoinState.JOINED
          ? panelDetail.originalInstructor.googleId : undefined,
      name: panelDetail.editPanel.name,
      email: panelDetail.originalInstructor.joinState === JoinState.JOINED
          ? panelDetail.editPanel.email : panelDetail.originalInstructor.email,
      role: panelDetail.editPanel.role,
      displayName: panelDetail.editPanel.displayedToStudentsAs,
      isDisplayedToStudent: panelDetail.editPanel.isDisplayedToStudents,
    } as InstructorCreateRequest;

    this.instructorService.updateInstructor({
      courseId: panelDetail.originalInstructor.courseId,
      requestBody: reqBody,
    }).subscribe((resp: Instructor) => {
      panelDetail.originalInstructor = Object.assign({}, resp);
      const permission: InstructorOverallPermission = panelDetail.editPanel.permission;

      panelDetail.editPanel = this.getInstructorEditPanelModel(resp);
      panelDetail.editPanel.permission = permission;

      this.updatePrivilegeForInstructor(panelDetail.originalInstructor, panelDetail.editPanel.permission);

      this.statusMessageService.showSuccessToast(`The instructor ${resp.name} has been updated.`);

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });

    panelDetail.editPanel.isEditing = false;
    panelDetail.originalPanel = JSON.parse(JSON.stringify(panelDetail.editPanel));
  }

  /**
   * Deletes instructor at index.
   */
  deleteInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const modalRef: NgbModalRef = this.modalService.open(DeleteInstructorConfirmModalComponent);
    modalRef.componentInstance.instructorToDelete = panelDetail.originalInstructor;
    modalRef.componentInstance.isDeletingSelf = panelDetail.originalInstructor.googleId === this.currInstructorGoogleId;

    modalRef.result.then(() => {
      this.instructorService.deleteInstructor({
        courseId: panelDetail.originalInstructor.courseId,
        instructorEmail: panelDetail.originalInstructor.email,
      }).subscribe(() => {
        if (panelDetail.originalInstructor.googleId === this.currInstructorGoogleId) {
          this.navigationService.navigateWithSuccessMessage(
                  this.router, '/web/instructor/courses', 'Instructor is successfully deleted.');
        } else {
          this.instructorDetailPanels.splice(index, 1);
          this.statusMessageService.showSuccessToast('Instructor is successfully deleted.');
        }
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Re-sends an invitation email to an instructor in the course.
   */
  resendReminderEmail(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const modalRef: NgbModalRef = this.modalService.open(ResendInvitationEmailModalComponent);
    modalRef.componentInstance.instructorToResend = panelDetail.originalInstructor;

    modalRef.result.then(() => {
      this.courseService
          .remindInstructorForJoin(panelDetail.originalInstructor.courseId, panelDetail.originalInstructor.email)
          .subscribe((resp: MessageOutput) => {
            this.statusMessageService.showSuccessToast(resp.message);
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          });
    }, () => {});
  }

  /**
   * Adds new instructor.
   */
  addNewInstructor(): void {
    const reqBody: InstructorCreateRequest = {
      name: this.newInstructorPanel.name,
      email: this.newInstructorPanel.email,
      role: this.newInstructorPanel.role,
      displayName: this.newInstructorPanel.displayedToStudentsAs,
      isDisplayedToStudent: this.newInstructorPanel.isDisplayedToStudents,
    } as InstructorCreateRequest;

    this.instructorService.createInstructor({ courseId: this.courseId, requestBody: reqBody })
        .subscribe((resp: Instructor) => {
          const newDetailPanels: InstructorEditPanelDetail = {
            originalInstructor: Object.assign({}, resp),
            originalPanel: this.getInstructorEditPanelModel(resp),
            editPanel: this.getInstructorEditPanelModel(resp),
          };
          newDetailPanels.editPanel.permission = this.newInstructorPanel.permission;
          newDetailPanels.originalPanel = JSON.parse(JSON.stringify(newDetailPanels.editPanel));

          this.instructorDetailPanels.push(newDetailPanels);
          this.statusMessageService.showSuccessToast(`"The instructor ${resp.name} has been added successfully.
          An email containing how to 'join' this course will be sent to ${resp.email} in a few minutes."`);

          this.updatePrivilegeForInstructor(newDetailPanels.originalInstructor, newDetailPanels.editPanel.permission);

          this.isAddingNewInstructor = false;
          this.newInstructorPanel = {
            googleId: '',
            courseId: '',
            email: '',
            isDisplayedToStudents: true,
            displayedToStudentsAs: '',
            name: '',
            role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
            joinState: JoinState.NOT_JOINED,

            permission: {
              privilege: {
                canModifyCourse: true,
                canModifySession: true,
                canModifyStudent: true,
                canModifyInstructor: true,
                canViewStudentInSections: true,
                canModifySessionCommentsInSections: true,
                canViewSessionInSections: true,
                canSubmitSessionInSections: true,
              },
              sectionLevel: [],
            },

            isEditing: true,
          };
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Loads permission for instructor.
   */
  loadPermissionForInstructor(panel: InstructorEditPanelDetail): void {
    const instructor: Instructor = panel.originalInstructor;
    const permission: InstructorOverallPermission = panel.editPanel.permission;

    if (instructor.role !== InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM) {
      return;
    }

    // only need to load for custom role.
    const requests: Observable<any>[] = [];

    requests.push(this.instructorService.loadInstructorPrivilege({
      courseId: instructor.courseId,
      instructorEmail: instructor.email,
    })
        .pipe(tap((resp: InstructorPrivilege) => {
          permission.privilege.canModifyCourse = resp.canModifyCourse;
          permission.privilege.canModifySession = resp.canModifySession;
          permission.privilege.canModifyStudent = resp.canModifyStudent;
          permission.privilege.canModifyInstructor = resp.canModifyInstructor;
          permission.privilege.canViewStudentInSections = resp.canViewStudentInSections;
          permission.privilege.canModifySessionCommentsInSections = resp.canModifySessionCommentsInSections;
          permission.privilege.canViewSessionInSections = resp.canViewSessionInSections;
          permission.privilege.canSubmitSessionInSections = resp.canSubmitSessionInSections;
        })));

    this.allSections.forEach((sectionName: string) => {
      const sectionLevelPermission: InstructorSectionLevelPermission = {
        sectionNames: [sectionName],
        privilege: {
          canModifyCourse: false,
          canModifySession: false,
          canModifyStudent: false,
          canModifyInstructor: false,
          canViewStudentInSections: false,
          canModifySessionCommentsInSections: false,
          canViewSessionInSections: false,
          canSubmitSessionInSections: false,
        },
        sessionLevel: [],
      };
      permission.sectionLevel.push(sectionLevelPermission);

      requests.push(this.instructorService.loadInstructorPrivilege({
        sectionName,
        courseId: instructor.courseId,
        instructorEmail: instructor.email,
      }).pipe(tap((resp: InstructorPrivilege) => {
        sectionLevelPermission.privilege.canViewStudentInSections = resp.canViewStudentInSections;
        sectionLevelPermission.privilege.canModifySessionCommentsInSections = resp.canModifySessionCommentsInSections;
        sectionLevelPermission.privilege.canViewSessionInSections = resp.canViewSessionInSections;
        sectionLevelPermission.privilege.canSubmitSessionInSections = resp.canSubmitSessionInSections;
      })));
      this.allSessions.forEach((sessionName: string) => {
        requests.push(this.instructorService.loadInstructorPrivilege({
          sectionName,
          feedbackSessionName: sessionName,
          courseId: instructor.courseId,
          instructorEmail: instructor.email,
        }).pipe(tap((resp: InstructorPrivilege) => {
          sectionLevelPermission.sessionLevel.push({
            sessionName,
            privilege: {
              canModifyCourse: false,
              canModifySession: false,
              canModifyStudent: false,
              canModifyInstructor: false,
              canViewStudentInSections: false,
              canModifySessionCommentsInSections: resp.canModifySessionCommentsInSections,
              canViewSessionInSections: resp.canViewSessionInSections,
              canSubmitSessionInSections: resp.canSubmitSessionInSections,
            },
          });
        })));
      });
    });

    // get all permission
    forkJoin(requests).subscribe(() => {
      permission.sectionLevel = permission.sectionLevel
          .filter((sectionLevelPermission: InstructorSectionLevelPermission) => {
            // discard section level permission that is consistent with the overall permission
            if (sectionLevelPermission.privilege.canViewStudentInSections
                !== permission.privilege.canViewStudentInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canModifySessionCommentsInSections
                !== permission.privilege.canModifySessionCommentsInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canViewSessionInSections
                !== permission.privilege.canViewSessionInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canSubmitSessionInSections
                !== permission.privilege.canSubmitSessionInSections) {
              return true;
            }

            return sectionLevelPermission.sessionLevel
                .some((sessionLevelPermission: InstructorSessionLevelPermission) => {
                  return sectionLevelPermission.privilege.canModifySessionCommentsInSections
                      !== sessionLevelPermission.privilege.canModifySessionCommentsInSections ||
                      sectionLevelPermission.privilege.canViewSessionInSections
                      !== sessionLevelPermission.privilege.canViewSessionInSections ||
                      sectionLevelPermission.privilege.canSubmitSessionInSections
                      !== sessionLevelPermission.privilege.canSubmitSessionInSections;
                });
          });

      permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
        if (sectionLevel.sessionLevel.every((sessionLevel: InstructorSessionLevelPermission) => {
          return sectionLevel.privilege.canModifySessionCommentsInSections
              === sessionLevel.privilege.canModifySessionCommentsInSections &&
              sectionLevel.privilege.canViewSessionInSections
              === sessionLevel.privilege.canViewSessionInSections &&
              sectionLevel.privilege.canSubmitSessionInSections
              === sessionLevel.privilege.canSubmitSessionInSections;
        })) {
          // session level is consistent with the section level, we can remove it.
          sectionLevel.sessionLevel = [];
        }
      });
      panel.originalPanel = JSON.parse(JSON.stringify(panel.editPanel));
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Updates privilege for instructor
   */
  updatePrivilegeForInstructor(instructor: Instructor, permission: InstructorOverallPermission): void {
    if (instructor.role !== InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM) {
      return;
    }

    // only need to update for custom role.
    const requests: Observable<any>[] = [];

    requests.push(
        this.instructorService.updateInstructorPrivilege({
          courseId: instructor.courseId,
          instructorEmail: instructor.email,
          requestBody: {
            canModifyCourse: permission.privilege.canModifyCourse,
            canModifySession: permission.privilege.canModifySession,
            canModifyStudent: permission.privilege.canModifyStudent,
            canModifyInstructor: permission.privilege.canModifyInstructor,
            canViewStudentInSections: permission.privilege.canViewStudentInSections,
            canModifySessionCommentsInSections: permission.privilege.canModifySessionCommentsInSections,
            canViewSessionInSections: permission.privilege.canViewSessionInSections,
            canSubmitSessionInSections: permission.privilege.canSubmitSessionInSections,
          } as InstructorPrivilegeUpdateRequest,
        }).pipe(tap((resp: InstructorPrivilege) => {
          permission.privilege.canModifyCourse = resp.canModifyCourse;
          permission.privilege.canModifySession = resp.canModifySession;
          permission.privilege.canModifyStudent = resp.canModifyStudent;
          permission.privilege.canModifyInstructor = resp.canModifyInstructor;
          permission.privilege.canViewStudentInSections = resp.canViewStudentInSections;
          permission.privilege.canModifySessionCommentsInSections = resp.canModifySessionCommentsInSections;
          permission.privilege.canViewSessionInSections = resp.canViewSessionInSections;
          permission.privilege.canSubmitSessionInSections = resp.canSubmitSessionInSections;
        })));

    permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
      sectionLevel.sectionNames.forEach((sectionName: string) => {
        requests.push(
            this.instructorService.updateInstructorPrivilege({
              courseId: instructor.courseId,
              instructorEmail: instructor.email,
              requestBody: {
                sectionName,
                canViewStudentInSections: sectionLevel.privilege.canViewStudentInSections,
                canModifySessionCommentsInSections: sectionLevel.privilege.canModifySessionCommentsInSections,
                canViewSessionInSections: sectionLevel.privilege.canViewSessionInSections,
                canSubmitSessionInSections: sectionLevel.privilege.canSubmitSessionInSections,
              } as InstructorPrivilegeUpdateRequest,
            }).pipe(tap((resp: InstructorPrivilege) => {
              sectionLevel.privilege.canViewStudentInSections = resp.canViewStudentInSections;
              sectionLevel.privilege.canModifySessionCommentsInSections = resp.canModifySessionCommentsInSections;
              sectionLevel.privilege.canViewSessionInSections = resp.canViewSessionInSections;
              sectionLevel.privilege.canSubmitSessionInSections = resp.canSubmitSessionInSections;
            })));

        sectionLevel.sessionLevel.forEach((sessionLevel: InstructorSessionLevelPermission) => {
          requests.push(
              this.instructorService.updateInstructorPrivilege({
                courseId: instructor.courseId,
                instructorEmail: instructor.email,
                requestBody: {
                  sectionName,
                  feedbackSessionName: sessionLevel.sessionName,
                  canModifySessionCommentsInSections: sessionLevel.privilege.canModifySessionCommentsInSections,
                  canViewSessionInSections: sessionLevel.privilege.canViewSessionInSections,
                  canSubmitSessionInSections: sessionLevel.privilege.canSubmitSessionInSections,
                } as InstructorPrivilegeUpdateRequest,
              }).pipe(tap((resp: InstructorPrivilege) => {
                sessionLevel.privilege.canModifySessionCommentsInSections = resp.canModifySessionCommentsInSections;
                sessionLevel.privilege.canViewSessionInSections = resp.canViewSessionInSections;
                sessionLevel.privilege.canSubmitSessionInSections = resp.canSubmitSessionInSections;
              })));
        });
      });
    });

    of(...requests).pipe(concatAll()).subscribe(() => {
      // privileges updated
      // filter out empty permission setting
      permission.sectionLevel = permission.sectionLevel.filter(
          (sectionLevel: InstructorSectionLevelPermission) => sectionLevel.sectionNames.length !== 0);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }
}
