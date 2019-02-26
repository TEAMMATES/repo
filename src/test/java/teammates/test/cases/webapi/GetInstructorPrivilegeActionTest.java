package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetInstructorPrivilegeAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.InstructorPrivilegeData;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionTest extends BaseActionTest<GetInstructorPrivilegeAction> {

    private DataBundle dataBundle = getTypicalDataBundle();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String section1 = dataBundle.students.get("student1InCourse1").getSection();
        String session1 = dataBundle.feedbackSessions.get("session1InCourse1").getFeedbackSessionName();
        InstructorPrivileges privileges = instructor1ofCourse1.privileges;
        // update section privilege for testing purpose.

        // course level privilege
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);

        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);

        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);

        // section level privilege
        privileges.updatePrivilege(section1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);

        // session level privilege
        privileges.updatePrivilege(section1, session1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        privileges.updatePrivilege(section1, session1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);

        instructor1ofCourse1.privileges = privileges;

        instructor1ofCourse1.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        dataBundle.instructors.put("instructor1OfCourse1", instructor1ofCourse1);
        removeAndRestoreDataBundle(dataBundle);
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1ofCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Request with course id to fetch general privileges");

        String[] courseIdParam = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        GetInstructorPrivilegeAction a = getAction(courseIdParam);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());

        ______TS("Request with course id and section name to fetch section level privileges");

        String[] sectionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
        };

        a = getAction(sectionParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());

        ______TS("Request with course id, section name and session name to fetch session level privileges");

        String[] sessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1ofCourse1.getFeedbackSessionName(),
        };

        a = getAction(sessionParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());

        ______TS("Request with course id,"
                + " section name and invalid session name, return section level privilege.");

        String[] invalidSessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "invalid session",
        };

        a = getAction(invalidSessionParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());

        ______TS("Request with course id and invalid section name, return general privilege.");

        String[] invalidSectionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, "invalid section",
        };

        a = getAction(invalidSectionParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());

        ______TS("Request with course id and invalid session name, return general privilege.");

        String[] invalidSectionAndCourseIdParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "invalid session",
        };

        a = getAction(invalidSectionAndCourseIdParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());

        ______TS("Request with course id and session name to fetch privilege for an instructor role");

        // course id is used for instructor identify verification here.
        String[] courseAndSessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME, "coowner",
        };

        a = getAction(courseAndSessionParams);
        response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());
        assertTrue(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        // has privilege in any of the sections.
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());

        ______TS("Failure: Request with invalid instructor role.");
        String[] invalidRoleParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME, "invalid role",
        };

        a = getAction(invalidRoleParams);
        JsonResult result = getJsonResult(a);

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid instructor role.", output.getMessage());

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
    }

}
