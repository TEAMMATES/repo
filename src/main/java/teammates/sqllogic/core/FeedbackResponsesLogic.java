package teammates.sqllogic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackResponse
 * @see FeedbackResponsesDb
 */
public final class FeedbackResponsesLogic {

    private static final FeedbackResponsesLogic instance = new FeedbackResponsesLogic();

    private FeedbackResponsesDb frDb;
    private UsersLogic usersLogic;
    private FeedbackQuestionsLogic fqLogic;

    private FeedbackResponsesLogic() {
        // prevent initialization
    }

    public static FeedbackResponsesLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code FeedbackResponsesLogic}.
     */
    void initLogicDependencies(FeedbackResponsesDb frDb, UsersLogic usersLogic, FeedbackQuestionsLogic fqLogic) {
        this.frDb = frDb;
        this.usersLogic = usersLogic;
        this.fqLogic = fqLogic;
    }

    /**
     * Gets a feedbackResponse or null if it does not exist.
     */
    public FeedbackResponse getFeedbackResponse(UUID frId) {
        return frDb.getFeedbackResponse(frId);
    }

    /**
     * Returns true if the responses of the question are visible to students.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToStudent(FeedbackQuestion question) {
        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            return true;
        }
        boolean isStudentRecipientType =
                   question.getRecipientType().equals(FeedbackParticipantType.STUDENTS)
                || question.getRecipientType().equals(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF)
                || question.getRecipientType().equals(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION)
                || question.getRecipientType().equals(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                || question.getRecipientType().equals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                || question.getRecipientType().equals(FeedbackParticipantType.GIVER)
                   && question.getGiverType().equals(FeedbackParticipantType.STUDENTS);

        if ((isStudentRecipientType || question.getRecipientType().isTeam())
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        return question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    /**
     * Returns true if the responses of the question are visible to instructors.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToInstructor(FeedbackQuestion question) {
        return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giverIdentifier, List<FeedbackQuestion> questions) {
        assert questions != null;

        for (FeedbackQuestion question : questions) {
            boolean hasResponse = question
                    .getFeedbackResponses()
                    .stream()
                    .anyMatch(response -> response.getGiver().equals(giverIdentifier));
            if (hasResponse) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giver, String feedbackSessionName, String courseId) {

        return frDb.hasResponsesFromGiverInSession(giver, feedbackSessionName, courseId);
    }

    /**
     * Creates a feedback response.
     * @return the created response
     * @throws InvalidParametersException if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return frDb.createFeedbackResponse(feedbackResponse);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(
                question.getId(), instructor.getEmail());
    }

    /**
     * Get existing feedback responses from student or his team for the given
     * question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestion question, Student student) {
        if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
            return getFeedbackResponsesFromTeamForQuestion(
                    question.getId(), question.getCourseId(), student.getTeamName(), null);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
    }

    private List<FeedbackResponse> getFeedbackResponsesFromTeamForQuestion(
            UUID feedbackQuestionId, String courseId, String teamName, @Nullable SqlCourseRoster courseRoster) {

        List<FeedbackResponse> responses = new ArrayList<>();
        List<Student> studentsInTeam = courseRoster == null
                ? usersLogic.getStudentsForTeam(teamName, courseId) : courseRoster.getTeamToMembersTable().get(teamName);

        for (Student student : studentsInTeam) {
            responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.getEmail()));
        }

        responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                feedbackQuestionId, teamName));
        return responses;
    }

    /**
     * Deletes all feedback responses of a question cascade its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(UUID feedbackQuestionId) {
        // delete all responses, comments of the question
        frDb.deleteFeedbackResponsesForQuestionCascade(feedbackQuestionId);
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(UUID questionId) {
        return frDb.areThereResponsesForQuestion(questionId);
    }

    /**
     * Checks whether there are responses for a course.
     */
    public boolean hasResponsesForCourse(String courseId) {
        return frDb.hasResponsesForCourse(courseId);

    }

    /**
     * Deletes all feedback responses involved an entity, cascade its associated comments.
     * Deletion will automatically be cascaded to each feedback response's comments,
     * handled by Hibernate using the OnDelete annotation.
     */
    public void deleteFeedbackResponsesForCourseCascade(String courseId, String entityEmail) {
        // delete responses from the entity
        List<FeedbackResponse> responsesFromStudent =
                getFeedbackResponsesFromGiverForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesFromStudent) {
            frDb.deleteFeedbackResponse(response);
        }

        // delete responses to the entity
        List<FeedbackResponse> responsesToStudent =
                getFeedbackResponsesForRecipientForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesToStudent) {
            frDb.deleteFeedbackResponse(response);
        }
    }

    /**
     * Gets all responses given by a user for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giver) {
        assert courseId != null;
        assert giver != null;

        return frDb.getFeedbackResponsesFromGiverForCourse(courseId, giver);
    }

    /**
     * Gets all responses received by a user for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForRecipientForCourse(
            String courseId, String recipient) {
        assert courseId != null;
        assert recipient != null;

        return frDb.getFeedbackResponsesForRecipientForCourse(courseId, recipient);
    }

    /**
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForQuestion(
            UUID feedbackQuestionId, String giver) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, giver);
    }

    /**
     * Updates the relevant responses before the deletion of a student.
     * This method takes care of the following:
     * Making existing responses of 'rank recipient question' consistent.
     */
    public void updateRankRecipientQuestionResponsesAfterDeletingStudent(String courseId) {
        List<FeedbackQuestion> filteredQuestions =
                fqLogic.getFeedbackQuestionForCourseWithType(courseId, FeedbackQuestionType.RANK_RECIPIENTS);
        SqlCourseRoster roster = new SqlCourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        for (FeedbackQuestion question : filteredQuestions) {
            makeRankRecipientQuestionResponsesConsistent(question, roster);
        }
    }

    /**
     * Makes the rankings by one giver in the response to a 'rank recipient question' consistent, after deleting a
     * student.
     * <p>
     *     Fails silently if the question type is not 'rank recipient question'.
     * </p>
     */
    private void makeRankRecipientQuestionResponsesConsistent(
            FeedbackQuestion question, SqlCourseRoster roster) {
        assert !question.getQuestionDetailsCopy().getQuestionType()
                .equals(FeedbackQuestionType.RANK_RECIPIENTS);

        FeedbackParticipantType giverType = question.getGiverType();
        List<FeedbackResponse> responses = new ArrayList<>();
        int numberOfRecipients = 0;

        switch (giverType) {
        case INSTRUCTORS:
        case SELF:
            for (Instructor instructor : roster.getInstructors()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, instructor, null, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail());
            }
            break;
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
            Student firstMemberOfTeam;
            String team;
            Map<String, List<Student>> teams = roster.getTeamToMembersTable();
            for (Map.Entry<String, List<Student>> entry : teams.entrySet()) {
                team = entry.getKey();
                firstMemberOfTeam = entry.getValue().get(0);
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, firstMemberOfTeam, roster).size();
                responses =
                        getFeedbackResponsesFromTeamForQuestion(
                                question.getId(), question.getCourseId(), team, roster);
            }
            break;
        default:
            for (Student student : roster.getStudents()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, student, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
            }
            break;
        }

        updateFeedbackResponsesForRankRecipientQuestions(responses, numberOfRecipients);
    }

    /**
     * Updates responses for 'rank recipient question', such that the ranks in the responses are consistent.
     * @param responses responses to one feedback question, from one giver
     * @param maxRank the maximum rank in each response
     */
    private void updateFeedbackResponsesForRankRecipientQuestions(
            List<FeedbackResponse> responses, int maxRank) {
        if (maxRank <= 0) {
            return;
        }

        FeedbackRankRecipientsResponseDetails responseDetails;
        boolean[] isRankUsed;
        boolean isUpdateNeeded = false;
        int answer;
        int maxUnusedRank = 0;

        // Checks whether update is needed.
        for (FeedbackResponse response : responses) {
            if (!(response instanceof FeedbackRankRecipientsResponse)) {
                continue;
            }
            responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
            answer = responseDetails.getAnswer();
            if (answer > maxRank) {
                isUpdateNeeded = true;
                break;
            }
        }

        // Updates repeatedly, until all responses are consistent.
        while (isUpdateNeeded) {
            isUpdateNeeded = false; // will be set to true again once invalid rank appears after update
            isRankUsed = new boolean[maxRank];

            // Obtains the largest unused rank.
            for (FeedbackResponse response : responses) {
                if (!(response instanceof FeedbackRankRecipientsResponse)) {
                    continue;
                }
                responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
                answer = responseDetails.getAnswer();
                if (answer <= maxRank) {
                    isRankUsed[answer - 1] = true;
                }
            }
            for (int i = maxRank - 1; i >= 0; i--) {
                if (!isRankUsed[i]) {
                    maxUnusedRank = i + 1;
                    break;
                }
            }
            assert maxUnusedRank > 0; // if update is needed, there must be at least one unused rank

            for (FeedbackResponse response : responses) {
                if (response instanceof FeedbackRankRecipientsResponse) {
                    responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
                    answer = responseDetails.getAnswer();
                    if (answer > maxUnusedRank) {
                        answer--;
                        responseDetails.setAnswer(answer);
                    }
                    if (answer > maxRank) {
                        isUpdateNeeded = true; // sets the flag to true if the updated rank is still invalid
                    }
                }
            }
        }
    }

}
