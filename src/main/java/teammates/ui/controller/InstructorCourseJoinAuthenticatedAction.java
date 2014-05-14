package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * This action handles instructors who attempt to join a course after
 * the instructor has been forced to re-authenticate himself by 
 * {@link InstructorCourseJoinAction}. This action does the actual
 * joining of the instructor to the course.
 */
public class InstructorCourseJoinAuthenticatedAction extends Action {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        Assumption.assertNotNull(key);
        
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        /* Process authentication for the instructor to join course */
        try {
            logic.joinCourseForInstructor(key, account.googleId);
        } catch (JoinCourseException e) {
            // Does not sanitize for html to allow insertion of mailto link
            setStatusForException(e, e.getMessage());
            log.info(e.getMessage());
        }
        
        /* Set status to be shown to admin */
        final String joinedCourseMsg = "Action Instructor Joins Course"
                + "<br/>Google ID: " + account.googleId
                + "<br/>Key : " + StringHelper.decrypt(key);
        if(statusToAdmin != null) {
            statusToAdmin += "<br/><br/>" + joinedCourseMsg;
        } else {
            statusToAdmin = joinedCourseMsg;
        }
        
        /* Create redirection to instructor's homepage */
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        InstructorAttributes instructor  = logic.getInstructorForRegistrationKey(key);
        if(instructor != null) {
            response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, instructor.courseId);    
        }
        
        return response;
    }
}
