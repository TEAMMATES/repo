package teammates.test.cases.webapi;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.CreateInstructorInCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.InstructorCreateRequest;

/**
 * SUT: {@link CreateInstructorInCourseAction}.
 */
public class CreateInstructorInCourseActionTest extends BaseActionTest<CreateInstructorInCourseAction> {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Typical case: add an instructor successfully");

        loginAsInstructor(instructorId);

        String newInstructorName = "New Instructor Name";
        String newInstructorEmail = "ICIAAT.newInstructor@email.tmt";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName, newInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        CreateInstructorInCourseAction a = getAction(reqBody, submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("The instructor " + newInstructorName + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to "
                + newInstructorEmail + " in a few minutes.", msg.getMessage());

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));

        InstructorAttributes instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorEmail, instructorAdded.email);

        verifySpecifiedTasksAdded(a, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = a.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();

        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, reqBody.getEmail());
        assertEquals(instructorId, reqBody.getId());

        ______TS("Error: try to add an existing instructor");

        a = getAction(reqBody, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_CONFLICT, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("An instructor with the same email address already exists in the course.",
                msg.getMessage());

        verifyNoTasksAdded(a);

        ______TS("Error: try to add an instructor with invalid email");

        String newInvalidInstructorEmail = "ICIAAT.newInvalidInstructor.email.tmt";
        reqBody = new InstructorCreateRequest(null, newInstructorName, newInvalidInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        a = getAction(reqBody, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, newInvalidInstructorEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH),
                msg.getMessage());

        verifyNoTasksAdded(a);

        ______TS("Masquerade mode: add an instructor");

        instructorsLogic.deleteInstructorCascade(courseId, newInstructorEmail);

        loginAsAdmin();
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName, newInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        a = getAction(reqBody, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The instructor " + newInstructorName + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to "
                + newInstructorEmail + " in a few minutes.", msg.getMessage());

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));

        instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorEmail, instructorAdded.email);

        verifySpecifiedTasksAdded(a, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = a.getTaskQueuer().getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();

        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL)[0]);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);

        // remove the newly added instructor
        InstructorsLogic.inst().deleteInstructorCascade("idOfTypicalCourse1", "instructor@email.tmt");
    }
}
