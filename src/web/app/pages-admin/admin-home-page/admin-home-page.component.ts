import { Component, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { catchError, finalize, map, mergeMap } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { CourseService } from '../../../services/course.service';
import { LinkService } from '../../../services/link.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  Account, AccountRequest,
  Accounts,
  Courses, JoinLink,
} from '../../../types/api-output';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { AccountRequestCreateErrorResultsWrapper, ErrorMessageOutput } from '../../error-message-output';
import { InstructorData, RegisteredInstructorAccountData } from './instructor-data';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
})
export class AdminHomePageComponent {

  instructorDetails: string = '';
  instructorName: string = '';
  instructorEmail: string = '';
  instructorInstitution: string = '';

  instructorsConsolidated: InstructorData[] = [];
  activeRequests: number = 0;

  isAddingInstructors: boolean = false;

  isExistingAccountRequestModalLoading: boolean = false;
  existingAccountRequestIndex: number = 0;
  existingAccountRequest!: AccountRequest;
  registeredInstructorAccountData: RegisteredInstructorAccountData[] = [];

  @ViewChild('existingAccountRequestModal') existingAccountRequestModal!: TemplateRef<any>;

  constructor(
    private accountService: AccountService,
    private courseService: CourseService,
    private simpleModalService: SimpleModalService,
    private statusMessageService: StatusMessageService,
    private linkService: LinkService,
    private ngbModal: NgbModal,
  ) {}

