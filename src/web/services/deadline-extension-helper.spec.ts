import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../app/pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { Instructor, JoinState, Student } from '../types/api-output';
import { InstructorPermissionRole } from '../types/api-request';
import { DeadlineExtensionHelper, DeadlineHandlerType } from './deadline-extension-helper';

const timeNow = Date.now();
const fixedLengthOfTime = 1000000;

const ongoingExtension: Record<string, number> = { ongoingExtension1: timeNow + fixedLengthOfTime };
const notOngoingExtension1: Record<string, number> = { notOngoingExtension1: timeNow - fixedLengthOfTime };
const notOngoingExtension2: Record<string, number> = { notOngoingExtension2: timeNow };
const hasOngingDeadlines: Record<string, number> = {
    ...ongoingExtension, ...notOngoingExtension1, ...notOngoingExtension2,
};
const hasNoOngoingDeadlines: Record<string, number> = {
    ...notOngoingExtension1, ...notOngoingExtension2,
};

const student1: Student = {
  email: 'student1Model@example.com',
  courseId: 'course1',
  name: 'student1Model',
  teamName: '1',
  sectionName: '1',
};
const student1Model: StudentExtensionTableColumnModel = {
  sectionName: '1',
  teamName: '1',
  name: 'student1Model',
  email: 'student1Model@example.com',
  extensionDeadline: 0,
  hasExtension: true,
  isSelected: false,
};
const student2: Student = {
  email: 'student2Model@example.com',
  courseId: 'course1',
  name: 'student2Model',
  teamName: '2',
  sectionName: '1',
};
const student2Model: StudentExtensionTableColumnModel = {
  sectionName: '1',
  teamName: '2',
  name: 'student2Model',
  email: 'student2Model@example.com',
  extensionDeadline: 0,
  hasExtension: true,
  isSelected: false,
};
const instructor1: Instructor = {
  courseId: '1',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  email: 'instructor1Model@example.com',
  name: 'instructor1Model',
  joinState: JoinState.JOINED,
};
const instructor1ModelWithExtension: InstructorExtensionTableColumnModel = {
  name: 'instructor1Model',
  email: 'instructor1Model@example.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  extensionDeadline: 3,
  hasExtension: true,
  isSelected: false,
};
const instructor2: Instructor = {
  courseId: '1',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  email: 'instructor2Model@example.com',
  name: 'instructor2Model',
  joinState: JoinState.JOINED,
};
const instructor2ModelWithExtension: InstructorExtensionTableColumnModel = {
  name: 'instructor2Model',
  email: 'instructor2Model@example.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  extensionDeadline: 3,
  hasExtension: true,
  isSelected: false,
};

