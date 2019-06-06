import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ErrorReportComponent } from './error-report.component';
import { HttpRequestService } from "../../../services/http-request.service";
import { of, throwError } from "rxjs";

describe('ErrorReportComponent', () => {
  let component: ErrorReportComponent;
  let fixture: ComponentFixture<ErrorReportComponent>;
  let httpRequestService: HttpRequestService;

  beforeEach(async(() => {

    TestBed.configureTestingModule({
      declarations: [ErrorReportComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
      ],
      providers: [HttpRequestService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorReportComponent);
    component = fixture.componentInstance;
    httpRequestService = TestBed.get(HttpRequestService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have "Something went wrong" header', () => {
    const h2 = fixture.nativeElement.querySelector('h2');
    if (h2) {
      expect(h2.textContent).toContain('Something went wrong');
    }
  });

  it('should display error message', () => {
    const error = "Error message sample";
    component.errorMessage = error;

    fixture.detectChanges();

    const p: any = fixture.nativeElement.querySelector('p');
    if (p) {
      expect(p.textContent).toContain(error);
    }
  });

  it('should have the requestID form disabled ', () => {
    const input = fixture.nativeElement.querySelector('input');
    expect(input.disabled).toBeTruthy();
  });

  it('should have pre-filled Subject input form', () => {
    const input: any = fixture.nativeElement.querySelectorAll('input');

    expect(component.subject).toEqual('User-submitted Error Report');
    expect(input[1].value).toEqual(component.subject);
  });

  it('should get user input from Subject form', () => {
    const input: any = fixture.nativeElement.querySelectorAll('input');

    expect(component.subject).toEqual('User-submitted Error Report');
    input[1].value = 'testInput';
    input[1].dispatchEvent((new Event('input')));
    expect(component.subject).toEqual('testInput');
  });

  it('should disable send report button and set errorReportSubmitted to true after successfully sending report', () => {
    const button: any = fixture.nativeElement.querySelector('button');

    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();

    spyOn(httpRequestService, 'post').and.returnValue(of(''));
    button.click();

    expect(component.sendButtonEnabled).toBeFalsy();
    expect(component.errorReportSubmitted).toBeTruthy();
  });

  it('should not disable send report button and leave errorReportSubmitted as false after unsuccessfully sending report', () => {
    const button: any = fixture.nativeElement.querySelector('button');

    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();

    spyOn(httpRequestService, 'post').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
  }));
    button.click();

    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();
  });


  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some content', () => {
    component.content = 'some content';

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with disabled send report button after sending report', () => {

    component.sendButtonEnabled = false;
    component.errorReportSubmitted = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

});
