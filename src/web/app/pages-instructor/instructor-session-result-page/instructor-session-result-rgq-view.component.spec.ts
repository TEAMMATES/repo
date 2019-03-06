import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';

describe('InstructorSessionResultRgqViewComponent', () => {
  let component: InstructorSessionResultRgqViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultRgqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultRgqViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultRgqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
