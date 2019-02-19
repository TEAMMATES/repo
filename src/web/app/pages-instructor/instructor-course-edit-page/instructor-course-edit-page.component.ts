import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { CourseEditFormModel } from './course-edit-form/course-edit-form-model';
import { DeleteInstructorModalComponent } from './delete-instructor-modal/delete-instructor-modal.component';
import {
  DefaultPrivileges, Privileges, SectionLevelPrivileges, SessionLevelPrivileges,
} from './instructor-privileges-model';
import { ResendReminderModalComponent } from './resend-reminder-modal/resend-reminder-modal.component';
import { ViewPrivilegesModalComponent } from './view-privileges-modal/view-privileges-modal.component';

interface Course {
  id: string;
  name: string;
  creationDate: string;
  timeZone: string;
}

/**
 * Possible instructor roles.
 */
enum Role {
  /**
   * Co-owner instructor role.
   */
  COOWNER = 'Co-owner',

  /**
   * Manager instructor role.
   */
  MANAGER = 'Manager',

  /**
   * Observer instructor role.
   */
  OBSERVER = 'Observer',

  /**
   * Tutor instructor role.
   */
  TUTOR = 'Tutor',

  /**
   * Custom instructor role.
   */
  CUSTOM = 'Custom',
}

interface InstructorAttributes {
  googleId: string;
  name: string;
  email: string;
  role: Role;
  isDisplayedToStudents: boolean;
  displayedName: string;
  privileges: Privileges;
}

interface CourseEditDetails {
  courseToEdit: Course;
  instructorList: InstructorAttributes[];
  instructor: InstructorAttributes;
  instructorToShowIndex: number;
  sectionNames: string[];
  feedbackNames: string[];
  defaultPrivileges: DefaultPrivileges;
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

  user: string = '';

  // enums
  Role: typeof Role = Role;

  // models
  courseEditFormModel: CourseEditFormModel = {
    courseId: '',
    courseName: '',
    timeZone: 'UTC',

    isEditable: false,
    isSaving: false,
  };

  formEditInstructors!: FormGroup;
  formInstructors!: FormArray;

