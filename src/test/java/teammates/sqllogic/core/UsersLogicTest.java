package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;
import teammates.ui.request.InstructorCreateRequest;

/**
 * SUT: {@link UsersLogic}.
 */
public class UsersLogicTest extends BaseTestCase {

    private UsersLogic usersLogic = UsersLogic.inst();

    private AccountsLogic accountsLogic;

    private UsersDb usersDb;

    private Instructor instructor;

    private Student student;

    private Account account;

    private Course course;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        usersDb = mock(UsersDb.class);
        accountsLogic = mock(AccountsLogic.class);
        FeedbackResponsesLogic feedbackResponsesLogic = mock(FeedbackResponsesLogic.class);
        FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = mock(FeedbackResponseCommentsLogic.class);
        DeadlineExtensionsLogic deadlineExtensionsLogic = mock(DeadlineExtensionsLogic.class);
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        usersLogic.initLogicDependencies(usersDb, accountsLogic, feedbackResponsesLogic,
                feedbackResponseCommentsLogic, deadlineExtensionsLogic);

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = getTypicalInstructor();
        student = getTypicalStudent();
        account = getTypicalAccount();

        instructor.setAccount(account);
        student.setAccount(account);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateInstructor_validInstructorDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        usersLogic.createInstructor(instructor);

        verify(usersDb, times(1)).createInstructor(instructor);
    }

    @Test
    public void testCreateInstructor_instructorWithInvalidEmail_throwsInvalidParametersException()
            throws EntityAlreadyExistsException {
        instructor.setEmail("invalid-email");

        assertThrows(InvalidParametersException.class, () -> usersLogic.createInstructor(instructor));
        verify(usersDb, never()).createInstructor(instructor);
    }

    @Test
    public void testUpdateInstructor_invalidInstructor_throwsInvalidParametersException()
            throws EntityDoesNotExistException {
        String invalidName = "1234567890".repeat(11);
        InstructorCreateRequest request = new InstructorCreateRequest(
                instructor.getGoogleId(), invalidName, "", instructor.getRole().toString(),
                instructor.getDisplayName(), true);

        when(usersDb.getInstructorByGoogleId(instructor.getCourseId(), instructor.getGoogleId())).thenReturn(instructor);

        assertThrows(InvalidParametersException.class,
                () -> usersLogic.updateInstructorCascade(instructor.getCourseId(), request));

        verify(usersDb, never()).updateInstructor(instructor);
    }

    @Test
    public void testCreateStudent_studentDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        usersLogic.createStudent(student);

        verify(usersDb, times(1)).createStudent(student);
    }

    @Test
    public void testCreateStudent_studentWithInvalidEmail_throwsInvalidParametersException()
            throws EntityAlreadyExistsException {
        student.setEmail("invalid-email");

        assertThrows(InvalidParametersException.class, () -> usersLogic.createStudent(student));
        verify(usersDb, never()).createStudent(student);
    }

    @Test
    public void testUpdateStudent_invalidStudent_throwsInvalidParametersException()
            throws EntityDoesNotExistException {
        student.setEmail("");

        assertThrows(InvalidParametersException.class, () -> usersLogic.updateStudentCascade(student));
        verify(usersDb, never()).updateStudent(student);
    }

    @Test
    public void testResetInstructorGoogleId_instructorExistsWithEmptyUsersListFromGoogleId_success()
            throws EntityDoesNotExistException {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getInstructorForEmail(courseId, email)).thenReturn(instructor);
        when(usersDb.getAllUsersByGoogleId(googleId)).thenReturn(Collections.emptyList());
        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        List<Instructor> instructorsList = new ArrayList<>();
        instructorsList.add(instructor);
        when(usersLogic.getInstructorsForCourse(courseId)).thenReturn(instructorsList);

        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertEquals(null, instructor.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(googleId);
    }

    @Test
    public void testResetInstructorGoogleId_instructorDoesNotExists_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getInstructorForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetInstructorGoogleId(email, courseId, googleId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Instructor [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    @Test
    public void testResetStudentGoogleId_studentExistsWithEmptyUsersListFromGoogleId_success()
            throws EntityDoesNotExistException {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getStudentForEmail(courseId, email)).thenReturn(student);
        when(usersDb.getAllUsersByGoogleId(googleId)).thenReturn(Collections.emptyList());
        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(googleId);
    }

    @Test
    public void testResetStudentGoogleId_entityDoesNotExists_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getStudentForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentGoogleId(email, courseId, googleId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Student [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    @Test
    public void testGetUnregisteredStudentsForCourse_success() {
        Account registeredAccount = new Account("valid-google-id", "student-name", "valid1-student@email.tmt");
        Student registeredStudent = new Student(course, "reg-student-name", "valid1-student@email.tmt", "comments");
        registeredStudent.setAccount(registeredAccount);

        Student unregisteredStudentNullAccount =
                new Student(course, "unreg1-student-name", "valid2-student@email.tmt", "comments");
        unregisteredStudentNullAccount.setAccount(null);

        List<Student> students = Arrays.asList(
                registeredStudent,
                unregisteredStudentNullAccount);

        when(usersDb.getStudentsForCourse(course.getId())).thenReturn(students);

        List<Student> unregisteredStudents = usersLogic.getUnregisteredStudentsForCourse(course.getId());

        assertEquals(1, unregisteredStudents.size());
        assertTrue(unregisteredStudents.get(0).equals(unregisteredStudentNullAccount));
    }

    @Test
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse_lastModifyInstructorPrivilege_shouldPreserve() {
        InstructorPrivileges privileges = instructor.getPrivileges();
        privileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        instructor.setPrivileges(privileges);
        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(course.getId(), instructor);

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }

}
