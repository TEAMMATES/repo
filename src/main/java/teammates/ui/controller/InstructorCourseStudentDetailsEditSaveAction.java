package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

public class InstructorCourseStudentDetailsEditSaveAction extends InstructorCoursesPageAction {
    
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
        
        data.student = logic.getStudentForEmail(courseId, studentEmail);
        data.regKey = logic.getEncryptedKeyForStudent(courseId, studentEmail);
        
        data.student.name = getRequestParamValue(Const.ParamsNames.STUDENT_NAME);
        data.student.email = getRequestParamValue(Const.ParamsNames.NEW_STUDENT_EMAIL);
        data.student.team = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        data.student.comments = getRequestParamValue(Const.ParamsNames.COMMENTS);    
        
        data.student.name = Sanitizer.sanitizeName(data.student.name);
        data.student.email = Sanitizer.sanitizeEmail(data.student.email);
        data.student.team = Sanitizer.sanitizeName(data.student.team);
        data.student.comments = Sanitizer.sanitizeTextField(data.student.comments);
        
        try {
            logic.updateStudent(studentEmail, data.student);
            statusToUser.add(Const.StatusMessages.STUDENT_EDITED);
            statusToAdmin = "Student <span class=\"bold\">" + studentEmail + 
                    "'s</span> details in Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"+ 
                    "New Email: " + data.student.email + "<br>New Team: " + 
                    data.student.team + "<br>Comments: " + data.student.comments;
            
            RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
            return result;
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.newEmail = data.student.email;
            data.student.email = studentEmail;
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT, data);
        }
        
    }


}