  /**
   * Validates and adds the instructor details filled with first form.
   */
  validateAndAddInstructorDetails(): void {
    const invalidLines: string[] = [];
    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      const instructorDetailSplit: string[] = instructorDetail.split(/[|\t]/).map((item: string) => item.trim());
      if (instructorDetailSplit.length < 3) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      this.instructorsConsolidated.push({
        name: instructorDetailSplit[0],
        email: instructorDetailSplit[1],
        institution: instructorDetailSplit[2],
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      });
    }
    this.instructorDetails = invalidLines.join('\r\n');
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      // TODO handle error
      return;
    }
    this.instructorsConsolidated.push({
      name: this.instructorName,
      email: this.instructorEmail,
      institution: this.instructorInstitution,
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    });
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  /**
   * Adds the instructor at the i-th index.
   */
  addInstructor(i: number): void {
    const instructor: InstructorData = this.instructorsConsolidated[i];
    if (this.instructorsConsolidated[i].isCurrentlyBeingEdited
      || (instructor.status !== 'PENDING' && instructor.status !== 'FAIL')) {
      return;
    }
    this.activeRequests += 1;
    instructor.status = 'ADDING';

    this.isAddingInstructors = true;

    this.accountService.createAccountRequestAsAdmin({
      instructorName: instructor.name,
      instructorInstitute: instructor.institution, // final institute
      instructorCountry: '',
      instructorEmail: instructor.email,
      instructorHomePageUrl: '',
      comments: '',
    })
        .pipe(finalize(() => {
          this.isAddingInstructors = false;
        }))
        .subscribe((resp: JoinLink) => {
          instructor.status = 'SUCCESS';
          instructor.statusCode = 200;
          instructor.joinLink = resp.joinLink;
          this.activeRequests -= 1;
        }, (resp: ErrorMessageOutput | AccountRequestCreateErrorResultsWrapper) => {
          instructor.status = 'FAIL';
          instructor.statusCode = resp.status;
          if ('message' in resp.error) {
            // resp is ErrorMessageOutput
            instructor.message = resp.error.message;
          } else {
            // resp is AccountRequestCreateErrorResultsWrapper
            instructor.message = [resp.error.invalidNameMessage, resp.error.invalidEmailMessage,
              resp.error.invalidInstituteMessage]
              .filter(Boolean)
              .join(' ');
          }
          this.activeRequests -= 1;
        });
  }

  /**
   * Removes the instructor at the i-th index.
   */
  removeInstructor(i: number): void {
    this.instructorsConsolidated.splice(i, 1);
  }

  /**
   * Sets the i-th instructor data row's edit mode status.
   *
   * @param i The index.
   * @param isEnabled Whether the edit mode status is enabled.
   */
  setInstructorRowEditModeEnabled(i: number, isEnabled: boolean): void {
    this.instructorsConsolidated[i].isCurrentlyBeingEdited = isEnabled;
  }

  /**
   * Adds all the pending and failed-to-add instructors.
   */
  addAllInstructors(): void {
    for (let i: number = 0; i < this.instructorsConsolidated.length; i += 1) {
      this.addInstructor(i);
    }
  }

  /**
   * Opens a modal containing more information about an existing account request.
   */
  showExistingAccountRequestModal(i: number): void {
    this.isExistingAccountRequestModalLoading = true;
    this.existingAccountRequestIndex = i;
    this.registeredInstructorAccountData = [];

    const email = this.instructorsConsolidated[i].email;
    const institute = this.instructorsConsolidated[i].institution;

    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
      'An account request already exists',
      SimpleModalType.INFO,
      this.existingAccountRequestModal,
      undefined,
      { scrollable: true, size: 'lg', windowClass: 'process-account-request-modal-size' },
    );

    this.accountService.getAccountRequest(email, institute).subscribe((ar: AccountRequest) => {
      this.existingAccountRequest = ar;

      this.accountService.getAccounts(email).pipe(
        map((accounts: Accounts) => accounts.accounts),
        mergeMap((accounts: Account[]) =>
          forkJoin(accounts.map(
            (account: Account) => this.getRegisteredAccountData(account.googleId)),
          ),
        ),
        finalize(() => { this.isExistingAccountRequestModalLoading = false; }),
      ).subscribe((resp: RegisteredInstructorAccountData[]) => {
        this.registeredInstructorAccountData = resp;
      }, (resp: ErrorMessageOutput) => {
        modalRef.dismiss();
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, (resp: ErrorMessageOutput) => {
      modalRef.dismiss();
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  private getRegisteredAccountData(googleId: string): Observable<RegisteredInstructorAccountData> {
    const getStudentCourses: Observable<Courses> = this.courseService
      .getStudentCoursesInMasqueradeMode(googleId)
      .pipe(
        catchError((err: ErrorMessageOutput) => {
          if (err.status === 403) {
            // User is not a student
            return of({ courses: [] });
          }
          return throwError(err);
        }),
      );
    const getInstructorCourses: Observable<Courses> = this.courseService
      .getInstructorCoursesInMasqueradeMode(googleId)
      .pipe(
        catchError((err: ErrorMessageOutput) => {
          if (err.status === 403) {
            // User is not an instructor
            return of({ courses: [] });
          }
          return throwError(err);
        }),
      );

    return forkJoin([
      getStudentCourses,
      getInstructorCourses,
    ]).pipe(
      map((value: [Courses, Courses]) => {
        const manageAccountLink = this.linkService
          .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);
        return {
          googleId,
          manageAccountLink,
          studentCourses: value[0].courses,
          instructorCourses: value[1].courses,
        };
      }),
    );
  }

  // TODO: this method should be deleted
  resetAccountRequest(i: number): void {
    const modalContent = `Are you sure you want to reset the account request for
        <strong>${this.instructorsConsolidated[i].name}</strong> with email
        <strong>${this.instructorsConsolidated[i].email}</strong> from
        <strong>${this.instructorsConsolidated[i].institution}</strong>?
        An email with the account registration link will also be sent to the instructor.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Reset account request for <strong>${this.instructorsConsolidated[i].name}</strong>?`,
        SimpleModalType.WARNING,
        modalContent);

    modalRef.result.then(() => {
      this.accountService
        .resetAccountRequest(
          this.instructorsConsolidated[i].email,
          this.instructorsConsolidated[i].institution,
        )
        .subscribe(
          (resp: AccountRequest) => {
            this.instructorsConsolidated[i].status = 'SUCCESS';
            this.instructorsConsolidated[i].statusCode = 200;
            this.instructorsConsolidated[i].joinLink =
              this.linkService.generateAccountRegistrationLink(resp.registrationKey);
            this.ngbModal.dismissAll();
          },
          (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        );
    }, () => {});
  }

}
