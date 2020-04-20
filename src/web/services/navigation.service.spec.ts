import { TestBed } from '@angular/core/testing';

import { MatSnackBarModule } from '@angular/material';
import { NavigationService } from './navigation.service';

describe('NavigationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      MatSnackBarModule,
    ],
  }));

  it('should be created', () => {
    const service: NavigationService = TestBed.get(NavigationService);
    expect(service).toBeTruthy();
  });

  it('should return an encoded param string upon calling encodeParams', () => {
    const service: NavigationService = TestBed.get(NavigationService);
    expect(service.encodeParams({ courseId: '#123?123' })).toEqual('?courseId=%23123%3F123');
  });

  it('filterEmptyParams should filter empty params', () => {
    const service: NavigationService = TestBed.get(NavigationService);
    const params = { courseId: '#123?123', filterThis: '' };
    service.filterEmptyParams(params)
    expect(Object.keys(params).length).toEqual(1);
  })
});
