package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RemindFeedbackSessionSubmissionAction;

/**
 * SUT: {@link RemindFeedbackSessionSubmissionAction}.
 */
public class RemindFeedbackSessionSubmissionActionTest extends BaseActionTest<RemindFeedbackSessionSubmissionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_SUBMISSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student5InCourse1");

        loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Unsuccessful case: Not enough parameters");
        verifyHttpParameterFailure();
        String[] paramsNoCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail(),
        };
        verifyHttpParameterFailure(paramsNoCourseId);
        String[] paramsNoFeedback = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail(),
        };
        verifyHttpParameterFailure(paramsNoFeedback);
        String[] paramsNoUsersToRemind = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };
        verifyHttpParameterFailure(paramsNoUsersToRemind);

        ______TS("Unsuccessful case: Feedback session not open, warning message generated");

        fs = typicalBundle.feedbackSessions.get("awaiting.session");
        String[] paramsFeedbackSessionNotOpen = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail(),
        };

        RemindFeedbackSessionSubmissionAction action = getAction(paramsFeedbackSessionNotOpen);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail(),
        };

        action = getAction(paramsTypical);
        result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifySpecifiedTasksAdded(action,
                Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student5InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);
    }

}
