import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ReCaptcha2Component } from 'ngx-captcha';
import { finalize } from 'rxjs/operators';
import { FormValidator } from 'src/web/types/form-validator';
import { environment } from '../../../../environments/environment';
import { AccountService } from '../../../../services/account.service';
import { NavigationService } from '../../../../services/navigation.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { JoinLink } from '../../../../types/api-output';
import { ErrorMessageOutput } from '../../../error-message-output';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-process-account-request-panel',
  templateUrl: './process-account-request-panel.component.html',
  styleUrls: ['./process-account-request-panel.component.scss'],
})
export class ProcessAccountRequestPanelComponent implements OnInit {

  FormValidator: typeof FormValidator = FormValidator; // enum
  form!: FormGroup;

  backendErrorMessage : string = '';
  readonly emptyFieldMessage : string = 'This field should not be empty';

  readonly recaptchaSiteKey: string = environment.captchaSiteKey;

  isFormSaving: boolean = false;
  isEditing: boolean = false;

  exampleName: string = 'Example Name';
  exampleInstitute: string = 'National University of Singapore, Singapore';
  exampleEmail: string = 'example_email@u.nus.edu';
  exampleHomePageURL: string = 'https://www.comp.nus.edu.sg/cs/people/example-name';
  exampleOtherComments: string = 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna.';

  @ViewChild('recaptchaElem') recaptchaElem!: ReCaptcha2Component;

  constructor(private statusMessageService: StatusMessageService,
              private accountService: AccountService,
              private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.form = new FormGroup({
      name: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.PERSON_NAME_MAX_LENGTH)]),
      institute: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.SECTION_NAME_MAX_LENGTH)]),
      country: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.TEAM_NAME_MAX_LENGTH)]),
      email: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.EMAIL_MAX_LENGTH), Validators.email]),
      url: new FormControl(''),
      'account-type': new FormControl('instructor',
        [Validators.required, this.fieldExpectedValueValidator('instructor')]),
      comments: new FormControl(''),
      recaptcha: new FormControl(''),
    });
  }

  /**
   * Returns a validator function to check if the form control field has the expected value.
   */
  fieldExpectedValueValidator(expected: string) {
    return function (control: AbstractControl) : ValidationErrors | null {
      if (control.value !== expected) {
        return { notExpected: true };
      }
      return null;
    }
  }

  /**
   * Displays message to user stating that the field exceeds the max length.
   */
  displayExceedMaxLengthMessage(fieldName: string, maxLength: number): string {
    return `${fieldName} should not exceed ${maxLength} characters`;
  }

  editAccountRequest(): void {
    this.isEditing = !this.isEditing;
  }

  /**
   * Submits the account request form.
   */
  onSubmit(): void {
    this.form.markAllAsTouched();

    // set recaptcha validation errors only on submit
    console.log(this.recaptchaElem.getResponse());
    console.log(this.recaptcha!.value);
    if (this.recaptchaSiteKey !== '' && this.recaptchaElem.getResponse() === '') {
      this.recaptcha!.setErrors({
        unchecked: true,
      });
    }

    if (!this.form.valid) {
      console.log('invalid form');
      this.statusMessageService.showErrorToast('Ensure the form is valid before submission.');
      return;
    }

    this.isFormSaving = true;
    this.backendErrorMessage = '';

    this.accountService.createAccountRequest({
      instructorName: this.name!.value,
      instructorInstitute: this.institute!.value,
      instructorCountry: this.country!.value,
      instructorEmail: this.email!.value,
      instructorHomePageUrl: this.url!.value,
      otherComments: this.comments!.value,
    })
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe((resp: JoinLink) => { // TODO: change to MessageOutput and resp.message
        this.navigationService.navigateWithSuccessMessage('/web/front/home', resp.joinLink);
      }, (resp: ErrorMessageOutput) => {


        this.backendErrorMessage = resp.error.message;

        this.form.setErrors({
          invalidFields : resp.error.message,
        });
        // const errors = JSON.parse(resp.error.message);
        // if (errors.name) {
        //   this.name!.setErrors({
        //     invalidField : errors.name,
        //   })
        // }
        // if (errors.institute) {
        //   this.institute!.setErrors({
        //     invalidField : errors.institute,
        //   })
        // }
        // if (errors.country) {
        //   this.country!.setErrors({
        //     invalidField : errors.country,
        //   })
        // }
        // if (errors.email) {
        //   this.email!.setErrors({
        //     invalidField : errors.email,
        //   })
        // }
      });

    // this.studentService.updateStudent({
    //   courseId: '',
    //   studentEmail: '',
    //   requestBody: reqBody,
    // })
    //   .pipe(finalize(() => {
    //     this.isFormSaving = false;
    //   }))
    //   .subscribe((resp: MessageOutput) => {
    //     this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details',
    //       resp.message, { courseid: '' });
    //   }, (resp: ErrorMessageOutput) => {
    //     this.statusMessageService.showErrorToast(resp.error.message);
    //   });
  }

  get name() {
    return this.form.get('name');
  }

  get institute() {
    return this.form.get('institute');
  }

  get country() {
    return this.form.get('country');
  }

  get email() {
    return this.form.get('email');
  }

  get url() {
    return this.form.get('url');
  }

  get accountType() {
    return this.form.get('account-type');
  }

  get comments() {
    return this.form.get('comments');
  }

  get recaptcha() {
    return this.form.get('recaptcha');
  }
}
