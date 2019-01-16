package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.newcontroller.DeleteInstructorCourseAllStudentsAction;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link DeleteInstructorCourseAllStudentsAction}.
 */
public class DeleteInstructorCourseAllStudentsActionTest extends BaseActionTest<DeleteInstructorCourseAllStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS_DELETE_ALL_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("success: delete all students");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        DeleteInstructorCourseAllStudentsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyInaccessibleWithoutModifyStudentPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
