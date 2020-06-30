package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.*;
import teammates.ui.webapi.output.GenerateEmailData;

/**
 * Generate email content
 */
public class GenerateEmailAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String emailType = getNonNullRequestParamValue(Const.ParamsNames.GENERATE_EMAIL_TYPE);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        Assumption.assertNotNull(student);

        EmailWrapper email;

        if (emailType.equals(EmailType.STUDENT_COURSE_JOIN.name())) {
            email = emailGenerator.generateStudentCourseJoinEmail(course, student);
        } else if (emailType.equals(EmailType.FEEDBACK_SESSION_REMINDER.name())) {
            if (feedbackSessionName == null) {
                throw new InvalidHttpParameterException("Feedback session name not specified");
            }
            FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
            email = emailGenerator.generateFeedbackSessionStudentReminderEmail(feedbackSession, student);
        } else {
            throw new InvalidHttpParameterException("Unknown emailType " + emailType);
        }

        return new JsonResult(new GenerateEmailData(email));
    }
}
