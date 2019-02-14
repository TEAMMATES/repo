package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.ResetAccountAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link ResetAccountAction}.
 */
public class ResetAccountActionTest extends BaseActionTest<ResetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();

        ______TS("Failure case: no parameters supplied");

        ResetAccountAction a = getAction();
        JsonResult r = getJsonResult(a);
        MessageOutput response = (MessageOutput) r.getOutput();

        assertEquals("Either student email or instructor email has to be specified.", response.getMessage());
        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        ______TS("Failure case: no course id supplied");

        String[] paramsInsufficient = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
        };

        verifyHttpParameterFailure(paramsInsufficient);

        ______TS("typical success case: reset instructor account");

        String[] paramsInstructor = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        a = getAction(paramsInstructor);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        response = (MessageOutput) r.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        assertNotNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
        assertNull(logic.getInstructorForGoogleId(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId()));

        ______TS("typical success case: reset instructor student account");

        String[] paramsStudent = {
                Const.ParamsNames.STUDENT_EMAIL, student1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, student1OfCourse1.getCourse(),
        };

        a = getAction(paramsStudent);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        response = (MessageOutput) r.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        assertNotNull(logic.getStudentForEmail(student1OfCourse1.getCourse(), student1OfCourse1.getEmail()));
        assertNull(logic.getStudentForGoogleId(student1OfCourse1.getCourse(), student1OfCourse1.googleId));

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
