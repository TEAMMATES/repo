import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-endpoints';
import { InstructorPrivilege, Instructors } from '../types/api-output';
import { Intent } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles instructor related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class InstructorService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Get a list of instructors of a course by calling API.
   */
  getInstructorsFromCourse(courseId: string, intent?: Intent): Observable<Instructors> {

    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };

    if (intent) {
      paramMap[intent] = intent;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS, paramMap);
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadInstructorPrivilege(courseId: string, sectionName?: string, feedbackSessionName?: string):
    Observable<InstructorPrivilege> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    if (sectionName) {
      paramMap.sectionname = sectionName;
    }

    if (feedbackSessionName) {
      paramMap.fsname = feedbackSessionName;
    }

    return this.httpRequestService.get('/instructor/privilege', paramMap);
  }
}
