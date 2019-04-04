import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { McqFieldComponent } from './mcq-field.component';

describe('McqFieldComponent', () => {
  let component: McqFieldComponent;
  let fixture: ComponentFixture<McqFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqFieldComponent],
      imports: [
        FormsModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
