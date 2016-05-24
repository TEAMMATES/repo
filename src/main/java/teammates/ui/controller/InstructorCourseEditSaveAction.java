package teammates.ui.controller;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorCourseEditSaveAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        
        String courseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_NAME, courseName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
        
        CourseAttributes courseToEdit = new CourseAttributes(courseId, courseName);
        
        try {
            logic.updateCourse(courseToEdit);
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Course name for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                          + "New name: " + courseName;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
                                        
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;                                
    }
}
