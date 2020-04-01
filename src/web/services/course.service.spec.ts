import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-endpoints';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { CourseArchiveRequest, CourseCreateRequest } from "../types/api-request";

describe('CourseService', () => {
  let spyHttpRequestService: any;
  let service: CourseService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.get(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on courses endpoint with course status as an instructor', () => {
    const courseStatus: string = 'active';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      coursestatus: courseStatus,
    };
    service.getAllCoursesAsInstructor(courseStatus);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute GET on course endpoint with course id as an instructor', () => {
    const courseId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    service.getCourseAsInstructor(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should execute GET when getting all courses as student', () => {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
    };
    service.getAllCoursesAsStudent();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute GET when getting specific course as student', () => {
    const courseId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
      courseid: courseId,
    };
    service.getCourseAsStudent(courseId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should GET student courses data of a given google id in masquerade mode', () => {
    const googleId: string = 'test-id';
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
      user: googleId,
    };
    service.getStudentCoursesInMasqueradeMode(googleId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should GET instructor courses data of a given google id in masquerade mode', () => {
    const googleId: string = 'test-id';
    const activeCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'active',
      entitytype: 'instructor',
      user: googleId,
    };
    const archivedCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'archived',
      entitytype: 'instructor',
      user: googleId,
    };
    service.getInstructorCoursesInMasqueradeMode(googleId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, activeCoursesParamMap);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, archivedCoursesParamMap);
  });

  it('should execute GET when getting all active instructor courses', () => {
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      coursestatus: 'active',
    };
    service.getInstructorCoursesThatAreActive();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSES, paramMap);
  });

  it('should execute POST to create course', () => {
    const request: CourseCreateRequest = new class implements CourseCreateRequest {
      courseId: string = 'test-id';
      courseName: string = 'test-name';
      timeZone: string = 'test-zone';
    };
    const paramMap: { [key: string]: string } = {};
    service.createCourse(request);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap, request);
  });

  it('should execute PUT to update course', () => {
    const courseid: string = 'test-id';
    const request: CourseCreateRequest = new class implements CourseCreateRequest {
      courseId: string = courseid;
      courseName: string = 'test-name';
      timeZone: string = 'test-zone';
    };
    const paramMap: { [key: string]: string } = { courseid };
    service.updateCourse(courseid, request);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap, request);
  });

  it('should execute DELETE to delete course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.deleteCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.COURSE, paramMap);
  });

  it('should execute PUT to archive course', () => {
    const courseid: string = 'test-id';
    const request: CourseArchiveRequest = new class implements CourseArchiveRequest {
      archiveStatus: boolean = true;
      courseId: string = courseid;
      courseName: string = 'test-name';
      timeZone: string = 'test-zone';
    };
    const paramMap: { [key: string]: string } = { courseid };
    service.changeArchiveStatus(courseid, request);
    expect(spyHttpRequestService.put)
        .toHaveBeenCalledWith(ResourceEndpoints.COURSE_ARCHIVE, paramMap, request);
  });

  it('should execute PUT to bin course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.binCourse(courseid);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute DELETE to restore course', () => {
    const courseid: string = 'test-id';
    const paramMap: { [key: string]: string } = { courseid };
    service.restoreCourse(courseid);
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.BIN_COURSE, paramMap);
  });

  it('should execute GET to retrieve join course status', () => {
    const regKey = 'ABC';
    const entityType = 'instructor';
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
    };
    service.getJoinCourseStatus(regKey, entityType);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });

  it('should execute PUT when joining course', () => {
    const paramMap: { [key: string]: string } = {
      key: '123',
      entitytype: 'instructor',
      instructorinstitution: 'National University of Singapore',
      mac: 'ABC123',
    };
    service.joinCourse(paramMap.key, paramMap.entitytype, paramMap.instructorinstitution, paramMap.mac);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.JOIN, paramMap);
  });

  it('should execute GET when getting course section names', () => {
    const paramMap: { [key: string]: string } = {
      courseid: 'CS3281',
    };
    service.getCourseSectionNames(paramMap.courseid);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE_SECTIONS, paramMap);
  });

  it('should execute GET when getting students enrolled in course', () => {
    const paramMap: { [key: string]: string } = {
      courseid: 'CS3281',
    };
    service.getStudentsEnrolledInCourse({
      courseId: paramMap.courseid,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.COURSE_ENROLL_STUDENTS, paramMap);
  });
});
