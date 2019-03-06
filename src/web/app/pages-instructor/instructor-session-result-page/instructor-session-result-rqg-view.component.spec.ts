import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';

describe('InstructorSessionResultRqgViewComponent', () => {
  let component: InstructorSessionResultRqgViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultRqgViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultRqgViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultRqgViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
