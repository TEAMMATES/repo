package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.EditInstructorInCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.InstructorCreateRequest;

/**
 * SUT: {@link EditInstructorInCourseAction}.
 */
public class EditInstructorInCourseActionTest extends BaseActionTest<EditInstructorInCourseAction> {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructorToEdit = typicalBundle.instructors.get("instructorNotDisplayedToStudent1");
        String instructorId = instructorToEdit.googleId;
        String courseId = instructorToEdit.courseId;

        loginAsInstructor(instructorId);

        ______TS("Typical case: edit instructor successfully");

        String newInstructorName = "newName";
        String newInstructorEmail = "newEmail@email.com";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        EditInstructorInCourseAction a = getAction(reqBody, submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("The changes to the instructor " + newInstructorName + " has been updated.",
                msg.getMessage());

        InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.name);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));

        ______TS("Failure case: edit failed due to invalid parameters");

        String invalidEmail = "wrongEmail.com";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.name,
                invalidEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        a = getAction(reqBody, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        String expectedErrorMessage = new FieldValidator().getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, msg.getMessage());

        ______TS("Failure case: after editing instructor, no instructors are displayed");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, instructorToEdit.name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorToEdit.email,

                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,

                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,

                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT, "false",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true",
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);
        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        String expectedMessage = "At least one instructor must be displayed to students";
        assertEquals(expectedMessage, msg.getMessage());

        ______TS("Masquerade mode: edit instructor successfully");

        loginAsAdmin();

        newInstructorName = "newName2";
        newInstructorEmail = "newEmail2@email.com";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);


        a = getAction(reqBody, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The changes to the instructor " + newInstructorName + " has been updated.",
                msg.getMessage());

        editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertEquals(newInstructorName, editedInstructor.name);

        //remove the new instructor entity that was created
        CoursesLogic.inst().deleteCourseCascade("icieat.courseId");

        ______TS("Unsuccessful case: test null course id parameter");

        submissionParams = new String[0];
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        try {
            a = getAction(reqBody, submissionParams);
            getJsonResult(a);
        } catch (NullHttpParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_HTTP_PARAMETER,
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Unsuccessful case: test null instructor name parameter");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, null,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        try {
            a = getAction(reqBody, submissionParams);
            getJsonResult(a);
        } catch (InvalidHttpRequestBodyException e) {
            assertEquals(String.format(Const.StatusCodes.INVALID_REQUEST_BODY,
                    Const.ParamsNames.NAME), e.getMessage());
        }

        ______TS("Unsuccessful case: test null instructor email parameter");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                null, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        try {
            a = getAction(reqBody, submissionParams);
            getJsonResult(a);
        } catch (InvalidHttpRequestBodyException e) {
            assertEquals(String.format(Const.StatusCodes.INVALID_REQUEST_BODY,
                    Const.ParamsNames.EMAIL), e.getMessage());
        }
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);
    }
}
