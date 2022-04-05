import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import moment from 'moment-timezone';
import { TimezoneService } from '../../../../services/timezone.service';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { EXAMPLE_NOTIFICATION_ONE, EXAMPLE_NOTIFICATION_TWO } from '../admin-notifications-page-data';
import { AdminNotificationsPageModule } from '../admin-notifications-page.module';
import { NotificationsTableRowModel } from './notifications-table-model';

import { NotificationsTableComponent } from './notifications-table.component';

describe('NotificationsTableComponent', () => {
  let component: NotificationsTableComponent;
  let fixture: ComponentFixture<NotificationsTableComponent>;
  let timezoneService: TimezoneService;

  const notificationTableRowModel1: NotificationsTableRowModel = {
    isHighlighted: true,
    notification: EXAMPLE_NOTIFICATION_ONE,
  };
  const notificationTableRowModel2: NotificationsTableRowModel = {
    isHighlighted: false,
    notification: EXAMPLE_NOTIFICATION_TWO,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AdminNotificationsPageModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsTableComponent);
    component = fixture.componentInstance;
    timezoneService = TestBed.inject(TimezoneService);
    jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    moment.tz.setDefault('SGT');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in notification page with 2 notifications sorted by create time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
    component.notificationsTableRowModelsSortOrder = SortOrder.DESC;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap like in notification page with 2 notifications sorted by start time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_START_TIME;
    component.notificationsTableRowModelsSortOrder = SortOrder.DESC;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
