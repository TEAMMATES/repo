package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.logic.api.EmailGenerator;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;

public abstract class FeedbackSubmissionEditSaveAction extends Action {

    private static final Logger log = Logger.getLogger();

    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;
    protected boolean hasValidResponse;
    protected boolean isSendSubmissionEmail;
    protected List<FeedbackResponseAttributes> responsesToSave = new ArrayList<>();
    protected List<FeedbackResponseAttributes> responsesToDelete = new ArrayList<>();
    protected List<FeedbackResponseAttributes> responsesToUpdate = new ArrayList<>();
    protected Map<String, String> responseGiverMapForComments = new HashMap<String, String>();
    protected Map<String, String> responseRecipientMapForComments = new HashMap<String, String>();
    protected Map<String, String> questionIdsForComments = new HashMap<String, String>();
    protected Map<String, String> commentsToUpdateId = new HashMap<String, String>();
    protected Map<String, String> commentsToAddText = new HashMap<String, String>();
    protected Map<String, String> commentsToUpdateText = new HashMap<String, String>();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        setAdditionalParameters();
        verifyAccessibleForSpecificUser();

        String userEmailForCourse = getUserEmailForCourse();

        data = new FeedbackSubmissionEditPageData(account, student, sessionToken);
        data.bundle = getDataBundle(userEmailForCourse);
        Assumption.assertNotNull("Feedback session " + feedbackSessionName
                                 + " does not exist in " + courseId + ".", data.bundle);

        checkAdditionalConstraints();

        setStatusToAdmin();

