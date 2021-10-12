import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';

import { VisibilityPanelComponent } from './visibility-panel.component';

describe('VisibilityPanelComponent', () => {
  let component: VisibilityPanelComponent;
  let fixture: ComponentFixture<VisibilityPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        VisibilityPanelComponent,
      ],
      imports: [
        NgbDropdownModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        VisibilityMessagesModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisibilityPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
