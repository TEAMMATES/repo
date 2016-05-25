package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackRemindAction extends InstructorFeedbacksPageAction {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        
        nextUrl = nextUrl == null ? Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE : nextUrl; //NOPMD
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        logic.sendReminderForFeedbackSession(courseId, feedbackSessionName);
        
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT, StatusMessageColor.SUCCESS));
        statusToAdmin = "Email sent out to all students who have not completed " 
                      + "Feedback Session <span class=\"bold\">(" + feedbackSessionName 
                      + ")</span> " + "of Course <span class=\"bold\">[" + courseId + "]</span>";
        
        return createRedirectResult(nextUrl);
    }

}