        if (!isSessionOpenForSpecificUser(data.bundle.feedbackSession)) {
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN,
                                               StatusMessageColor.WARNING));
            return createSpecificRedirectResult();
        }

        String userTeamForCourse = getUserTeamForCourse();
        String userSectionForCourse = getUserSectionForCourse();

        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();

        for (int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String totalResponsesForQuestion = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-" + questionIndx);

            if (totalResponsesForQuestion == null) {
                continue; // question has been skipped (not displayed).
            }

            List<FeedbackResponseAttributes> responsesForQuestion = new ArrayList<>();
            String questionId = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
            FeedbackQuestionAttributes questionAttributes = data.bundle.getQuestionAttributes(questionId);
            if (questionAttributes == null) {
                statusToUser.add(new StatusMessage("The feedback session or questions may have changed "
                                                       + "while you were submitting. Please check your responses "
                                                       + "to make sure they are saved correctly.",
                                                   StatusMessageColor.WARNING));
                isError = true;
                log.warning("Question not found. (deleted or invalid id passed?) id: "
                            + questionId + " index: " + questionIndx);
                continue;
            }

            FeedbackQuestionDetails questionDetails = questionAttributes.getQuestionDetails();

            int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);

            Set<String> emailSet = data.bundle.getRecipientEmails(questionAttributes.getId());
            emailSet.add("");
            emailSet = SanitizationHelper.desanitizeFromHtml(emailSet);

            ArrayList<String> responsesRecipients = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++) {
                FeedbackResponseAttributes response =
                        extractFeedbackResponseData(requestParameters, questionIndx, responseIndx, questionAttributes);

                if (response.feedbackQuestionType != questionAttributes.questionType) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_WRONG_QUESTION_TYPE, questionIndx));
                }

                boolean isExistingResponse = response.getId() != null;
                // test that if editing an existing response, that the edited response's id
                // came from the original set of existing responses loaded on the submission page
                if (isExistingResponse && !isExistingResponseValid(response)) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_INVALID_ID, questionIndx));
                    continue;
                }

                responsesRecipients.add(response.recipient);
                // if the answer is not empty but the recipient is empty
                if (response.recipient.isEmpty() && !response.responseMetaData.getValue().isEmpty()) {
                    errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MISSING_RECIPIENT, questionIndx));
                }

                if (response.responseMetaData.getValue().isEmpty()) {
                    // deletes the response since answer is empty
                    addToPendingResponses(response);
                } else {
                    response.giver = questionAttributes.giverType.isTeam() ? userTeamForCourse
                                                                                : userEmailForCourse;
                    response.giverSection = userSectionForCourse;
                    responsesForQuestion.add(response);
                    extractFeedbackResponseCommentsDataForResponse(questionIndx, responseIndx,
                            questionAttributes, response);
                }
            }

            List<String> questionSpecificErrors =
                    questionDetails.validateResponseAttributes(responsesForQuestion,
                                                               data.bundle.recipientList.get(questionId).size());
            errors.addAll(questionSpecificErrors);

            if (!emailSet.containsAll(responsesRecipients)) {
                errors.add(String.format(Const.StatusMessages.FEEDBACK_RESPONSE_INVALID_RECIPIENT, questionIndx));
            }

            if (errors.isEmpty()) {
                for (FeedbackResponseAttributes response : responsesForQuestion) {
                    addToPendingResponses(response);
                }
            } else {
                List<StatusMessage> errorMessages = new ArrayList<>();

                for (String error : errors) {
                    errorMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
                }

                statusToUser.addAll(errorMessages);
                isError = true;
            }
        }

        saveNewReponses(responsesToSave);
        deleteResponses(responsesToDelete);
        updateResponses(responsesToUpdate);

        updateResponsesComments(commentsToUpdateId, commentsToUpdateText, responseGiverMapForComments,
                responseRecipientMapForComments, questionIdsForComments);
        saveResponsesComments(commentsToAddText, responseGiverMapForComments, responseRecipientMapForComments,
                questionIdsForComments);
        if (!isError) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, StatusMessageColor.SUCCESS));
        }

        if (isUserRespondentOfSession()) {
            appendRespondent();
        } else {
            removeRespondent();
        }

        boolean isSubmissionEmailRequested = "on".equals(getRequestParamValue(Const.ParamsNames.SEND_SUBMISSION_EMAIL));
        if (!isError && isSendSubmissionEmail && isSubmissionEmailRequested) {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            Assumption.assertNotNull(session);

            String user = account == null ? null : account.googleId;
            String unregisteredStudentEmail = student == null ? null : student.email;
            String unregisteredStudentRegisterationKey = student == null ? null : student.key;
            StudentAttributes student = null;
            InstructorAttributes instructor = null;
            if (user != null) {
                student = logic.getStudentForGoogleId(courseId, user);
                instructor = logic.getInstructorForGoogleId(courseId, user);
            }
            if (student == null && unregisteredStudentEmail != null) {
                student = StudentAttributes
                        .builder("", unregisteredStudentEmail, unregisteredStudentEmail)
                        .withKey(unregisteredStudentRegisterationKey)
                        .build();
            }
            Assumption.assertFalse(student == null && instructor == null);

            try {
                Calendar timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                EmailWrapper email = instructor == null
                        ? new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForStudent(session,
                                student, timestamp)
                        : new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session,
                                instructor, timestamp);
                emailSender.sendEmail(email);
            } catch (EmailSendingException e) {
                log.severe("Submission confirmation email failed to send: "
                           + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return createSpecificRedirectResult();
    }

    private void extractFeedbackResponseCommentsDataForResponse(int questionIndx,
            int responseIndx, FeedbackQuestionAttributes questionAttributes,
            FeedbackResponseAttributes response) {

        if (questionAttributes.getQuestionDetails().isStudentsCommentsOnResponsesAllowed()) {
            String commentIndxForNewComment = "-" + responseIndx + "-" + Const.GIVER_INDEX_FOR_FEEDBACK_SUBMISSION_PAGE
                    + "-" + questionIndx;
            extractCommentsDataForNewComments(response, questionAttributes, commentIndxForNewComment);

            if (response.getId() != null) {
                List<FeedbackResponseCommentAttributes> previousComments =
                        logic.getFeedbackResponseCommentsForResponse(response.getId());

                int totalNumberOfComments = previousComments.size();
                filterCommentsOfUser(response.giver, previousComments);

                if (!previousComments.isEmpty()) {
                    for (int i = 1; i <= totalNumberOfComments; i++) {
                        String commentIndxForUpdatingComment = "-" + responseIndx + "-"
                                + Const.GIVER_INDEX_FOR_FEEDBACK_SUBMISSION_PAGE + "-" + questionIndx + "-" + i;
                        extractCommentsDataForUpdatedComments(response, questionAttributes, commentIndxForUpdatingComment);
                    }
                }
            }
        }
    }

    private void extractCommentsDataForNewComments(FeedbackResponseAttributes response,
            FeedbackQuestionAttributes questionAttributes, String commentIndxForNewComment) {
        String commentText =
                getRequestParamValue(
                        Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT + commentIndxForNewComment);

        if (!StringHelper.isEmpty(commentText)) {

            questionIdsForComments.put(commentIndxForNewComment, questionAttributes.getId());
            responseGiverMapForComments.put(commentIndxForNewComment, response.giver);
            responseRecipientMapForComments.put(commentIndxForNewComment, response.recipient);
            commentsToAddText.put(commentIndxForNewComment, commentText);
        }
    }

    private void extractCommentsDataForUpdatedComments(FeedbackResponseAttributes response,
            FeedbackQuestionAttributes questionAttributes, String commentIndxForUpdatingComment) {
        String editedCommentText =
                getRequestParamValue(
                        Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT + commentIndxForUpdatingComment);
        String commentId =
                getRequestParamValue(
                        Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID + commentIndxForUpdatingComment);

        if (commentId != null) {
            FeedbackResponseCommentAttributes commentCheck =
                    logic.getFeedbackResponseComment(Long.parseLong(commentId));

            String showCommentTo = getRequestParamValue(
                    Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO + commentIndxForUpdatingComment);
            String showGiverNameTo = getRequestParamValue(
                    Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO + commentIndxForUpdatingComment);

            String initialShowCommentToString =
                    StringHelper.removeEnclosingSquareBrackets(commentCheck.showCommentTo.toString());
            String initialShowGiverNameToString =
                    StringHelper.removeEnclosingSquareBrackets(commentCheck.showGiverNameTo.toString());

            boolean areVisibilityOptionsChanged =
                    !showCommentTo.equals(initialShowCommentToString)
                    || !showGiverNameTo.equals(initialShowGiverNameToString);

            if (editedCommentText != null && !StringHelper.isEmpty(editedCommentText)
                    && !commentCheck.commentText.getValue().equals(editedCommentText)
                     || areVisibilityOptionsChanged) {

                questionIdsForComments.put(commentIndxForUpdatingComment, questionAttributes.getId());
                commentsToUpdateId.put(commentIndxForUpdatingComment, commentId);
                commentsToUpdateText.put(commentIndxForUpdatingComment, editedCommentText);
                responseGiverMapForComments.put(commentIndxForUpdatingComment, response.giver);
                responseRecipientMapForComments.put(commentIndxForUpdatingComment, response.recipient);
            }
        }
    }

    private void saveResponsesComments(Map<String, String> commentsToAddText,
            Map<String, String> responseGiverMapForComments,
            Map<String, String> responseRecipientMapForComments,
            Map<String, String> questionIdsForComments) throws EntityDoesNotExistException {

        for (String commentIndx : commentsToAddText.keySet()) {

            String questionId = questionIdsForComments.get(commentIndx);
            String giver = responseGiverMapForComments.get(commentIndx);
            String recipient = responseRecipientMapForComments.get(commentIndx);
            FeedbackResponseAttributes responseToAddComment = logic.getFeedbackResponse(questionId, giver, recipient);
            String commentText = commentsToAddText.get(commentIndx);
            String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO + commentIndx);
            String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO + commentIndx);
            String giverRole = getRequestParamValue(Const.ParamsNames.COMMENT_GIVER_ROLE + commentIndx);

            createCommentsForResponses(courseId, feedbackSessionName, giver, questionId,
                    responseToAddComment, commentText, giverRole, showCommentTo, showGiverNameTo);
        }
    }

    private void updateResponsesComments(Map<String, String> commentToUpdateId, Map<String, String> commentToUpdateText,
            Map<String, String> responseGiverMapForComments, Map<String, String> responseRecipientMapForComments,
            Map<String, String> questionIdsForComment) throws EntityDoesNotExistException {

        for (String commentIndx : commentToUpdateId.keySet()) {

            String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO + commentIndx);
            String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO + commentIndx);
            String commentId = commentToUpdateId.get(commentIndx);
            String updatedCommentText = commentToUpdateText.get(commentIndx);
            String giverRole = getRequestParamValue(Const.ParamsNames.COMMENT_GIVER_ROLE + commentIndx);
            FeedbackResponseAttributes responseToEditComment =
                    logic.getFeedbackResponse(questionIdsForComment.get(commentIndx),
                            responseGiverMapForComments.get(commentIndx), responseRecipientMapForComments.get(commentIndx));

            updateResponseComment(
                    giverRole, showCommentTo, showGiverNameTo, commentId, responseToEditComment, updatedCommentText);
        }
    }

    private void updateResponseComment(String giverRole, String showCommentTo,
            String showGiverNameTo, String feedbackResponseCommentId,
            FeedbackResponseAttributes response, String commentText) throws EntityDoesNotExistException {

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder(courseId, feedbackSessionName, response.giver, new Text(commentText))
                .withCreatedAt(new Date())
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .withGiverRole(giverRole)
                .build();
        feedbackResponseComment.setId(Long.parseLong(feedbackResponseCommentId));

        //Edit visibility settings
        feedbackResponseComment.showCommentTo = new ArrayList<>();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String viewer : showCommentToArray) {
                feedbackResponseComment.showCommentTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }
        feedbackResponseComment.showGiverNameTo = new ArrayList<>();
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            for (String viewer : showGiverNameToArray) {
                feedbackResponseComment.showGiverNameTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }

        try {
            FeedbackResponseCommentAttributes updatedComment =
                    logic.updateFeedbackResponseComment(feedbackResponseComment);
            //TODO: move putDocument to task queue
            logic.putDocument(updatedComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.WARNING));
            isError = true;
        }

        if (!isError) {
            statusToAdmin += "FeedbackSubmitEditSaveAction:<br>"
                           + "Editing feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                           + "in course/feedback session: " + feedbackResponseComment.courseId + "/"
                           + feedbackResponseComment.feedbackSessionName + "<br>"
                           + "by: " + feedbackResponseComment.giverEmail + "<br>"
                           + "comment text: " + feedbackResponseComment.commentText.getValue();
        }
    }

    /**
     * If the {@code response} is an existing response, check that
     * the questionId and responseId that it has
     * is in {@code data.bundle.questionResponseBundle}.
     * @param response  a response which has non-null id
     */
    private boolean isExistingResponseValid(FeedbackResponseAttributes response) {

        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = data.bundle.getQuestionAttributes(questionId);

        if (!data.bundle.questionResponseBundle.containsKey(question)) {
            // question id is invalid
            return false;
        }

        List<FeedbackResponseAttributes> existingResponses = data.bundle.questionResponseBundle.get(question);
        List<String> existingResponsesId = new ArrayList<>();
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            existingResponsesId.add(existingResponse.getId());
        }

        // checks if response id is valid
        return existingResponsesId.contains(response.getId());
    }

    private void addToPendingResponses(FeedbackResponseAttributes response) {
        boolean isExistingResponse = response.getId() != null;
        if (isExistingResponse) {
            // Delete away response if any empty fields
            if (response.responseMetaData.getValue().isEmpty() || response.recipient.isEmpty()) {
                responsesToDelete.add(response);
                return;
            }
            responsesToUpdate.add(response);
        } else if (!response.responseMetaData.getValue().isEmpty()
                   && !response.recipient.isEmpty()) {
            responsesToSave.add(response);
        }
    }

    private void saveNewReponses(List<FeedbackResponseAttributes> responsesToSave)
            throws EntityDoesNotExistException {
        try {
            logic.createFeedbackResponses(responsesToSave);
            hasValidResponse = true;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
    }

    private void deleteResponses(List<FeedbackResponseAttributes> responsesToDelete) {
        for (FeedbackResponseAttributes response : responsesToDelete) {
            logic.deleteFeedbackResponse(response);
        }
    }

    private void updateResponses(List<FeedbackResponseAttributes> responsesToUpdate)
            throws EntityDoesNotExistException {
        for (FeedbackResponseAttributes response : responsesToUpdate) {
            try {
                logic.updateFeedbackResponse(response);
                hasValidResponse = true;
            } catch (EntityAlreadyExistsException | InvalidParametersException e) {
                setStatusForException(e);
            }
        }
    }

    private FeedbackResponseAttributes extractFeedbackResponseData(
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx,
            FeedbackQuestionAttributes feedbackQuestionAttributes) {

        FeedbackQuestionDetails questionDetails = feedbackQuestionAttributes.getQuestionDetails();
        FeedbackResponseAttributes response = new FeedbackResponseAttributes();

        // This field can be null if the response is new
        response.setId(getRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-" + questionIndx + "-" + responseIndx));

        response.feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, response.feedbackSessionName);

        response.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, response.courseId);

        response.feedbackQuestionId = getRequestParamValue(
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx,
                response.feedbackQuestionId);
        Assumption.assertEquals("feedbackQuestionId Mismatch", feedbackQuestionAttributes.getId(),
                                response.feedbackQuestionId);

        response.recipient = getRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + questionIndx + "-" + responseIndx);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + questionIndx + "-"
                + responseIndx, response.recipient);

        String feedbackQuestionType = getRequestParamValue(
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-" + questionIndx);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-" + questionIndx,
                feedbackQuestionType);
        response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);

        FeedbackParticipantType recipientType = feedbackQuestionAttributes.recipientType;
        if (recipientType == FeedbackParticipantType.INSTRUCTORS || recipientType == FeedbackParticipantType.NONE) {
            response.recipientSection = Const.DEFAULT_SECTION;
        } else if (recipientType == FeedbackParticipantType.TEAMS) {
            response.recipientSection = logic.getSectionForTeam(courseId, response.recipient);
        } else if (recipientType == FeedbackParticipantType.STUDENTS) {
            StudentAttributes student = logic.getStudentForEmail(courseId, response.recipient);
            response.recipientSection = student == null ? Const.DEFAULT_SECTION : student.section;
        } else {
            response.recipientSection = getUserSectionForCourse();
        }

        // This field can be null if the question is skipped
        String paramName = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionIndx + "-" + responseIndx;
        String[] answer = getRequestParamValues(paramName);

        if (questionDetails.isQuestionSkipped(answer)) {
            response.responseMetaData = new Text("");
        } else {
            FeedbackResponseDetails responseDetails =
                    FeedbackResponseDetails.createResponseDetails(answer, questionDetails.getQuestionType(),
                                                                  questionDetails, requestParameters,
                                                                  questionIndx, responseIndx);
            response.setResponseDetails(responseDetails);
        }

        return response;
    }

    /**
     * To be used to set any extra parameters or attributes that
     * a class inheriting FeedbackSubmissionEditSaveAction requires.
     */
    protected abstract void setAdditionalParameters() throws EntityDoesNotExistException;

    /**
     * To be used to test any constraints that a class inheriting FeedbackSubmissionEditSaveAction
     * needs. For example, this is used in moderations that check that instructors did not
     * respond to any question that they did not have access to during moderation.
     *
     * <p>Called after FeedbackSubmissionEditPageData data is set, and after setAdditionalParameters
     */
    protected abstract void checkAdditionalConstraints();

    /**
     * Note that when overriding this method, this should not use {@code respondingStudentList}
     * or {@code respondingInstructorList} of {@code FeedbackSessionAttributes}, because this method
     * is used to update {@code respondingStudentList} and {@code respondingInstructorList}.
     *
     * @return true if user has responses in the feedback session
     */
    protected boolean isUserRespondentOfSession() {
        // if there is no valid response on the form submission,
        // we need to use logic to check the database to handle cases where not all questions are displayed
        // e.g. on FeedbackQuestionSubmissionEditSaveAction,
        // or if the submitter can submit both as a student and instructor
        return hasValidResponse
            || logic.hasGiverRespondedForSession(getUserEmailForCourse(), feedbackSessionName, courseId);
    }

    private void createCommentsForResponses(String courseId, String feedbackSessionName, String userEmailForCourse,
            String questionId, FeedbackResponseAttributes response,
            String commentText, String giverRole, String showCommentTo,
            String showGiverNameTo) throws EntityDoesNotExistException {

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder(courseId, feedbackSessionName, userEmailForCourse, new Text(commentText))
                .withFeedbackResponseId(response.getId())
                .withFeedbackQuestionId(questionId)
                .withCreatedAt(new Date())
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .withGiverRole(giverRole)
                .build();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String viewer : showCommentToArray) {
                feedbackResponseComment.showCommentTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }
        feedbackResponseComment.showGiverNameTo = new ArrayList<>();
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            for (String viewer : showGiverNameToArray) {
                feedbackResponseComment.showGiverNameTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }

        FeedbackResponseCommentAttributes createdComment = null;
        try {
            createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
            logic.putDocument(createdComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.WARNING));
            isError = true;
        }

        if (!isError) {
            appendCommentActionInfoToStatusToAdmin(feedbackResponseComment);
        }
    }

    /*
     * Remove comments which are not by or for the user.
    */
    private void filterCommentsOfUser(String giverEmail, List<FeedbackResponseCommentAttributes> previousComments) {
        List<FeedbackResponseCommentAttributes> commentsToRemove = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseCommentAttributes comment : previousComments) {
            if (!comment.giverEmail.equals(giverEmail)) {
                commentsToRemove.add(comment);
            }
        }
        previousComments.removeAll(commentsToRemove);
    }

    protected abstract void appendRespondent();

    protected abstract void removeRespondent();

    protected abstract void verifyAccessibleForSpecificUser();

    protected abstract String getUserEmailForCourse();

    protected abstract String getUserTeamForCourse();

    protected abstract String getUserSectionForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract void setStatusToAdmin();

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);

    protected abstract RedirectResult createSpecificRedirectResult();

    protected abstract void appendCommentActionInfoToStatusToAdmin(
            FeedbackResponseCommentAttributes feedbackResponseComment);
}
