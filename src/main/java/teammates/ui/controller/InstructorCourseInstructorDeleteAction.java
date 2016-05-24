package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;

/**
 * Action: deleting an instructor for a course by another instructor
 */
public class InstructorCourseInstructorDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        /* Process deleting an instructor and setup status to be shown to user and admin */
        if (hasAlternativeInstructor(courseId, instructorEmail)) {
            logic.deleteInstructor(courseId, instructorEmail);
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorEmail + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        } else {
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, StatusMessageColor.DANGER));
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorEmail + "</span>"
                    + " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
                    + "as there is only one instructor left to be able to modify instructors.<br>";
        }
        
        /* Create redirection. It will redirect back to 'Courses' page if the instructor deletes himself */
        RedirectResult result = null;
        if (logic.isInstructorOfCourse(account.googleId, courseId)) {
            result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        } else {
            result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        }
        
        return result;
    }

    /**
     * @param courseId is the id of the course
     * @param instructorToDeleteEmail is the email of the instructor who is being deleted
     * @return true if there is a joined instructor (other than the instructor to delete) with the privilege of modifying instructors
     */
    private boolean hasAlternativeInstructor(String courseId, String instructorToDeleteEmail) {

        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);

        for (InstructorAttributes instr : instructors) {

            boolean isAlternativeInstructor = instr.isRegistered()
                                              && !instr.getEmail().equals(instructorToDeleteEmail)
                                              && instr.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

            if (isAlternativeInstructor) {
                return true;
            }
        }

        return false;
    }
}
