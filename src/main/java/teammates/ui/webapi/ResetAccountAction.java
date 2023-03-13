package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: resets an account ID.
 */
class ResetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (studentEmail == null && instructorEmail == null) {
            throw new InvalidHttpParameterException("Either student email or instructor email has to be specified.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = null;
        if (studentEmail != null) {
            StudentAttributes existingStudent = logic.getStudentForEmail(courseId, studentEmail);
            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            AccountAttributes accountInfo = logic.getAccount(existingStudent.getGoogleId());

            wrongGoogleId = existingStudent.getGoogleId();

            try {
                if (accountInfo == null || accountInfo.isMigrated()) {
                    sqlLogic.resetStudentGoogleId(studentEmail, courseId);
                } else {
                    logic.resetStudentGoogleId(studentEmail, courseId);
                    taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, true);
                }
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (instructorEmail != null) {
            InstructorAttributes existingInstructor = logic.getInstructorForEmail(courseId, instructorEmail);
            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            AccountAttributes accountInfo = logic.getAccount(existingInstructor.getGoogleId());

            wrongGoogleId = existingInstructor.getGoogleId();

            try {
                if (accountInfo == null || accountInfo.isMigrated()) {
                    sqlLogic.resetInstructorGoogleId(instructorEmail, courseId);
                } else {
                    logic.resetInstructorGoogleId(instructorEmail, courseId);
                    taskQueuer.scheduleCourseRegistrationInviteToInstructor(null, instructorEmail, courseId, true);
                }
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        // To modify
        if (wrongGoogleId != null
                && logic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
                && logic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            logic.deleteAccountCascade(wrongGoogleId);
        }

        return new JsonResult("Account is successfully reset.");
    }

}
