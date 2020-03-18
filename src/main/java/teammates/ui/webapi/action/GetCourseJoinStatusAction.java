package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.JoinStatus;

/**
 * Get the join status of a course.
 */
public class GetCourseJoinStatusAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public ActionResult execute() {
        String regkey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentJoinStatus(regkey);
        case Const.EntityType.INSTRUCTOR:
            return getInstructorJoinStatus(regkey);
        default:
            return new JsonResult("Error: invalid entity type", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private JsonResult getStudentJoinStatus(String regkey) {
        StudentAttributes student = logic.getStudentForRegistrationKey(regkey);
        if (student == null) {
            return new JsonResult("No student with given registration key: " + regkey, HttpStatus.SC_NOT_FOUND);
        }
        return getJoinStatusResult(student.isRegistered());
    }

    private JsonResult getInstructorJoinStatus(String regkey) {
        InstructorAttributes instructor = logic.getInstructorForRegistrationKey(regkey);
        if (instructor == null) {
            return new JsonResult("No instructor with given registration key: " + regkey, HttpStatus.SC_NOT_FOUND);
        }

        String institute = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        String mac = getRequestParamValue(Const.ParamsNames.INSTITUTION_MAC);
        if (institute != null && !StringHelper.isCorrectSignature(institute, mac)) {
            return new JsonResult("Error in institute validation.", HttpStatus.SC_UNAUTHORIZED);
        }

        return getJoinStatusResult(instructor.isRegistered());
    }

    private JsonResult getJoinStatusResult(boolean hasJoined) {
        JoinStatus result = new JoinStatus(hasJoined, hasJoined ? null : userInfo.id);
        return new JsonResult(result);
    }
}
