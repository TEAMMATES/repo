package teammates.ui.controller;

import java.time.LocalDateTime;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;

public class InstructorFeedbackEditSaveAction extends InstructorFeedbackAbstractAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account, sessionToken);

        // This is only for validation to pass; it will be overridden with its existing value at the logic layer
        String dummyCreatorEmail = "dummy@example.com";

        FeedbackSessionAttributes feedbackSession =
                extractFeedbackSessionData(feedbackSessionName, logic.getCourse(courseId), dummyCreatorEmail);

        FeedbackSessionAttributes oldFeedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        try {
            validateTimeData(feedbackSession);
            addResolvedTimeFieldsToDataIfRequired(feedbackSession, data, oldFeedbackSession);
            logic.updateFeedbackSession(feedbackSession);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "Updated Feedback Session "
                    + "<span class=\"bold\">(" + feedbackSession.getFeedbackSessionName() + ")</span> for Course "
                    + "<span class=\"bold\">[" + feedbackSession.getCourseId() + "]</span> created.<br>"
                    + "<span class=\"bold\">From:</span> " + feedbackSession.getStartTime()
                    + "<span class=\"bold\"> to</span> " + feedbackSession.getEndTime()
                    + "<br><span class=\"bold\">Session visible from:</span> " + feedbackSession.getSessionVisibleFromTime()
                    + "<br><span class=\"bold\">Results visible from:</span> " + feedbackSession.getResultsVisibleFromTime()
                    + "<br><br><span class=\"bold\">Instructions:</span> " + feedbackSession.getInstructions();
            data.setHasError(false);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.setHasError(true);
        }
        data.setStatusMessagesToUser(statusToUser);
        return createAjaxResult(data);
    }

    private void addResolvedTimeFieldsToDataIfRequired(
            FeedbackSessionAttributes session, InstructorFeedbackEditPageData data,
            FeedbackSessionAttributes oldFeedbackSession) {
        boolean isAdded = addResolvedTimeFieldToDataIfRequired(inputStartTimeLocal, session.getStartTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, Const.ParamsNames.FEEDBACK_SESSION_STARTTIME);

        if (!isAdded) {
            checkForDateUpdate(getNonNullRequestParamValue(
                    Const.ParamsNames.FEEDBACK_SESSION_STARTDATE), session.getStartTimeLocal(),
                    oldFeedbackSession.getEndTimeLocal(), data, Const.ParamsNames.FEEDBACK_SESSION_STARTDATE,
                    Const.StatusMessages.FEEDBACK_SESSION_STARTDATE_MODIFIED,
                    Const.StatusMessages.FEEDBACK_SESSION_STARTDATE_PARSE_FAILURE);
        }

        isAdded = addResolvedTimeFieldToDataIfRequired(inputEndTimeLocal, session.getEndTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, Const.ParamsNames.FEEDBACK_SESSION_ENDTIME);

        if (!isAdded) {
            checkForDateUpdate(getNonNullRequestParamValue(
                    Const.ParamsNames.FEEDBACK_SESSION_ENDDATE), session.getEndTimeLocal(),
                    oldFeedbackSession.getEndTimeLocal(), data, Const.ParamsNames.FEEDBACK_SESSION_ENDDATE,
                    Const.StatusMessages.FEEDBACK_SESSION_ENDDATE_MODIFIED,
                    Const.StatusMessages.FEEDBACK_SESSION_ENDDATE_PARSE_FAILURE);
        }

        addResolvedTimeFieldToDataIfRequired(inputVisibleTimeLocal, session.getSessionVisibleFromTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME);

        addResolvedTimeFieldToDataIfRequired(inputPublishTimeLocal, session.getResultsVisibleFromTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME);
    }

    private boolean addResolvedTimeFieldToDataIfRequired(LocalDateTime input, LocalDateTime resolved,
            InstructorFeedbackEditPageData data, String dateInputId, String timeInputId) {
        if (input == null || input.isEqual(resolved)) {
            return false;
        }
        data.putResolvedTimeField(dateInputId, TimeHelper.formatDateForSessionsForm(resolved));
        data.putResolvedTimeField(timeInputId, String.valueOf(resolved.getMinute() == 59 ? 23 : resolved.getHour()));
        return true;
    }

    private boolean checkForDateUpdate(String input, LocalDateTime resolved, LocalDateTime oldTime,
            InstructorFeedbackEditPageData data, String dateInputId, String modifiedMessage, String failureMessage) {
        boolean isAdded;
        if (resolved == null) {
            isAdded = addResolvedDateFieldToDataIfRequired(input, oldTime, data, dateInputId);
        } else {
            isAdded = addResolvedDateFieldToDataIfRequired(input, resolved, data, dateInputId);
        }
        addStatusMessageForModifiedDateFieldIfRequired(input, resolved, modifiedMessage, failureMessage, isAdded);
        return isAdded;
    }

    private boolean addResolvedDateFieldToDataIfRequired(String input, LocalDateTime resolved,
            InstructorFeedbackEditPageData data, String dateInputId) {
        if (input == null || input.equals(TimeHelper.formatDateForSessionsForm(resolved))) {
            return false;
        }
        data.putResolvedTimeField(dateInputId, TimeHelper.formatDateForSessionsForm(resolved));

        return true;
    }

    private void addStatusMessageForModifiedDateFieldIfRequired(String input, LocalDateTime resolved,
            String modifiedMessage, String failureMessage, boolean isAdded) {
        if (isAdded) {
            //failed to parse
            if (resolved == null) {
                statusToUser.add(new StatusMessage(failureMessage + input, StatusMessageColor.DANGER));
            } else {
                statusToUser.add(new StatusMessage(modifiedMessage + input, StatusMessageColor.WARNING));
            }
        }
    }
}
