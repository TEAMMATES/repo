import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UsermapPageComponent } from './usermap-page.component';

describe('UsermapPageComponent', () => {
  let component: UsermapPageComponent;
  let fixture: ComponentFixture<UsermapPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UsermapPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsermapPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
