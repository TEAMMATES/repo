package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.ui.webapi.action.GetStudentAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.JoinState;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.output.StudentData;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionTest extends BaseActionTest<GetStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    private void assertStudentDataMatches(StudentData studentData, StudentAttributes student, boolean isInstructor) {
        assertEquals(student.getEmail(), studentData.getEmail());
        assertEquals(student.getCourse(), studentData.getCourseId());
        assertEquals(student.getName(), studentData.getName());

        if (student.getLastName() == null) {
            String[] name = student.name.split(" ");
            assertEquals(name[name.length - 1], studentData.getLastName());
        } else {
            assertEquals(student.getLastName(), studentData.getLastName());
        }

        assertEquals(student.getTeam(), studentData.getTeamName());
        assertEquals(student.getSection(), studentData.getSectionName());

        if (isInstructor) {
            assertEquals(student.getComments(), studentData.getComments());

            if (student.isRegistered()) {
                assertTrue(studentData.getJoinState().equals(JoinState.JOINED));
            } else {
                assertTrue(studentData.getJoinState().equals(JoinState.NOT_JOINED));
            }
        } else {
            assertNull(studentData.getComments());
            assertNull(studentData.getJoinState());
        }
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

        ______TS("Failure Case: No Course ID in params");

        verifyHttpParameterFailure();

        ______TS("Failure Case: Unregistered Student with no RegKey");

        gaeSimulation.logoutUser();

        StudentAttributes unregStudent =
                StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
        };

        GetStudentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        assertEquals("No student found", message.getMessage());

        ______TS("Success Case: Unregistered Student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregStudent.key),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        StudentData outputData = (StudentData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertStudentDataMatches(outputData, unregStudent, false);

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        gaeSimulation.logoutUser();
        loginAsStudent(student1InCourse1.googleId);

        ______TS("Failure Case: Student - Logged In with no params");

        verifyHttpParameterFailure();

        ______TS("Success Case: Student - Logged In");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        outputData = (StudentData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertStudentDataMatches(outputData, student1InCourse1, false);

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.logoutUser();
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Failure Case: Instructor - Incomplete Params");

        verifyHttpParameterFailure();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
        };

        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getCourse(),
        };

        verifyHttpParameterFailure(submissionParams);

        ______TS("Success Case: Instructor");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        outputData = (StudentData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertStudentDataMatches(outputData, student1InCourse1, true);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        ______TS("Student - must be in the course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
        };

        verifyInaccessibleForStudents(submissionParams);

        ______TS("Instructor - must be in same course as student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("Unregistered Student - can access with key");

        StudentAttributes unregStudent =
                StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregStudent.key),
        };

        verifyAccessibleForUnregisteredUsers(submissionParams);
    }
}
