package teammates.ui.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * API output format for session results, including statistics.
 */
public class SessionResultsData extends ApiOutput {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

    final List<QuestionOutput> questions = new ArrayList<>();
    FeedbackSessionData feedbackSession;

    SessionResultsData() {
        // use factory method instead
    }

    /**
     * Factory method to construct API output for instructor.
     */
    public static SessionResultsData initForInstructor(SessionResultsBundle bundle) {
        SessionResultsData sessionResultsData = new SessionResultsData();
        sessionResultsData.feedbackSession = new FeedbackSessionData(bundle.getFeedbackSession());

        Map<String, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((questionId, responses) -> {
            FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(questionId);
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, null, bundle));
            // put normal responses
            List<ResponseOutput> allResponses = buildResponsesForInstructor(responses, bundle, false);
            qnOutput.allResponses.addAll(allResponses);

            // put missing responses
            List<FeedbackResponseAttributes> missingResponses = bundle.getQuestionMissingResponseMap().get(questionId);
            qnOutput.allResponses.addAll(buildResponsesForInstructor(missingResponses, bundle, true));

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    /**
     * Factory method to construct API output for student.
     */
    public static SessionResultsData initForStudent(SessionResultsBundle bundle, StudentAttributes student) {
        SessionResultsData sessionResultsData = new SessionResultsData();
        sessionResultsData.feedbackSession = new FeedbackSessionData(bundle.getFeedbackSession());
        sessionResultsData.feedbackSession.hideInformationForStudent();

        Map<String, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((questionId, responses) -> {
            FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(questionId);
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, student.getEmail(), bundle));
            Map<String, List<ResponseOutput>> otherResponsesMap = new HashMap<>();

            if (questionDetails.isIndividualResponsesShownToStudents()) {
                for (FeedbackResponseAttributes response : responses) {
                    boolean canUserSeeResponses = question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
                    boolean isUserGiver = student.getEmail().equals(response.getGiver());
                    boolean isUserRecipient = student.getEmail().equals(response.getRecipient());
                    ResponseOutput responseOutput = buildSingleResponseForStudent(response, bundle, student);

                    if (canUserSeeResponses && isUserRecipient) {
                        qnOutput.responsesToSelf.add(responseOutput);
                    }

                    if (isUserGiver) {
                        qnOutput.responsesFromSelf.add(responseOutput);
                    }

                    if (!isUserRecipient && !isUserGiver) {
                        // we don't need care about the keys of the map here
                        // as only the values of the map will be used
                        otherResponsesMap.computeIfAbsent(response.getRecipient(), k -> new ArrayList<>())
                                .add(responseOutput);
                    }

                    qnOutput.allResponses.add(responseOutput);
                }
            }
            qnOutput.otherResponses.addAll(otherResponsesMap.values());

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    private static ResponseOutput buildSingleResponseForStudent(
            FeedbackResponseAttributes response, SessionResultsBundle bundle, StudentAttributes student) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());

        // process giver
        boolean isUserGiver = student.getEmail().equals(response.getGiver());
        boolean isUserTeamGiver = question.giverType == FeedbackParticipantType.TEAMS
                && student.getTeam().equals(response.getGiver());
        String giverName = "";
        String giverTeam = "";
        if (isUserTeamGiver) {
            giverName = String.format("Your Team (%s)", response.getGiver());
            giverTeam = response.getGiver();
        } else if (isUserGiver) {
            giverName = "You";
            giverTeam = student.team;
        } else {
            // we don't want student to figure out who is who by using the hash
            giverName = removeAnonymousHash(getGiverNameOfResponse(response, bundle).getName());
        }

        // process recipient
        boolean isUserRecipient = student.getEmail().equals(response.getRecipient());
        boolean isUserTeamRecipient = question.getRecipientType() == FeedbackParticipantType.TEAMS
                && student.getTeam().equals(response.getRecipient());
        String recipientName = "";
        String recipientTeam = "";
        if (isUserRecipient) {
            recipientName = "You";
            recipientTeam = student.team;
        } else if (isUserTeamRecipient) {
            recipientName = String.format("Your Team (%s)", response.getRecipient());
            recipientTeam = response.getRecipient();
        } else {
            // we don't want student to figure out who is who by using the hash
            recipientName = removeAnonymousHash(getRecipientNameOfResponse(response, bundle).getName());
            if (!recipientName.contains(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT)) {
                recipientTeam = bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getTeamName();
            }
        }

        // process comments
        List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response.getId(), Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        return ResponseOutput.builder()
                .withResponseId(response.getId())
                .withGiver(giverName)
                .withGiverLastName(null)
                .withGiverTeam(giverTeam)
                .withGiverEmail(null)
                .withRelatedGiverEmail(null)
                .withGiverSection(response.getGiverSection())
                .withRecipient(recipientName)
                .withRecipientLastName(null)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(null)
                .withRecipientSection(response.getRecipientSection())
                .withResponseDetails(response.getResponseDetails())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    private static List<ResponseOutput> buildResponsesForInstructor(
            List<FeedbackResponseAttributes> responses, SessionResultsBundle bundle, boolean areMissingResponses) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackResponseAttributes response : responses) {
            output.add(buildSingleResponseForInstructor(response, bundle, areMissingResponses));
        }

