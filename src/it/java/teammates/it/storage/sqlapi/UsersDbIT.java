package teammates.it.storage.sqlapi;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final AccountsDb accountsDb = AccountsDb.inst();

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        Account instructorAccount = new Account("instructor-account", "instructor-name", "valid-instructor@email.tmt");
        accountsDb.createAccount(instructorAccount);
        instructor = getTypicalInstructor();
        usersDb.createInstructor(instructor);
        instructor.setAccount(instructorAccount);

        Account studentAccount = new Account("student-account", "student-name", "valid-student@email.tmt");
        accountsDb.createAccount(studentAccount);
        student = getTypicalStudent();
        usersDb.createStudent(student);
        student.setAccount(studentAccount);

        HibernateUtil.flushSession();
    }

    @Test
    public void testGetInstructor() {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualInstructor.getId());
        actualInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by courseId and email");
        actualInstructor = usersDb.getInstructor(instructor.getCourseId(), instructor.getEmail());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by courseId and email that does not exist");
        actualInstructor = usersDb.getInstructor(instructor.getCourseId(), "does-not-exist@teammates.tmt");
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by regKey");
        actualInstructor = usersDb.getInstructorByRegKey(instructor.getRegKey());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by regKey that does not exist");
        actualInstructor = usersDb.getInstructorByRegKey("invalid-reg-key");
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by googleId");
        actualInstructor = usersDb.getInstructorByGoogleId(instructor.getCourseId(), instructor.getAccount().getGoogleId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by googleId that does not exist");
        actualInstructor = usersDb.getInstructorByGoogleId(instructor.getCourseId(), "invalid-google id");
        assertNull(actualInstructor);
    }

    @Test
    public void testGetStudent() {
        ______TS("success: gets a student that already exists");
        Student actualStudent = usersDb.getStudent(student.getId());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualStudent.getId());
        actualStudent = usersDb.getStudent(nonExistentId);
        assertNull(actualStudent);

        ______TS("success: gets a student by courseId and email");
        actualStudent = usersDb.getStudent(student.getCourseId(), student.getEmail());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by courseId and email that does not exist");
        actualStudent = usersDb.getStudent(student.getCourseId(), "does-not-exist@teammates.tmt");
        assertNull(actualStudent);

        ______TS("success: gets a student by regKey");
        actualStudent = usersDb.getStudentByRegKey(student.getRegKey());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by regKey that does not exist");
        actualStudent = usersDb.getStudentByRegKey("invalid-reg-key");
        assertNull(actualStudent);

        ______TS("success: gets a student by googleId");
        actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), student.getAccount().getGoogleId());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by googleId that does not exist");
        actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), "invalid-google id");
        assertNull(actualStudent);
    }

    private Student getTypicalStudent() {
        return new Student(course, "student-name", "valid-student@email.tmt", "comments");
    }

    private Instructor getTypicalInstructor() {
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        return new Instructor(course, "instructor-name", "valid-instructor@email.tmt",
                true, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }
}
