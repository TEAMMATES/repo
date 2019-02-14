package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PutStudentProfileAction;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.StudentProfileUpdateRequest;

/**
 * SUT: {@link PutStudentProfileAction}.
 */
public class PutStudentProfileActionTest extends BaseActionTest<PutStudentProfileAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        AccountAttributes student1 = typicalBundle.accounts.get("student1InCourse1");
        AccountAttributes student2 = typicalBundle.accounts.get("student2InCourse1");

        testActionWithInvalidParameters(student1);
        testActionSuccess(student1, "Typical Case");
        testActionForbidden(student1, student2, "Forbidden Case");
        testActionInMasqueradeMode(student1);

        student1 = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        // simulate sanitization that occurs before persistence
        student1.sanitizeForSaving();
        testActionSuccess(student1, "Typical case: attempted script injection");
    }

    private void testActionWithInvalidParameters(AccountAttributes student) throws Exception {
        loginAsStudent(student.googleId);
        ______TS("Failure case: invalid parameters");

        String[] submissionParams = createValidParam(student.googleId);
        StudentProfileUpdateRequest req = createInvalidUpdateRequest();


        PutStudentProfileAction action = getAction(req, submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        List<String> expectedErrorMessages = new ArrayList<>();
        MessageOutput invalidOutput = (MessageOutput) result.getOutput();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, req.getShortName(),
                        FieldValidator.PERSON_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                        FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, req.getEmail(),
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(req.getNationality())));

        assertEquals(String.join(System.lineSeparator(), expectedErrorMessages), invalidOutput.getMessage());

        ______TS("Failure case: invalid parameters with attempted script injection");

        submissionParams = createValidParam(student.googleId);
        req = createInvalidUpdateRequestForProfileWithScriptInjection();


        action = getAction(req, submissionParams);
        result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        expectedErrorMessages = new ArrayList<>();
        invalidOutput = (MessageOutput) result.getOutput();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(req.getShortName()),
                        FieldValidator.PERSON_NAME_FIELD_NAME,
                        FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                        FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(req.getEmail()),
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(req.getInstitute()),
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                        FieldValidator.INSTITUTE_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(req.getNationality())));

        assertEquals(String.join(System.lineSeparator(), expectedErrorMessages), invalidOutput.getMessage());
    }

    private void testActionSuccess(AccountAttributes student, String caseDescription) {
        String[] submissionParams = createValidParam(student.googleId);
        StudentProfileUpdateRequest req = createValidRequestForProfile();
        loginAsStudent(student.googleId);

        ______TS(caseDescription);

        PutStudentProfileAction action = getAction(req, submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_ACCEPTED);
    }

    private void testActionForbidden(AccountAttributes student1, AccountAttributes student2,
                                     String caseDescription) {
        String[] submissionParams = createValidParam(student1.googleId);
        StudentProfileUpdateRequest req = createValidRequestForProfile();
        loginAsStudent(student2.googleId);

        ______TS(caseDescription);

        PutStudentProfileAction action = getAction(req, submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_FORBIDDEN, result.getStatusCode());
    }

    private void testActionInMasqueradeMode(AccountAttributes student) {

        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = createValidParamsForMasqueradeMode(student.googleId);
        StudentProfileUpdateRequest req = createValidRequestForProfile();

        PutStudentProfileAction action = getAction(req, submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_ACCEPTED);
    }

    private String[] createValidParamsForMasqueradeMode(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
                Const.ParamsNames.USER_ID, googleId,
        };
    }

    private String[] createValidParam(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
        };
    }

    private StudentProfileUpdateRequest createValidRequestForProfile() {
        StudentProfileUpdateRequest req = new StudentProfileUpdateRequest();

        req.setShortName("short ");
        req.setEmail("e@email.com  ");
        req.setInstitute(" TEAMMATES Test Institute 5   ");
        req.setNationality("American");
        req.setGender("  other   ");
        req.setMoreInfo("   This is more info on me   ");

        return req;
    }

    private StudentProfileUpdateRequest createInvalidUpdateRequest() {
        StudentProfileUpdateRequest req = new StudentProfileUpdateRequest();

        req.setShortName("$$short");
        req.setEmail("invalid.email");
        req.setInstitute("institute");
        req.setNationality("USA");
        req.setGender("female");
        req.setMoreInfo("This is more info on me");

        return req;
    }

    private StudentProfileUpdateRequest createInvalidUpdateRequestForProfileWithScriptInjection() {
        StudentProfileUpdateRequest req = new StudentProfileUpdateRequest();

        req.setShortName("short%<script>alert(\"was here\");</script>");
        req.setEmail("<script>alert(\"was here\");</script>");
        req.setInstitute("<script>alert(\"was here\");</script>");
        req.setNationality("USA<script>alert(\"was here\");</script>");
        req.setGender("female<script>alert(\"was here\");</script>");
        req.setMoreInfo("This is more info on me<script>alert(\"was here\");</script>");

        return req;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