describe('DeadlineExtensionHelper', () => {
  beforeEach(() => jest.useFakeTimers().setSystemTime(timeNow));

  it('should detect ongoing extensions correctly', () => {
    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasOngingDeadlines,
        instructorDeadlines: hasNoOngoingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasNoOngoingDeadlines,
        instructorDeadlines: hasOngingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasOngingDeadlines,
        instructorDeadlines: hasOngingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasNoOngoingDeadlines,
        instructorDeadlines: {},
    })).toBeFalsy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: {},
        instructorDeadlines: hasNoOngoingDeadlines,
    })).toBeFalsy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasNoOngoingDeadlines,
        instructorDeadlines: hasNoOngoingDeadlines,
    })).toBeFalsy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: {},
        instructorDeadlines: {},
    })).toBeFalsy();
  });

  it('should detect extensions before given end time correctly', () => {
    expect(DeadlineExtensionHelper.isDeadlinesBeforeUpdatedEndTime(
      hasOngingDeadlines, hasNoOngoingDeadlines, timeNow)).toBeTruthy();
    expect(DeadlineExtensionHelper.isDeadlinesBeforeUpdatedEndTime(
      hasOngingDeadlines, hasOngingDeadlines, timeNow)).toBeTruthy();
    expect(DeadlineExtensionHelper.isDeadlinesBeforeUpdatedEndTime(
      hasNoOngoingDeadlines, hasNoOngoingDeadlines, timeNow)).toBeTruthy();
    expect(DeadlineExtensionHelper.isDeadlinesBeforeUpdatedEndTime(
      hasOngingDeadlines, hasNoOngoingDeadlines, timeNow - (2 * fixedLengthOfTime))).toBeFalsy();
  });

  it('should filter and set deadlines before given end time correctly', () => {
    expect(Object.keys(DeadlineExtensionHelper.setDeadlinesBeforeEndTime(hasOngingDeadlines, timeNow)).length)
      .toEqual(1);
    expect(Object.keys(DeadlineExtensionHelper.setDeadlinesBeforeEndTime(hasNoOngoingDeadlines, timeNow)).length)
      .toEqual(1);
  });

  it('should map students correctly', () => {
    const student1Extension: Record<string, number> = { 'student1Model@example.com': 0 };
    const student2Extension: Record<string, number> = { 'student2Model@example.com': 0 };
    const studentModels = DeadlineExtensionHelper
      .mapStudentsToStudentModels([student1, student2], { ...student1Extension, ...student2Extension }, timeNow);

    expect(studentModels).toEqual([student1Model, student2Model]);
  });

  it('should map instructors correctly', () => {
    const instructor1Extension: Record<string, number> = { 'instructor1Model@example.com': 3 };
    const instructor2Extension: Record<string, number> = { 'instructor2Model@example.com': 3 };
    const instructorModels = DeadlineExtensionHelper
      .mapInstructorsToInstructorModels([instructor1, instructor2],
        { ...instructor1Extension, ...instructor2Extension }, timeNow);

    expect(instructorModels).toEqual([instructor1ModelWithExtension, instructor2ModelWithExtension]);
  });

  it('should get deadlines correctly after updating deadlines for creation', () => {
    const existingExtensionForStudentModel1: Record<string, number> = { 'student1Model@example.com': 0 };
    const existingExtensionForInstructorModel1: Record<string, number> = { 'instructor1Model@example.com': 0 };
    const hasOngingDeadlinesWithStudent = { ...hasOngingDeadlines, ...existingExtensionForStudentModel1 };
    const hasOngingDeadlinesWithInstructor = { ...hasOngingDeadlines, ...existingExtensionForInstructorModel1 };

    const updatedStudentDeadlines = DeadlineExtensionHelper.getUpdatedDeadlines([student1Model, student2Model],
      hasOngingDeadlinesWithStudent, DeadlineHandlerType.CREATE, 100);
    expect(Object.keys(updatedStudentDeadlines).length).toEqual(5);
    expect(updatedStudentDeadlines['student1Model@example.com']).toEqual(100);
    expect(updatedStudentDeadlines['student2Model@example.com']).toEqual(100);

    const updatedInstructorDeadlines = DeadlineExtensionHelper
      .getUpdatedDeadlines([instructor1ModelWithExtension, instructor2ModelWithExtension],
        hasOngingDeadlinesWithInstructor, DeadlineHandlerType.CREATE, 200);
    expect(Object.keys(updatedInstructorDeadlines).length).toEqual(5);
    expect(updatedInstructorDeadlines['instructor1Model@example.com']).toEqual(200);
    expect(updatedInstructorDeadlines['instructor2Model@example.com']).toEqual(200);
  });

  it('should get deadlines correctly after updating deadlines for deletion', () => {
    const student1ModelExtension: Record<string, number> = { 'student1Model@example.com': 0 };
    const instructor1ModelExtension: Record<string, number> = { 'instructor1Model@example.com': 0 };
    const hasOngingDeadlinesWithStudent = { ...hasOngingDeadlines, ...student1ModelExtension };
    const hasOngingDeadlinesWithInstructor = { ...hasOngingDeadlines, ...instructor1ModelExtension };

    expect(Object.keys(DeadlineExtensionHelper.getUpdatedDeadlines([student1Model, student2Model],
      hasOngingDeadlinesWithStudent, DeadlineHandlerType.DELETE)).length).toEqual(3);
    expect(Object.keys(DeadlineExtensionHelper
      .getUpdatedDeadlines([instructor1ModelWithExtension, instructor2ModelWithExtension],
        hasOngingDeadlinesWithInstructor, DeadlineHandlerType.DELETE)).length).toEqual(3);
  });
});
