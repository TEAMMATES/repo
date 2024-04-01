import { ComponentFixture, TestBed, fakeAsync, tick, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Observable, first } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { InstructorRequestFormComponent } from './instructor-request-form.component';
import { AccountService } from '../../../../services/account.service';
import { AccountCreateRequest } from '../../../../types/api-request';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;
  let accountService: AccountService;
  const typicalModel: InstructorRequestFormModel = {
    name: 'John Doe',
    institution: 'Example Institution',
    country: 'Example Country',
    email: 'jd@example.edu',
    comments: '',
  };
  const typicalCreateRequest: AccountCreateRequest = {
    instructorEmail: typicalModel.email,
    instructorName: typicalModel.name,
    instructorInstitution: `${typicalModel.institution}, ${typicalModel.country}`,
  };

  const accountServiceStub: Partial<AccountService> = {
    createAccountRequest: () => new Observable((subscriber) => {
        subscriber.next();
      }),
  };

  /**
   * Fills in form fields with the given data.
   *
   * @param data Data to fill form with.
   */
  function fillFormWith(data: InstructorRequestFormModel): void {
    component.name.setValue(data.name);
    component.institution.setValue(data.institution);
    component.country.setValue(data.country);
    component.email.setValue(data.email);
    component.comments.setValue(data.comments);
  }

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorRequestFormComponent],
      imports: [ReactiveFormsModule],
      providers: [{ provide: AccountService, useValue: accountServiceStub }],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorRequestFormComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correctly', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should emit requestSubmissionEvent once when submit button is clicked', () => {
    jest.spyOn(component.requestSubmissionEvent, 'emit');

    fillFormWith(typicalModel);
    const submitButton = fixture.debugElement.query(By.css('#submit-button'));
    submitButton.nativeElement.click();

    expect(component.requestSubmissionEvent.emit).toHaveBeenCalledTimes(1);
  });

  it('should emit requestSubmissionEvent with the correct data when form is submitted', fakeAsync(() => {
    jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      new Observable((subscriber) => { subscriber.next(); }));

    // Listen for emitted value
    let actualModel: InstructorRequestFormModel | null = null;
    component.requestSubmissionEvent.pipe(first())
        .subscribe((data: InstructorRequestFormModel) => { actualModel = data; });

    fillFormWith(typicalModel);
    component.onSubmit();
    tick(1000);

    expect(actualModel).toBeTruthy();
    expect(actualModel!.name).toBe(typicalModel.name);
    expect(actualModel!.institution).toBe(typicalModel.institution);
    expect(actualModel!.country).toBe(typicalModel.country);
    expect(actualModel!.email).toBe(typicalModel.email);
    expect(actualModel!.comments).toBe(typicalModel.comments);
  }));

  it('should send the correct request data when form is submitted', fakeAsync(() => {
    jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      new Observable((subscriber) => { subscriber.next(); }));

    fillFormWith(typicalModel);
    component.onSubmit();
    tick(1000);

    expect(accountService.createAccountRequest).toHaveBeenCalledTimes(1);
    expect(accountService.createAccountRequest).toHaveBeenCalledWith(expect.objectContaining(typicalCreateRequest));
  }));
});
