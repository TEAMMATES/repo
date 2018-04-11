package teammates.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackSessionTemplates;
import teammates.ui.pagedata.InstructorFeedbackSessionsPageData;

public class InstructorFeedbackAddAction extends InstructorFeedbackAbstractAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotEmpty(courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        String feedbackSessionName = SanitizationHelper.sanitizeTitle(
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));

        FeedbackSessionAttributes fs = extractFeedbackSessionData(feedbackSessionName, course, instructor.email);

        String feedbackSessionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TYPE);

        InstructorFeedbackSessionsPageData data = new InstructorFeedbackSessionsPageData(account, sessionToken);
        try {
            validateTimeData(fs);
            logic.createFeedbackSession(fs);

            try {
                createTemplateFeedbackQuestions(fs.getCourseId(), fs.getFeedbackSessionName(),
                                                fs.getCreatorEmail(), feedbackSessionType);
            } catch (InvalidParametersException e) {
                // Failed to create feedback questions for specified template/feedback session type.
                //TODO: let the user know an error has occurred? delete the feedback session?
                log.severe(TeammatesException.toStringWithStackTrace(e));
            }

            String statusMessage;

            if (FeedbackSessionType.STANDARD.getFeedbackSessionTemplateName().equals(feedbackSessionType)) {
                statusMessage = Const.StatusMessages.FEEDBACK_SESSION_ADDED_OWN_QUESTIONS;
            } else if (FeedbackSessionType.TEAM_EVALUATION.getFeedbackSessionTemplateName()
                    .equals(feedbackSessionType)) {
                statusMessage = Const.StatusMessages.FEEDBACK_SESSION_ADDED_TEAM;
            } else {
                statusMessage = Const.StatusMessages.FEEDBACK_SESSION_ADDED_TEAM_OPTIMIZED;
            }

            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "New Feedback Session <span class=\"bold\">(" + fs.getFeedbackSessionName() + ")</span> for Course "
                    + "<span class=\"bold\">[" + fs.getCourseId() + "]</span> created.<br>"
                    + "<span class=\"bold\">From:</span> " + fs.getStartTime()
                    + "<span class=\"bold\"> to</span> " + fs.getEndTime() + "<br>"
                    + "<span class=\"bold\">Session visible from:</span> " + fs.getSessionVisibleFromTime() + "<br>"
                    + "<span class=\"bold\">Results visible from:</span> " + fs.getResultsVisibleFromTime() + "<br><br>"
                    + "<span class=\"bold\">Instructions:</span> " + fs.getInstructions();

            //TODO: add a condition to include the status due to inconsistency problem of database
            //      (similar to the one below)
            return createRedirectResult(
                    data.getInstructorFeedbackEditLink(
                            fs.getCourseId(), fs.getFeedbackSessionName()));

        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        // isError == true if an exception occurred above

        boolean shouldOmitArchived = true;
        Map<String, InstructorAttributes> instructors = loadCourseInstructorMap(shouldOmitArchived);
        List<InstructorAttributes> instructorList = new ArrayList<>(instructors.values());
        List<CourseAttributes> courses = loadCoursesList(instructorList);
        List<FeedbackSessionAttributes> feedbackSessions = loadFeedbackSessionsList(instructorList);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(feedbackSessions);

        if (feedbackSessions.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY,
                                               StatusMessageColor.WARNING));
        }

        data.initWithoutHighlightedRow(courses, courseId, feedbackSessions, instructors, fs,
                                       feedbackSessionType);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, data);
    }

    private void createTemplateFeedbackQuestions(String courseId, String feedbackSessionName,
            String creatorEmail, String feedbackSessionType) throws InvalidParametersException {
        if (feedbackSessionType == null) {
            return;
        }

        List<FeedbackQuestionAttributes> questions =
                getFeedbackSessionTemplateQuestions(feedbackSessionType, courseId, feedbackSessionName, creatorEmail);

        int questionNumber = 1;
        for (FeedbackQuestionAttributes fqa : questions) {
            logic.createFeedbackQuestionForTemplate(fqa, questionNumber);
            questionNumber++;
        }
    }

    /**
     * Gets the list of questions for the specified feedback session template.
     */
    private static List<FeedbackQuestionAttributes> getFeedbackSessionTemplateQuestions(
            String templateType, String courseId, String feedbackSessionName, String creatorEmail) {
        Assumption.assertNotNull(templateType);

        String template = "";

        if (FeedbackSessionType.TEAM_EVALUATION.getFeedbackSessionTemplateName().equals(templateType)) {
            template = FeedbackSessionTemplates.TEAM_EVALUATION_TEMPLATE;
        } else if (FeedbackSessionType.OPTIMIZED_TEAM_EVALUATION
                .getFeedbackSessionTemplateName().equals(templateType)) {
            template = FeedbackSessionTemplates.OPTIMIZED_TEAM_EVALUATION_TEMPLATE;
        }

        if (!template.isEmpty()) {
            String jsonString = Templates.populateTemplate(template,
                    "${courseId}", courseId,
                    "${feedbackSessionName}", feedbackSessionName,
                    "${creatorEmail}", creatorEmail);

            Type listType = new TypeToken<ArrayList<FeedbackQuestionAttributes>>(){}.getType();
            return JsonUtils.fromJson(jsonString, listType);
        }

        return new ArrayList<>();
    }
}
