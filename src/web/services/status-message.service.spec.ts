import { TestBed } from '@angular/core/testing';

import { StatusMessageService } from './status-message.service';

describe('StatusMessageService', () => {
  it('should be created', () => {
    const service: StatusMessageService = TestBed.get(StatusMessageService);
    expect(service).toBeTruthy();
  });
});
