package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: Permanently delete a specific session from Recycle Bin for an instructor.
 */
public class InstructorFeedbackDeleteRecoverySessionAction extends Action {

    @Override
    public ActionResult execute() {

        String idOfCourseToRestore = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, idOfCourseToRestore);

        String nameOfSessionToRestore = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, nameOfSessionToRestore);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToRestore, account.googleId),
                logic.getFeedbackSession(nameOfSessionToRestore, idOfCourseToRestore),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        try {
            logic.deleteFeedbackSession(nameOfSessionToRestore, idOfCourseToRestore);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_DELETED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Session <span class=\"bold\">[" + nameOfSessionToRestore + "]</span> "
                    + "from Course: <span class=\"bold\">[" + idOfCourseToRestore + " permanently deleted.";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }
}
