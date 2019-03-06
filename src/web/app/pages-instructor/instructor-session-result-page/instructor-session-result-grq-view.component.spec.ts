import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';

describe('InstructorSessionResultGrqViewComponent', () => {
  let component: InstructorSessionResultGrqViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultGrqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultGrqViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultGrqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
