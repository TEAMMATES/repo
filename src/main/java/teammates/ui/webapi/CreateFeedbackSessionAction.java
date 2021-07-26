package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * Create a feedback session.
 */
class CreateFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        FeedbackSessionCreateRequest createRequest =
                getAndValidateRequestBody(FeedbackSessionCreateRequest.class);

        String feedbackSessionName = SanitizationHelper.sanitizeTitle(createRequest.getFeedbackSessionName());

        FeedbackSessionAttributes fs =
                FeedbackSessionAttributes
                        .builder(feedbackSessionName, course.getId())
                        .withCreatorEmail(instructor.getEmail())
                        .withTimeZone(course.getTimeZone())
                        .withInstructions(createRequest.getInstructions())
                        .withStartTime(createRequest.getSubmissionStartTime())
                        .withEndTime(createRequest.getSubmissionEndTime())
                        .withGracePeriod(createRequest.getGracePeriod())
                        .withSessionVisibleFromTime(createRequest.getSessionVisibleFromTime())
                        .withResultsVisibleFromTime(createRequest.getResultsVisibleFromTime())
                        .withIsClosingEmailEnabled(createRequest.isClosingEmailEnabled())
                        .withIsPublishedEmailEnabled(createRequest.isPublishedEmailEnabled())
                        .build();

        try {
            logic.createFeedbackSession(fs);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }

        if (createRequest.getToCopyCourseId() != null) {
            createFeedbackQuestions(createRequest.getToCopyCourseId(), courseId, createRequest.getFeedbackSessionName(),
                    createRequest.getOldSessionName());
        }

        fs = getNonNullFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        FeedbackSessionData output = new FeedbackSessionData(fs);
        InstructorPrivilegeData privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }

    private void createFeedbackQuestions(String copyCourseId, String newCourseId, String feedbackSessionName,
            String oldSessionName) {
        logic.getFeedbackQuestionsForSession(oldSessionName, copyCourseId).forEach(question -> {
            FeedbackQuestionAttributes attributes = FeedbackQuestionAttributes.builder()
                    .withCourseId(newCourseId)
                    .withFeedbackSessionName(feedbackSessionName)
                    .withGiverType(question.getGiverType())
                    .withRecipientType(question.getRecipientType())
                    .withQuestionNumber(question.getQuestionNumber())
                    .withNumberOfEntitiesToGiveFeedbackTo(question.getNumberOfEntitiesToGiveFeedbackTo())
                    .withShowResponsesTo(question.getShowResponsesTo())
                    .withShowGiverNameTo(question.getShowGiverNameTo())
                    .withShowRecipientNameTo(question.getShowRecipientNameTo())
                    .withQuestionDetails(question.getQuestionDetails())
                    .withQuestionDescription(question.getQuestionDescription())
                    .build();

            // validate questions (giver & recipient)
            String err = attributes.getQuestionDetails().validateGiverRecipientVisibility(attributes);
            if (!err.isEmpty()) {
                throw new InvalidHttpRequestBodyException(err);
            }
            // validate questions (question details)
            FeedbackQuestionDetails questionDetails = attributes.getQuestionDetails();
            if (questionDetails instanceof FeedbackMsqQuestionDetails) {
                FeedbackMsqQuestionDetails msqQuestionDetails = (FeedbackMsqQuestionDetails) questionDetails;
                int numOfGeneratedMsqChoices = logic.getNumOfGeneratedChoicesForParticipantType(
                        attributes.getCourseId(), msqQuestionDetails.getGenerateOptionsFor());
                msqQuestionDetails.setNumOfGeneratedMsqChoices(numOfGeneratedMsqChoices);
                questionDetails = msqQuestionDetails;
            }
            List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();
            if (!questionDetailsErrors.isEmpty()) {
                throw new InvalidHttpRequestBodyException(questionDetailsErrors.toString());
            }

            try {
                attributes = logic.createFeedbackQuestion(attributes);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e.getMessage(), e);
            }
        });
    }
}