  instructorList: InstructorAttributes[] = [];
  instructor!: InstructorAttributes;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];
  defaultPrivileges!: DefaultPrivileges;

  isAddingInstructor: boolean = false;
  formAddInstructor!: FormGroup;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private fb: FormBuilder,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEditDetails(queryParams.courseid);
    });
  }

  /******************************************************************************
   * COURSE DETAILS RELATED FUNCTIONS
   ******************************************************************************/

  /**
   * Gets details related to the specified course.
   */
  getCourseEditDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/instructors/course/details', paramMap)
        .subscribe((resp: CourseEditDetails) => {
          this.courseEditFormModel = this.getCourseEditFormModel(resp.courseToEdit);
          this.instructorList = resp.instructorList;
          this.instructor = resp.instructor;
          this.instructorToShowIndex = resp.instructorToShowIndex;
          this.sectionNames = resp.sectionNames;
          this.feedbackNames = resp.feedbackNames;
          this.defaultPrivileges = resp.defaultPrivileges;

          this.initEditInstructorsForm();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Gets the {@code courseEditFormModel} with {@link Course} entity.
   */
  getCourseEditFormModel(course: Course): CourseEditFormModel {
    const model: CourseEditFormModel = {
      courseId: course.id,
      courseName: course.name,
      timeZone: course.timeZone,

      isEditable: false,
      isSaving: false,
    };

    return model;
  }

  /**
   * Handles editing course details event.
   */
  editCourseHandler(): void {
    this.courseEditFormModel.isSaving = true;

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      coursename: this.courseEditFormModel.courseName,
      coursetimezone: this.courseEditFormModel.timeZone,
    };

    this.httpRequestService.put('/instructors/course/details/save', paramsMap)
        .subscribe((course: Course) => {
          this.courseEditFormModel = this.getCourseEditFormModel(course);

          this.statusMessageService.showSuccessMessage(`Updated course [${course.id}] details: `
              + `Name: ${course.name}, Time zone: ${course.timeZone}`);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Handles deleting course event.
   */
  deleteCourseHandler(): void {
    const paramsMap: { [key: string]: string } = { courseid: this.courseEditFormModel.courseId };

    this.httpRequestService.delete('/instructors/course/delete', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /******************************************************************************
   * EDIT INSTRUCTOR RELATED FUNCTIONS
   ******************************************************************************/

  /**
   * Gets the placeholder content for displayed name when it is not displayed to students.
   */
  getPlaceholderForDisplayedName(isDisplayed: boolean): string {
    return isDisplayed ? 'E.g.Co-lecturer, Teaching Assistant'
        : '(This instructor will NOT be displayed to students)';
  }

  /**
   * Initialises the details panels with data from the backend for all instructors.
   */
  private initEditInstructorsForm(): void {
    this.formEditInstructors = this.fb.group({ formInstructors: [] });

    const control: FormArray = this.fb.array([]);
    this.instructorList.forEach((instructor: InstructorAttributes, index: number) => {
      const defaultPrivileges: Privileges = instructor.privileges;
      const instructorEmail: string = instructor.email ? instructor.email : '';
      const instructorDisplayedName: string = instructor.isDisplayedToStudents ? instructor.displayedName : '';

      const instructorForm: FormGroup = this.fb.group({
        googleId: [{ value: instructor.googleId, disabled: true }],
        name: [{ value: instructor.name, disabled: true }],
        email: [{ value: instructorEmail, disabled: true }],
        isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
        displayedName: [{ value: instructorDisplayedName, disabled: true }],
        role: [{ value: instructor.role }],
        privileges: [{ value: defaultPrivileges }],
        tunePermissions: this.fb.group({
          permissionsForCourse: this.fb.group(instructor.privileges.courseLevel),
          tuneSectionGroupPermissions: this.fb.array([]),
        }),
      });

      // Listen for specific course value changes
      const courseLevel: FormGroup = (instructorForm.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      courseLevel.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToView: boolean) => {
        if (!isAbleToView) {
          courseLevel.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      courseLevel.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
        if (isAbleToModify) {
          courseLevel.controls.canviewsessioninsection.setValue(true);
        }
      });

      (instructorForm.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions =
          this.initSectionGroupPermissions(instructor);

      // Listen for changes to custom privileges
      const roleControl: (AbstractControl | null) = instructorForm.get('role');
      const permissionsControl: (FormGroup | null) = instructorForm.get('tunePermissions') as FormGroup;

      if (roleControl != null && permissionsControl != null) {
        roleControl.valueChanges.subscribe((selectedRole: string) => {
          const panelId: string = `tune-permissions-${index}`;
          const panel: (HTMLElement | null) = document.getElementById(panelId);

          if (selectedRole === 'Custom' && panel != null) {
            panel.style.display = 'block';
            permissionsControl.controls.permissionsForCourse.reset(this.instructorList[index].privileges.courseLevel);
            permissionsControl.controls.tuneSectionGroupPermissions =
                this.initSectionGroupPermissions(this.instructorList[index]);
          } else if (panel != null) {
            panel.style.display = 'none';
          }
        });
      }

      control.push(instructorForm);
    });

    this.formEditInstructors.controls.formInstructors = control;
  }

  /**
   * Initialises section permissions for section group panels.
   */
  private initSectionGroupPermissions(instructor: InstructorAttributes): FormArray {
    const tuneSectionGroupPermissions: FormArray = this.fb.array([]);

    // Initialise section level privileges for each section group
    Object.keys(instructor.privileges.sectionLevel).forEach((sectionName: string) => {
      const sectionPrivileges: { [key: string]: SectionLevelPrivileges } = instructor.privileges.sectionLevel;
      const sectionPrivilegesForSection: SectionLevelPrivileges = sectionPrivileges[sectionName];
      const specialSectionPermissions: FormGroup = this.fb.group({
        permissionsForSection: this.fb.group(sectionPrivilegesForSection),
        permissionsForSessions: this.fb.group({}),
      });

      specialSectionPermissions.addControl(sectionName, this.fb.control(true));

      // Initialise remaining controls for non-special sections
      this.sectionNames.forEach((section: string) => {
        if (section !== sectionName) {
          specialSectionPermissions.addControl(section, this.fb.control(false));
        }
      });

      // Listen for specific section value changes
      const sectionLevel: FormGroup = specialSectionPermissions.controls.permissionsForSection as FormGroup;

      sectionLevel.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToView: boolean) => {
        if (!isAbleToView) {
          sectionLevel.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      sectionLevel.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
        if (isAbleToModify) {
          sectionLevel.controls.canviewsessioninsection.setValue(true);
        }
      });

      // Initialise session level privileges for each section
      const sessionPrivilegesForSection: { [session: string]: SessionLevelPrivileges } =
          instructor.privileges.sessionLevel[sectionName];

      this.feedbackNames.forEach((feedback: string) => {
        let sessionPrivileges: FormGroup;
        if (sessionPrivilegesForSection != null && sessionPrivilegesForSection[feedback] != null) {
          sessionPrivileges = this.fb.group(sessionPrivilegesForSection[feedback]);
        } else {
          sessionPrivileges = this.fb.group({
            cansubmitsessioninsection: false,
            canviewsessioninsection: false,
            canmodifysessioncommentinsection: false,
          });
        }

        // Listen for specific session value changes
        sessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
          if (!isAbleToSubmit) {
            sessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
          }
        });

        sessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
            .subscribe((isAbleToModify: boolean) => {
              if (isAbleToModify) {
                sessionPrivileges.controls.canviewsessioninsection.setValue(true);
              }
            });

        (specialSectionPermissions.controls.permissionsForSessions as FormGroup)
            .addControl(feedback, sessionPrivileges);
      });

      tuneSectionGroupPermissions.push(specialSectionPermissions);
    });

    return tuneSectionGroupPermissions;
  }

  /**
   * Checks if the current instructor has a valid google id.
   */
  hasGoogleId(index: number): boolean {
    const googleId: string = this.instructorList[index].googleId;
    return googleId != null && googleId !== '';
  }

  /**
   * Enables/disables editing the displayed instructor name if it is/is not displayed to other students.
   */
  onChangeIsDisplayedToStudents(evt: any, instr: FormGroup, index: number): void {
    const displayedNameControl: (AbstractControl | null) = instr.controls.displayedName;
    const nameDisplayId: string = `name-display-${index}`;
    const displayedNameField: (HTMLInputElement | null) = document.getElementById(nameDisplayId) as HTMLInputElement;

    const isDisplayedToStudents: boolean = evt.target.checked;
    if (displayedNameControl != null) {
      if (isDisplayedToStudents) {
        displayedNameControl.enable();
        displayedNameControl.setValue('Instructor');
        displayedNameField.placeholder = this.getPlaceholderForDisplayedName(true);
      } else {
        displayedNameControl.disable();
        displayedNameControl.setValue('');
        displayedNameField.placeholder = this.getPlaceholderForDisplayedName(false);
      }
    }
  }

  /**
   * Toggles the edit instructor panel for a given instructor.
   * Instructor email cannot be edited when editing a yet-to-join instructor.
   */
  toggleIsEditingInstructor(control: FormGroup, index: number): void {
    const editBtnId: string = `btn-edit-${index}`;
    const cancelBtnId: string = `btn-cancel-${index}`;
    const saveBtnId: string = `btn-save-${index}`;

    const editBtn: (HTMLElement | null) = document.getElementById(editBtnId);
    const cancelBtn: (HTMLElement | null) = document.getElementById(cancelBtnId);
    const saveBtn: (HTMLElement | null) = document.getElementById(saveBtnId);

    let isEditBtnVisible: boolean = true;
    if (editBtn != null) {
      isEditBtnVisible = editBtn.style.display === 'inline-block';
    }

    const viewRoleId: string = `role-view-${index}`;
    const editRoleId: string = `role-edit-${index}`;

    const viewRole: (HTMLElement | null) = document.getElementById(viewRoleId);
    const editRole: (HTMLElement | null) = document.getElementById(editRoleId);

    const idControl: (AbstractControl | null) = control.controls.googleId;
    const roleControl: (AbstractControl | null) = control.controls.role;
    const displayNameControl: (AbstractControl | null) = control.controls.displayedName;
    const nameDisplayId: string = `name-display-${index}`;
    const displayedNameField: (HTMLInputElement | null) = document.getElementById(nameDisplayId) as HTMLInputElement;

    const permissionsPanelId: string = `tune-permissions-${index}`;
    const permissionsPanel: (HTMLElement | null) = document.getElementById(permissionsPanelId);

    if (editBtn != null && cancelBtn != null && saveBtn != null && viewRole != null && editRole != null
        && idControl != null && roleControl != null && displayNameControl != null && displayedNameField != null
        && permissionsPanel != null) {

      // If the instructor is currently being edited
      if (isEditBtnVisible) {
        editBtn.style.display = 'none';
        cancelBtn.style.display = 'inline-block';
        saveBtn.style.display = 'inline-block';

        viewRole.style.display = 'none';
        editRole.style.display = 'block';

        // Enable all form control elements except for the google id and possibly the displayed name
        control.enable();
        idControl.disable();
        roleControl.setValue(this.instructorList[index].role);

        if (this.instructorList[index].role === 'Custom') {
          permissionsPanel.style.display = 'block';
        }

        if (!this.instructorList[index].isDisplayedToStudents) {
          displayNameControl.disable();
        }

      } else {
        editBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'none';
        saveBtn.style.display = 'none';

        viewRole.style.display = 'inline-block';
        editRole.style.display = 'none';

        control.disable();
        control.reset(this.instructorList[index]);
        permissionsPanel.style.display = 'none';

        if (!this.instructorList[index].isDisplayedToStudents) {
          displayNameControl.setValue('');
          displayedNameField.placeholder = this.getPlaceholderForDisplayedName(false);
        }
      }
    }

    // Disable editing email for yet-to-join instructor
    const email: string = 'email';
    const emailControl: (AbstractControl | null) = control.get(email);
    if (emailControl != null && !this.hasGoogleId(index)) {
      emailControl.disable();
    }
  }

  /**
   * Saves the updated instructor details.
   */
  onSubmitEditInstructor(instr: FormGroup, index: number): void {

    // Make a copy of the edited instructor
    const editedInstructor: InstructorAttributes =  {
      googleId: instr.controls.googleId.value,
      name: instr.controls.name.value,
      email: instr.controls.email.value,
      role: instr.controls.role.value,
      isDisplayedToStudents: instr.controls.isDisplayedToStudents.value,
      displayedName: instr.controls.displayedName.value,
      privileges: this.defaultPrivileges[instr.controls.role.value],
    };

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      instructorid: editedInstructor.googleId,
      instructorname: editedInstructor.name,
      instructoremail: editedInstructor.email,
      instructorrole: editedInstructor.role,
      instructordisplayname: editedInstructor.displayedName,
    };

    const instructorIsDisplayed: string = 'instructorisdisplayed';
    if (editedInstructor.isDisplayedToStudents) {
      paramsMap[instructorIsDisplayed] = 'true';
    }

    if (instr.controls.role.value === 'Custom') {
      const tuneCoursePermissions: (FormGroup | null) = (instr.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      // Append custom course level privileges
      Object.keys(tuneCoursePermissions.controls).forEach((permission: string) => {
        if (tuneCoursePermissions.controls[permission].value) {
          paramsMap[permission] = 'true';
        }
      });
      editedInstructor.privileges.courseLevel = tuneCoursePermissions.value;

      // Append custom section level privileges
      const tuneSectionGroupPermissions: (FormArray | null) = (instr.controls.tunePermissions as FormGroup)
          .controls.tuneSectionGroupPermissions as FormArray;

      const newSectionLevelPrivileges: { [key: string]: SectionLevelPrivileges } = {};
      const newSessionLevelPrivileges: { [section: string]: { [session: string]: SessionLevelPrivileges } } = {};

      tuneSectionGroupPermissions.controls.forEach((sectionGroupPermissions: AbstractControl, panelIdx: number) => {
        const specialSections: string[] = [];

        // Mark section as special if it has been checked in a section group
        this.sectionNames.forEach((section: string, sectionIdx: number) => {
          if ((sectionGroupPermissions as FormGroup).controls[section].value) {
            paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
            paramsMap[`sectiongroup${panelIdx}section${sectionIdx}`] = section;
            specialSections.push(section);
          }
        });

        // Include section permissions for a section group
        const permissionsInSection: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSection as FormGroup;
        Object.keys(permissionsInSection.controls).forEach((permission: string) => {
          if (permissionsInSection.controls[permission].value) {
            paramsMap[`${permission}sectiongroup${panelIdx}`] = 'true';
          }
        });

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSectionLevelPrivileges[section] = permissionsInSection.value;
        });

        // Append custom session level privileges
        const permissionsForSessions: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSessions as FormGroup;
        const specialSessionsAndSessionPermissions: { [session: string]: SessionLevelPrivileges } = {};

        // Mark session as special if a session has different permissions from the section permissions
        const sectionLevelSessionPrivileges: SessionLevelPrivileges = {
          canviewsessioninsection: permissionsInSection.controls.canviewsessioninsection.value,
          cansubmitsessioninsection: permissionsInSection.controls.cansubmitsessioninsection.value,
          canmodifysessioncommentinsection: permissionsInSection.controls.canmodifysessioncommentinsection.value,
        };

        this.feedbackNames.forEach((feedback: string) => {
          const permissionsForSession: (FormGroup | null) = permissionsForSessions.controls[feedback] as FormGroup;
          if (permissionsForSession.value !== sectionLevelSessionPrivileges) {
            Object.keys(permissionsForSession.controls).forEach((permission: string) => {
              if (permissionsForSession.controls[permission].value) {
                paramsMap[`${permission}sectiongroup${panelIdx}feedback${feedback}`] = 'true';
              }
            });
            specialSessionsAndSessionPermissions[feedback] = permissionsForSession.value;
          }
        });

        if (Object.keys(specialSessionsAndSessionPermissions).length > 0) {
          paramsMap[`issectiongroup${panelIdx}sessionsset`] = 'true';
        }

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSessionLevelPrivileges[section] = specialSessionsAndSessionPermissions;
        });
      });

      editedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
      editedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
    }

    this.httpRequestService.post('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.updateInstructorDetails(index, editedInstructor);
          this.toggleIsEditingInstructor(instr, index);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Updates the stored instructor and instructor list entities.
   */
  updateInstructorDetails(index: number, instr: InstructorAttributes): void {
    const newPrivileges: Privileges = instr.privileges;

    // Update the stored instructor
    if (this.instructorList.length === 1) {
      // If there is only one instructor, the instructor can modify instructors by default
      newPrivileges.courseLevel.canmodifyinstructor = true;
      this.instructor = instr;
    }

    // Update elements for privileges if needed
    if (this.instructor.googleId === instr.googleId) {
      this.updateElementsForPrivileges();
    }

    // Update the stored instructor list
    this.instructorList[index] = instr;
  }

  /**
   * Updates elements and buttons related to the current instructor's privileges.
   */
  private updateElementsForPrivileges(): void {
    const courseBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-course');
    for (const courseBtn of courseBtns as any) {
      (courseBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifycourse;
    }

    const instrBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-instr');
    for (const instrBtn of instrBtns as any) {
      (instrBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifyinstructor;
    }
  }

  /**
   * Toggles the add instructor form.
   */
  toggleIsAddingInstructor(): void {
    this.isAddingInstructor = !this.isAddingInstructor;

    if (this.isAddingInstructor) {
      this.initAddInstructorForm();
    }
  }

  /******************************************************************************
   * ADD INSTRUCTOR RELATED FUNCTIONS
   ******************************************************************************/

  /**
   * Initialises a new form for adding an instructor to the current course.
   */
  private initAddInstructorForm(): void {
    this.formAddInstructor = this.fb.group({
      googleId: [''],
      name: [''],
      email: [''],
      isDisplayedToStudents: [{ value: true }],
      displayedName: ['Instructor'],
      role: ['Co-owner'],
      privileges: this.defaultPrivileges.coowner,
      tunePermissions: this.fb.group({
        permissionsForCourse: this.fb.group({
          canmodifycourse: true,
          canmodifyinstructor: true,
          canmodifysession: true,
          canmodifystudent: true,
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        }),
        tuneSectionGroupPermissions: this.fb.array([]),
      }),
    });

    // Listen for changes to custom privileges
    const roleControl: (AbstractControl | null) = this.formAddInstructor.get('role');
    const permissionsControl: (FormGroup | null) = this.formAddInstructor.get('tunePermissions') as FormGroup;

    if (roleControl != null && permissionsControl != null) {
      roleControl.valueChanges.subscribe((selectedRole: string) => {
        const panelId: string = `tune-permissions-${this.instructorList.length}`;
        const panel: (HTMLElement | null) = document.getElementById(panelId);

        if (selectedRole === 'Custom' && panel != null) {
          panel.style.display = 'block';
          permissionsControl.controls.permissionsForCourse.reset();
          permissionsControl.controls.tuneSectionGroupPermissions = this.fb.array([]);
        } else if (panel != null) {
          panel.style.display = 'none';
        }
      });
    }
  }

  /**
   * Adds a new instructor to the current course.
   */
  onSubmitAddInstructor(formAddInstructor: FormGroup): void {
    // Create a copy of the added instructor
    const addedInstructor: InstructorAttributes = {
      googleId: formAddInstructor.controls.googleId.value,
      name: formAddInstructor.controls.name.value,
      email: formAddInstructor.controls.email.value,
      role: formAddInstructor.controls.role.value,
      isDisplayedToStudents: formAddInstructor.controls.isDisplayedToStudents.value,
      displayedName: formAddInstructor.controls.displayedName.value,
      privileges: this.defaultPrivileges[formAddInstructor.controls.role.value],
    };

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      instructorname: addedInstructor.name,
      instructoremail: addedInstructor.email,
      instructorrole: addedInstructor.role,
      instructordisplayname: addedInstructor.displayedName,
    };

    const instructorIsDisplayed: string = 'instructorisdisplayed';
    if (addedInstructor.isDisplayedToStudents) {
      paramsMap[instructorIsDisplayed] = 'true';
    }

    if (formAddInstructor.controls.role.value === 'Custom') {
      const tuneCoursePermissions: (FormGroup | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      // Append custom course level privileges
      Object.keys(tuneCoursePermissions.controls).forEach((permission: string) => {
        if (tuneCoursePermissions.controls[permission].value) {
          paramsMap[permission] = 'true';
        }
      });
      addedInstructor.privileges.courseLevel = tuneCoursePermissions.value;

      // Append custom section level privileges
      const tuneSectionGroupPermissions: (FormArray | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.tuneSectionGroupPermissions as FormArray;

      const newSectionLevelPrivileges: { [key: string]: SectionLevelPrivileges } = {};
      const newSessionLevelPrivileges: { [section: string]: { [session: string]: SessionLevelPrivileges } } = {};

      tuneSectionGroupPermissions.controls.forEach((sectionGroupPermissions: AbstractControl, panelIdx: number) => {
        const specialSections: string[] = [];

        // Mark section as special if it has been checked in a section group
        this.sectionNames.forEach((section: string, sectionIdx: number) => {
          if ((sectionGroupPermissions as FormGroup).controls[section].value) {
            paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
            paramsMap[`sectiongroup${panelIdx}section${sectionIdx}`] = section;
            specialSections.push(section);
          }
        });

        // Include section permissions for a section group
        const permissionsInSection: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSection as FormGroup;
        Object.keys(permissionsInSection.controls).forEach((permission: string) => {
          if (permissionsInSection.controls[permission].value) {
            paramsMap[`${permission}sectiongroup${panelIdx}`] = 'true';
          }
        });

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSectionLevelPrivileges[section] = permissionsInSection.value;
        });

        // Append custom session level privileges
        const permissionsForSessions: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSessions as FormGroup;
        const specialSessionsAndSessionPermissions: { [session: string]: SessionLevelPrivileges } = {};

        // Mark session as special if a session has different permissions from the section permissions
        const sectionLevelSessionPrivileges: SessionLevelPrivileges = {
          canviewsessioninsection: permissionsInSection.controls.canviewsessioninsection.value,
          cansubmitsessioninsection: permissionsInSection.controls.cansubmitsessioninsection.value,
          canmodifysessioncommentinsection: permissionsInSection.controls.canmodifysessioncommentinsection.value,
        };

        this.feedbackNames.forEach((feedback: string) => {
          const permissionsForSession: (FormGroup | null) = permissionsForSessions.controls[feedback] as FormGroup;
          if (permissionsForSession.value !== sectionLevelSessionPrivileges) {
            Object.keys(permissionsForSession.controls).forEach((permission: string) => {
              if (permissionsForSession.controls[permission].value) {
                paramsMap[`${permission}sectiongroup${panelIdx}feedback${feedback}`] = 'true';
              }
            });
            specialSessionsAndSessionPermissions[feedback] = permissionsForSession.value;
          }
        });

        if (Object.keys(specialSessionsAndSessionPermissions).length > 0) {
          paramsMap[`issectiongroup${panelIdx}sessionsset`] = 'true';
        }

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSessionLevelPrivileges[section] = specialSessionsAndSessionPermissions;
        });
      });

      addedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
      addedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
    }

    this.httpRequestService.put('/instructors/course/details/addInstructor', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.addToInstructorList(addedInstructor);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });

    this.toggleIsAddingInstructor();
  }

  /**
   * Updates the stored instructor list and forms.
   */
  private addToInstructorList(instructor: InstructorAttributes): void {
    this.instructorList.push(instructor);
    this.initEditInstructorsForm();
  }

  /**
   * Adds an additional panel to modify custom section privileges for a given instructor.
   */
  addTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    const instructor: InstructorAttributes = this.instructorList[index];
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: instructor.privileges.courseLevel.canviewstudentinsection,
        cansubmitsessioninsection: instructor.privileges.courseLevel.cansubmitsessioninsection,
        canviewsessioninsection: instructor.privileges.courseLevel.canviewsessioninsection,
        canmodifysessioncommentinsection: instructor.privileges.courseLevel.canmodifysessioncommentinsection,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Adds a default tune section group permission panel.
   */
  addEmptyTuneSectionGroupPermissionsPanel(instr: FormGroup): void {
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: false,
        cansubmitsessioninsection: false,
        canviewsessioninsection: false,
        canmodifysessioncommentinsection: false,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Removes a panel to modify custom section privileges for a given instructor.
   */
  removeTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).removeAt(index);
  }

  /**
   * Hides session level permissions for a section panel.
   */
  hideSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'none';
      hideLink.style.display = 'none';
      showLink.style.display = 'block';
    }
  }

  /**
   * Shows session level permissions for a section panel.
   */
  showSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'block';
      hideLink.style.display = 'block';
      showLink.style.display = 'none';
    }
  }

  /******************************************************************************
   * MODAL RELATED FUNCTIONS
   ******************************************************************************/

  /**
   * Opens a modal to show the privileges for a given instructor.
   */
  viewPrivilegesHandler(role: Role, privileges: Privileges): void {
    const modalRef: NgbModalRef = this.modalService.open(ViewPrivilegesModalComponent);

    modalRef.componentInstance.model = privileges.courseLevel;
    modalRef.componentInstance.instructorrole = role;
  }

  /**
   * Opens a modal to confirm resending an invitation email to an instructor.
   */
  resendReminderHandler(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(ResendReminderModalComponent);

    const instructorToResend: InstructorAttributes = this.instructorList[index];
    modalRef.componentInstance.instructorname = instructorToResend.name;
    modalRef.componentInstance.courseId = this.courseEditFormModel.courseId;

    modalRef.result.then(() => {
      const paramsMap: { [key: string]: string } = {
        courseid: this.courseEditFormModel.courseId,
        instructoremail: instructorToResend.email,
      };

      this.httpRequestService.post('/instructors/course/details/sendReminders', paramsMap)
          .subscribe((resp: MessageOutput) => {
            this.statusMessageService.showSuccessMessage(resp.message);
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(resp.error.message);
          });
    }, () => {});
  }

  /**
   * Opens a modal to confirm deleting an instructor.
   */
  deleteInstructorHandler(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(DeleteInstructorModalComponent);

    const instructorToDelete: InstructorAttributes = this.instructorList[index];
    modalRef.componentInstance.courseId = this.courseEditFormModel.courseId;
    modalRef.componentInstance.idToDelete = instructorToDelete.googleId;
    modalRef.componentInstance.nameToDelete = instructorToDelete.name;
    modalRef.componentInstance.currentId = this.instructor.googleId;

    modalRef.result.then(() => {
      const paramsMap: { [key: string]: string } = {
        courseid: this.courseEditFormModel.courseId,
        instructorid: this.instructor.googleId,
        instructoremail: instructorToDelete.email,
      };

      this.httpRequestService.delete('/instructors/course/details/deleteInstructor', paramsMap)
          .subscribe((resp: MessageOutput) => {
            if (instructorToDelete.googleId === this.instructor.googleId) {
              this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
            } else {
              this.removeFromInstructorList(index);
              this.statusMessageService.showSuccessMessage(resp.message);
            }
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(resp.error.message);
          });
    }, () => {});
  }

  /**
   * Removes a deleted instructor from the stored instructor lists.
   */
  private removeFromInstructorList(index: number): void {
    this.instructorList.splice(index, 1);
    (this.formEditInstructors.controls.formInstructors as FormArray).removeAt(index);
  }
}