        return output;
    }

    private static ResponseOutput buildSingleResponseForInstructor(
            FeedbackResponseAttributes response, SessionResultsBundle bundle, boolean isMissingResponse) {
        // process giver
        String giverEmail = null;
        String relatedGiverEmail = null;
        if (bundle.isResponseGiverVisible(response)) {
            giverEmail = response.getGiver();
            relatedGiverEmail = response.getGiver();

            if (bundle.getRoster().isTeamInCourse(giverEmail)) {
                // remove recipient email as it is a team name
                relatedGiverEmail =
                        bundle.getRoster().getTeamToMembersTable().get(giverEmail).iterator().next().getEmail();
                giverEmail = null;
            }
        }
        NameInfo giverNameInfo = getGiverNameOfResponse(response, bundle);
        String giverName = giverNameInfo.getName();
        String giverLastName = giverNameInfo.getLastName();
        String giverTeam = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
        String giverSection = response.getGiverSection();
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());
        if (question.giverType == FeedbackParticipantType.INSTRUCTORS) {
            InstructorAttributes instructor = bundle.getRoster().getInstructorForEmail(response.getGiver());
            giverName = instructor.getName();
            giverLastName = StringHelper.splitName(giverName)[1]; // get the last name from full name
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            giverSection = Const.DEFAULT_SECTION;
        }

        // process recipient
        String recipientEmail = null;
        NameInfo recipientNameInfo = getRecipientNameOfResponse(response, bundle);
        String recipientName = recipientNameInfo.getName();
        String recipientLastName = recipientNameInfo.getLastName();
        String recipientTeam =
                bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getTeamName();
        String recipientSection = response.getRecipientSection();
        if (question.recipientType == FeedbackParticipantType.INSTRUCTORS) {
            InstructorAttributes instructor = bundle.getRoster().getInstructorForEmail(response.getRecipient());
            recipientName = instructor.getName();
            recipientLastName = StringHelper.splitName(giverName)[1]; // get the last name from full name
            recipientTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            recipientSection = Const.DEFAULT_SECTION;
        }
        if (bundle.isResponseRecipientVisible(response)) {
            recipientEmail = response.getRecipient();

            if (bundle.getRoster().isTeamInCourse(recipientEmail)) {
                // remove recipient email as it is a team name
                recipientEmail = null;
            } else if (Const.GENERAL_QUESTION.equals(recipientEmail)) {
                // general recipient does not have email
                recipientEmail = null;
            }
        }

        // process comments
        List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response.getId(), Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        return ResponseOutput.builder()
                .withIsMissingResponse(isMissingResponse)
                .withResponseId(response.getId())
                .withGiver(giverName)
                .withGiverLastName(giverLastName)
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withRelatedGiverEmail(relatedGiverEmail)
                .withGiverSection(giverSection)
                .withRecipient(recipientName)
                .withRecipientLastName(recipientLastName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSection(recipientSection)
                .withResponseDetails(response.getResponseDetails())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    /**
     * Gets giver name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static NameInfo getGiverNameOfResponse(FeedbackResponseAttributes response, SessionResultsBundle bundle) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());
        FeedbackParticipantType participantType = question.giverType;

        CourseRoster.ParticipantInfo userInfo = bundle.getRoster().getInfoForIdentifier(response.getGiver());
        String name = userInfo.getName();
        String lastName = userInfo.getLastName();
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                && bundle.getRoster().isStudentInCourse(response.getGiver())) {
            // user gives responses on behalf of the team (legacy implementation), the name should be the team name instead
            name = userInfo.getTeamName();
            lastName = userInfo.getTeamName();
        }
        if (!bundle.isResponseGiverVisible(response)) {
            name = SessionResultsBundle.getAnonName(participantType, name);
            lastName = null;
        }

        return new NameInfo(name, lastName);
    }

    /**
     * Gets recipient name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static NameInfo getRecipientNameOfResponse(FeedbackResponseAttributes response, SessionResultsBundle bundle) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());
        FeedbackParticipantType participantType = question.getRecipientType();
        if (participantType == FeedbackParticipantType.SELF) {
            // recipient type for self-feedback is the same as the giver type
            participantType = question.getGiverType();
        }

        CourseRoster.ParticipantInfo userInfo = bundle.getRoster().getInfoForIdentifier(response.getRecipient());
        String name = userInfo.getName();
        String lastName = userInfo.getLastName();
        if (response.getRecipient().equals(Const.GENERAL_QUESTION)) {
            // for general question
            name = Const.USER_NOBODY_TEXT;
            lastName = null;
        }
        if (!bundle.isResponseRecipientVisible(response)) {
            name = SessionResultsBundle.getAnonName(participantType, name);
            lastName = null;
        }

        return new NameInfo(name, lastName);
    }

    private static Queue<CommentOutput> buildComments(List<FeedbackResponseCommentAttributes> feedbackResponseComments,
                                                      SessionResultsBundle bundle) {
        LinkedList<CommentOutput> outputs = new LinkedList<>();

        CommentOutput participantComment = null;
        for (FeedbackResponseCommentAttributes comment : feedbackResponseComments) {
            if (comment.isCommentFromFeedbackParticipant()) {
                // participant comment will not need these fields
                participantComment = CommentOutput.builder(comment)
                        .withCommentGiver(null)
                        .withCommentGiverName(null)
                        .withLastEditorEmail(null)
                        .withLastEditorName(null)
                        .build();
            } else {
                String giverEmail = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String giverName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String lastEditorEmail = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String lastEditorName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                if (bundle.isCommentGiverVisible(comment)) {
                    giverEmail = comment.getCommentGiver();
                    giverName = bundle.getRoster().getInfoForIdentifier(comment.getCommentGiver()).getName();
                    lastEditorEmail = comment.getLastEditorEmail();
                    lastEditorName = bundle.getRoster().getInfoForIdentifier(comment.getLastEditorEmail()).getName();
                }
                outputs.add(CommentOutput.builder(comment)
                        .withCommentGiver(giverEmail)
                        .withCommentGiverName(giverName)
                        .withLastEditorEmail(lastEditorEmail)
                        .withLastEditorName(lastEditorName)
                        .build());
            }
        }
        outputs.addFirst(participantComment);

        return outputs;
    }

    public List<QuestionOutput> getQuestions() {
        return questions;
    }

    /**
     * Data transfer object containing the processed name and last name of a participant.
     */
    private static class NameInfo {
        private final String name;
        @Nullable
        private final String lastName;

        private NameInfo(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public String getLastName() {
            return lastName;
        }
    }

    /**
     * API output format for questions in session results.
     */
    public static class QuestionOutput {

        private final FeedbackQuestionData feedbackQuestion;
        private final String questionStatistics;

        private final List<ResponseOutput> allResponses = new ArrayList<>();

        // For student view only
        private final List<ResponseOutput> responsesToSelf = new ArrayList<>();
        private final List<ResponseOutput> responsesFromSelf = new ArrayList<>();
        private final List<List<ResponseOutput>> otherResponses = new ArrayList<>();

        private QuestionOutput(FeedbackQuestionAttributes feedbackQuestionAttributes, String questionStatistics) {
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestionAttributes);
            this.questionStatistics = questionStatistics;
        }

        QuestionOutput(FeedbackQuestionAttributes feedbackQuestionAttributes,
                       List<ResponseOutput> allResponses) {
            this.questionStatistics = null;
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestionAttributes);
            this.allResponses.addAll(allResponses);
        }

        public FeedbackQuestionData getFeedbackQuestion() {
            return feedbackQuestion;
        }

        public String getQuestionStatistics() {
            return questionStatistics;
        }

        public List<ResponseOutput> getAllResponses() {
            return allResponses;
        }

        public List<ResponseOutput> getResponsesFromSelf() {
            return responsesFromSelf;
        }

        public List<ResponseOutput> getResponsesToSelf() {
            return responsesToSelf;
        }

        public List<List<ResponseOutput>> getOtherResponses() {
            return otherResponses;
        }

    }

    /**
     * API output format for question responses.
     */
    public static class ResponseOutput {

        private boolean isMissingResponse;

        private String responseId;

        private String giver;
        @Nullable
        private String giverLastName;
        /**
         * Depending on the question giver type, {@code giverIdentifier} may contain the giver's email, any team member's
         * email or null.
         */
        @Nullable
        private String relatedGiverEmail;
        private String giverTeam;
        @Nullable
        private String giverEmail;
        private String giverSection;
        private String recipient;
        @Nullable
        private String recipientLastName;
        private String recipientTeam;
        @Nullable
        private String recipientEmail;
        private String recipientSection;
        private FeedbackResponseDetails responseDetails;

        // comments
        @Nullable
        private CommentOutput participantComment;
        private List<CommentOutput> instructorComments;

        private ResponseOutput() {
            // use builder instead
        }

        /**
         * Returns a builder for {@link ResponseOutput}.
         */
        static Builder builder() {
            return new Builder();
        }

        public boolean isMissingResponse() {
            return isMissingResponse;
        }

        public String getResponseId() {
            return responseId;
        }

        public String getGiver() {
            return giver;
        }

        @Nullable
        public String getGiverLastName() {
            return giverLastName;
        }

        @Nullable
        public String getGiverEmail() {
            return giverEmail;
        }

        @Nullable
        public String getRelatedGiverEmail() {
            return relatedGiverEmail;
        }

        public String getGiverTeam() {
            return giverTeam;
        }

        public String getGiverSection() {
            return giverSection;
        }

        public String getRecipient() {
            return recipient;
        }

        @Nullable
        public String getRecipientLastName() {
            return recipientLastName;
        }

        public String getRecipientTeam() {
            return recipientTeam;
        }

        @Nullable
        public String getRecipientEmail() {
            return recipientEmail;
        }

        public String getRecipientSection() {
            return recipientSection;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

        @Nullable
        public CommentOutput getParticipantComment() {
            return participantComment;
        }

        public List<CommentOutput> getInstructorComments() {
            return instructorComments;
        }

        /**
         * Builder class for {@link ResponseOutput}.
         */
        public static final class Builder {
            private final ResponseOutput responseOutput;

            private Builder() {
                responseOutput = new ResponseOutput();
            }

            //CHECKSTYLE.OFF:MissingJavadocMethod
            private Builder withIsMissingResponse(boolean isMissingResponse) {
                responseOutput.isMissingResponse = isMissingResponse;
                return this;
            }

            Builder withResponseId(String responseId) {
                responseOutput.responseId = StringHelper.encrypt(responseId);
                return this;
            }

            Builder withGiver(String giverName) {
                responseOutput.giver = giverName;
                return this;
            }

            private Builder withGiverLastName(String giverLastName) {
                responseOutput.giverLastName = giverLastName;
                return this;
            }

            private Builder withRelatedGiverEmail(@Nullable String relatedGiverEmail) {
                responseOutput.relatedGiverEmail = relatedGiverEmail;
                return this;
            }

            private Builder withGiverTeam(String giverTeam) {
                responseOutput.giverTeam = giverTeam;
                return this;
            }

            Builder withGiverEmail(@Nullable String giverEmail) {
                responseOutput.giverEmail = giverEmail;
                return this;
            }

            Builder withGiverSection(String giverSection) {
                responseOutput.giverSection = giverSection;
                return this;
            }

            Builder withRecipient(String recipientName) {
                responseOutput.recipient = recipientName;
                return this;
            }

            private Builder withRecipientLastName(String recipientLastName) {
                responseOutput.recipientLastName = recipientLastName;
                return this;
            }

            private Builder withRecipientTeam(String recipientTeam) {
                responseOutput.recipientTeam = recipientTeam;
                return this;
            }

            Builder withRecipientEmail(@Nullable String recipientEmail) {
                responseOutput.recipientEmail = recipientEmail;
                return this;
            }

            Builder withRecipientSection(String recipientSection) {
                responseOutput.recipientSection = recipientSection;
                return this;
            }

            Builder withResponseDetails(FeedbackResponseDetails responseDetails) {
                responseOutput.responseDetails = responseDetails;
                return this;
            }

            Builder withParticipantComment(@Nullable CommentOutput participantComment) {
                responseOutput.participantComment = participantComment;
                return this;
            }

            Builder withInstructorComments(List<CommentOutput> instructorComments) {
                responseOutput.instructorComments = instructorComments;
                return this;
            }

            ResponseOutput build() {
                return responseOutput;
            }
            //CHECKSTYLE.ON:MissingJavadocMethod
        }
    }

    /**
     * API output format for response comments.
     */
    public static class CommentOutput extends FeedbackResponseCommentData {

        @Nullable
        private String commentGiverName;
        @Nullable
        private String lastEditorName;

        private CommentOutput(FeedbackResponseCommentAttributes frc) {
            // use builder instead
            super(frc);
        }

        /**
         * Returns a builder for {@link CommentOutput}.
         */
        static Builder builder(FeedbackResponseCommentAttributes frc) {
            return new Builder(frc);
        }

        @Nullable
        public String getCommentGiverName() {
            return commentGiverName;
        }

        @Nullable
        public String getLastEditorName() {
            return lastEditorName;
        }

        /**
         * Builder class for {@link CommentOutput}.
         */
        public static final class Builder {
            private final CommentOutput commentOutput;

            private Builder(FeedbackResponseCommentAttributes frc) {
                commentOutput = new CommentOutput(frc);
            }

            //CHECKSTYLE.OFF:MissingJavadocMethod
            Builder withCommentGiver(@Nullable String commentGiver) {
                commentOutput.commentGiver = commentGiver;
                return this;
            }

            Builder withCommentGiverName(@Nullable String commentGiverName) {
                commentOutput.commentGiverName = commentGiverName;
                return this;
            }

            Builder withLastEditorEmail(@Nullable String lastEditorEmail) {
                commentOutput.lastEditorEmail = lastEditorEmail;
                return this;
            }

            Builder withLastEditorName(@Nullable String lastEditorName) {
                commentOutput.lastEditorName = lastEditorName;
                return this;
            }

            CommentOutput build() {
                return commentOutput;
            }
            //CHECKSTYLE.ON:MissingJavadocMethod
        }
    }

}
