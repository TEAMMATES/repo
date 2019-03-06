package teammates.ui.webapi.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.InstructorPrivilegeData;

/**
 * Get the instructor privilege.
 */
public class GetInstructorPrivilegeAction extends Action {
    static final Map<String, InstructorPrivilegeData> INSTRUCTOR_PRIVILEGES = new HashMap<>();

    static {
        InstructorPrivilegeData coOwnerPrivilegeData = new InstructorPrivilegeData();
        InstructorPrivilegeData managerPrivilegeData = new InstructorPrivilegeData();
        InstructorPrivilegeData observerPrivilegeData = new InstructorPrivilegeData();
        InstructorPrivilegeData tutorPrivilegeData = new InstructorPrivilegeData();
        InstructorPrivilegeData customPrivilegeData = new InstructorPrivilegeData();

        coOwnerPrivilegeData.setCanModifyCourse(true);
        coOwnerPrivilegeData.setCanModifyInstructor(true);
        coOwnerPrivilegeData.setCanModifySession(true);
        coOwnerPrivilegeData.setCanModifyStudent(true);
        coOwnerPrivilegeData.setCanViewStudentInSections(true);
        coOwnerPrivilegeData.setCanViewSessionInSections(true);
        coOwnerPrivilegeData.setCanSubmitSessionInSections(true);
        coOwnerPrivilegeData.setCanModifySessionCommentsInSections(true);

        managerPrivilegeData.setCanModifyCourse(false);
        managerPrivilegeData.setCanModifyInstructor(true);
        managerPrivilegeData.setCanModifySession(true);
        managerPrivilegeData.setCanModifyStudent(true);
        managerPrivilegeData.setCanViewStudentInSections(true);
        managerPrivilegeData.setCanViewSessionInSections(true);
        managerPrivilegeData.setCanSubmitSessionInSections(true);
        managerPrivilegeData.setCanModifySessionCommentsInSections(true);

        observerPrivilegeData.setCanModifyCourse(false);
        observerPrivilegeData.setCanModifyInstructor(false);
        observerPrivilegeData.setCanModifySession(false);
        observerPrivilegeData.setCanModifyStudent(false);
        observerPrivilegeData.setCanViewStudentInSections(true);
        observerPrivilegeData.setCanViewSessionInSections(true);
        observerPrivilegeData.setCanSubmitSessionInSections(false);
        observerPrivilegeData.setCanModifySessionCommentsInSections(false);

        tutorPrivilegeData.setCanModifyCourse(false);
        tutorPrivilegeData.setCanModifyInstructor(false);
        tutorPrivilegeData.setCanModifySession(false);
        tutorPrivilegeData.setCanModifyStudent(false);
        tutorPrivilegeData.setCanViewStudentInSections(true);
        tutorPrivilegeData.setCanViewSessionInSections(true);
        tutorPrivilegeData.setCanSubmitSessionInSections(true);
        tutorPrivilegeData.setCanModifySessionCommentsInSections(false);

        customPrivilegeData.setCanModifyCourse(false);
        customPrivilegeData.setCanModifyInstructor(false);
        customPrivilegeData.setCanModifySession(false);
        customPrivilegeData.setCanModifyStudent(false);
        customPrivilegeData.setCanViewStudentInSections(false);
        customPrivilegeData.setCanViewSessionInSections(false);
        customPrivilegeData.setCanSubmitSessionInSections(false);
        customPrivilegeData.setCanModifySessionCommentsInSections(false);

        INSTRUCTOR_PRIVILEGES.put(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, coOwnerPrivilegeData);
        INSTRUCTOR_PRIVILEGES.put(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER, managerPrivilegeData);
        INSTRUCTOR_PRIVILEGES.put(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, observerPrivilegeData);
        INSTRUCTOR_PRIVILEGES.put(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, tutorPrivilegeData);
        INSTRUCTOR_PRIVILEGES.put(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM, customPrivilegeData);
    }

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public ActionResult execute() {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);

        if (instructorRole != null) {
            // fetching privilege for a particular instructor role.
            InstructorPrivilegeData rolePrivilege = INSTRUCTOR_PRIVILEGES.getOrDefault(instructorRole, null);
            if (rolePrivilege == null) {
                return new JsonResult("Invalid instructor role.", HttpStatus.SC_BAD_REQUEST);
            }
            return new JsonResult(rolePrivilege);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        // is getting privilege of another instructor
        String instructorOfInterest = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        InstructorAttributes instructor;
        if (instructorOfInterest == null) {
            instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        } else {
            instructor = logic.getInstructorForGoogleId(courseId, instructorOfInterest);
            if (instructor == null) {
                return new JsonResult("Instructor does not exist.", HttpStatus.SC_NOT_FOUND);
            }
        }

        String sectionName = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorPrivilegeData response = new InstructorPrivilegeData();

        // course level privileges.
        response.setCanModifyCourse(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        response.setCanModifySession(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        response.setCanModifyStudent(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        response.setCanModifyInstructor(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));

        if (sectionName == null) {
            response.setCanViewStudentInSections(
                    instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
            if (feedbackSessionName == null) {
                response.setCanSubmitSessionInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            } else {
                response.setCanSubmitSessionInSections(
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(feedbackSessionName,
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            }
        } else {
            response.setCanViewStudentInSections(
                    instructor.isAllowedForPrivilege(sectionName,
                            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));

            if (feedbackSessionName == null) {
                response.setCanSubmitSessionInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            } else {
                response.setCanSubmitSessionInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            }
        }
        return new JsonResult(response);
    }

}
