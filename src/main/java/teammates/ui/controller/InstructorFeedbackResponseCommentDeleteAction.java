package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * Action: Delete {@link FeedbackResponseCommentAttributes}.
 */
public class InstructorFeedbackResponseCommentDeleteAction extends InstructorFeedbackResponseCommentAbstractAction {
    private String statusMessageToAdmin;

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseCommentId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        verifyAccessibleForInstructorToFeedbackResponseComment(
                feedbackResponseCommentId, instructor, session, response);

        Long commentId = Long.parseLong(feedbackResponseCommentId);

        logic.deleteDocumentByCommentId(commentId);
        logic.deleteFeedbackResponseCommentById(commentId);

        statusMessageToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + commentId + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";

        InstructorFeedbackResponseCommentAjaxPageData data =
                new InstructorFeedbackResponseCommentAjaxPageData(account, sessionToken);

        statusToAdmin.add(statusMessageToAdmin);
        return createAjaxResult(data);
    }
}
