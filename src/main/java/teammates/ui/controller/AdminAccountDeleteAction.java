package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class AdminAccountDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String studentId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String account = getRequestParamValue("account");
        
        ActionResult result = null;
        
        //TODO: We should extract these into separate actions e.g., AdminInstructorDowngradeAction
        if (courseId == null && account == null){    
            //delete instructor status
            logic.downgradeInstructorToStudentCascade(instructorId);
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_STATUS_DELETED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor Status for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
            result = createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE);
            
        } else if (courseId == null && account != null){
            //delete entire account
            logic.deleteAccount(instructorId);
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_ACCOUNT_DELETED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor Account for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
            result = createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE);
            
        } else if (courseId != null && instructorId != null){
            //remove instructor from course
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, instructorId);
            logic.deleteInstructor(courseId, instructor.email);
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE, StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor <span class=\"bold\">" + instructorId + 
                    "</span> has been deleted from Course<span class=\"bold\">[" + courseId + "]</span>"; 
            result = createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE + "?instructorid=" + instructorId);
            
        } else if (courseId != null && studentId != null) {
            //remove student from course
            StudentAttributes student = logic.getStudentForGoogleId(courseId, studentId);
            logic.deleteStudent(courseId, student.email);
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE, StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor <span class=\"bold\">" + instructorId + 
                    "</span>'s student status in Course<span class=\"bold\">[" + courseId + "]</span> has been deleted"; 
            result = createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE + "?instructorid=" + studentId);
        }        
        
        return result;
    }

}
