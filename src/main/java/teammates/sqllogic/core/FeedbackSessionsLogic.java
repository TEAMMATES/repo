package teammates.sqllogic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionsDb
 */
public final class FeedbackSessionsLogic {

    private static final Logger log = Logger.getLogger();

    private static final String ERROR_NON_EXISTENT_FS_STRING_FORMAT = "Trying to %s a non-existent feedback session: ";
    private static final String ERROR_NON_EXISTENT_FS_UPDATE = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "update");
    private static final String ERROR_FS_ALREADY_PUBLISH = "Error publishing feedback session: "
            + "Session has already been published.";
    private static final String ERROR_FS_ALREADY_UNPUBLISH = "Error unpublishing feedback session: "
            + "Session has already been unpublished.";

    private static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;
    private static final int NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT = 24;

    private static final FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    private FeedbackSessionsDb fsDb;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponsesLogic frLogic;
    private CoursesLogic coursesLogic;

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackSessionsDb fsDb, CoursesLogic coursesLogic,
            FeedbackResponsesLogic frLogic, FeedbackQuestionsLogic fqLogic) {
        this.fsDb = fsDb;
        this.frLogic = frLogic;
        this.fqLogic = fqLogic;
        this.coursesLogic = coursesLogic;
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(UUID id) {
        assert id != null;
        return fsDb.getFeedbackSession(id);
    }

    /**
     * Gets a feedback session for {@code feedbackSessionName} and {@code courseId}.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return fsDb.getFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets all feedback sessions of a course, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        return fsDb.getFeedbackSessionEntitiesForCourse(courseId).stream()
                .filter(fs -> fs.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    /**
     * Gets all feedback sessions of a course started after time, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        return fsDb.getFeedbackSessionEntitiesForCourseStartingAfter(courseId, after).stream()
                .filter(session -> session.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        return fsDb.getSoftDeletedFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSession> getFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {

        List<Instructor> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> coursesLogic.getCourse(instructor.getCourseId()).getDeletedAt() == null)
                .collect(Collectors.toList());

        List<FeedbackSession> fsList = new ArrayList<>();

        for (Instructor instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getFeedbackSessionsForCourse(instructor.getCourseId()));
        }

        return fsList;
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {

        List<Instructor> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> coursesLogic.getCourse(instructor.getCourseId()).getDeletedAt() == null)
                .collect(Collectors.toList());

        List<FeedbackSession> fsList = new ArrayList<>();

        for (Instructor instructor : courseNotDeletedInstructorList) {
            fsList.addAll(fsDb.getSoftDeletedFeedbackSessionsForCourse(instructor.getCourseId()));
        }

        return fsList;
    }

    /**
     * Gets all and only the feedback sessions ongoing within a range of time.
     */
    public List<FeedbackSession> getOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnsweredFeedbackSession(String feedbackSessionName, String courseId) {
        assert courseId != null;
        assert feedbackSessionName != null;

        FeedbackSession feedbackSession = fsDb.getFeedbackSession(feedbackSessionName, courseId);

        Set<String> giverSet = new HashSet<>();
        feedbackSession.getFeedbackQuestions().forEach(question -> {
            question.getFeedbackResponses().forEach(response -> {
                giverSet.add(response.getGiver());
            });
        });

        return giverSet;
    }

    /**
     * Creates a feedback session.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert session != null;
        return fsDb.createFeedbackSession(session);
    }

    /**
     * Updates a feedback session.
     *
     * @return updated feedback session
     * @throws EntityDoesNotExistException if the feedback session does not exist
     * @throws InvalidParametersException if the new fields for feedback session are invalid
     */
    public FeedbackSession updateFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityDoesNotExistException {
        return fsDb.updateFeedbackSession(session);
    }

    /**
     * Unpublishes a feedback session.
     *
     * @return the unpublished feedback session
     * @throws InvalidParametersException if session is already unpublished
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSession sessionToUnpublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToUnpublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_UNPUBLISH);
        }

        sessionToUnpublish.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);

        return sessionToUnpublish;
    }

    /**
     * Publishes a feedback session.
     *
     * @return the published feedback session
     * @throws InvalidParametersException if session is already published
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSession sessionToPublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToPublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (sessionToPublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_PUBLISH);
        }

        sessionToPublish.setResultsVisibleFromTime(Instant.now());

        return sessionToPublish;
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        FeedbackSession feedbackSession = fsDb.getFeedbackSession(feedbackSessionName, courseId);
        fsDb.deleteFeedbackSession(feedbackSession);
    }

    /**
     * Soft-deletes a specific feedback session to Recycle Bin.
     * @return the time when the feedback session is moved to the recycle bin
     */
    public Instant moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        return fsDb.softDeleteFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific feedback session from Recycle Bin.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        fsDb.restoreDeletedFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Returns true if there are any questions for the specified user type (students/instructors) to answer.
     */
    public boolean isFeedbackSessionForUserTypeToAnswer(FeedbackSession session, boolean isInstructor) {
        if (!session.isVisible()) {
            return false;
        }

        return isInstructor
                ? fqLogic.hasFeedbackQuestionsForInstructors(session.getFeedbackQuestions(), false)
                : fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions());
    }

    /**
     * Returns true if the feedback session is viewable by the given user type (students/instructors).
     */
    public boolean isFeedbackSessionViewableToUserType(FeedbackSession session, boolean isInstructor) {
        // Allow user to view the feedback session if there are questions for them
        if (isFeedbackSessionForUserTypeToAnswer(session, isInstructor)) {
            return true;
        }

        // Allow user to view the feedback session if there are any question whose responses are visible to the user
        List<FeedbackQuestion> questionsWithVisibleResponses = new ArrayList<>();
        List<FeedbackQuestion> questionsForUser = session.getFeedbackQuestions();
        for (FeedbackQuestion question : questionsForUser) {
            if (!isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)
                    || isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question)) {
                // We only need one question with visible responses for the entire session to be visible
                questionsWithVisibleResponses.add(question);
                break;
            }
        }

        return session.isVisible() && !questionsWithVisibleResponses.isEmpty();
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If feedback session consists of all team questions, session is attempted by student only
     * if someone from the team has responded. If feedback session has some individual questions,
     * session is attempted only if the student has responded to any of the individual questions
     * (regardless of the completion status of the team questions).</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSession session, String userEmail, String userTeam) {
        assert session != null;
        assert userEmail != null;
        assert userTeam != null;

        if (!fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions())) {
            // if there are no questions for student, session is attempted
            return true;
        } else if (fqLogic.hasFeedbackQuestionsForGiverType(
                session.getFeedbackQuestions(), FeedbackParticipantType.STUDENTS)) {
            // case where there are some individual questions
            return frLogic.hasGiverRespondedForSession(userEmail, session.getFeedbackQuestions());
        } else {
            // case where all are team questions
            return frLogic.hasGiverRespondedForSession(userTeam, session.getFeedbackQuestions());
        }
    }

    /**
     * Checks whether an instructor has attempted a feedback session.
     *
     * <p>If there is no question for instructors, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByInstructor(FeedbackSession session, String userEmail) {
        assert session != null;
        assert userEmail != null;

        if (frLogic.hasGiverRespondedForSession(userEmail, session.getFeedbackQuestions())) {
            return true;
        }

        // if there is no question for instructor, session is attempted
        return !fqLogic.hasFeedbackQuestionsForInstructors(session.getFeedbackQuestions(), session.isCreator(userEmail));
    }

    /**
     * After an update to feedback session's fields, may need to adjust the email status of the session.
     * @param session recently updated session.
     */
    public void adjustFeedbackSessionEmailStatusAfterUpdate(FeedbackSession session) {
        // reset isOpenEmailSent if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (session.isOpenEmailSent()) {
            session.setOpenEmailSent(session.isOpened());

            // also reset isOpeningSoonEmailSent
            session.setOpeningSoonEmailSent(
                    session.isOpened() || session.isOpeningInHours(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT)
            );
        }

        // reset isClosedEmailSent if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (session.isClosedEmailSent()) {
            session.setClosedEmailSent(session.isClosed());

            // also reset isClosingSoonEmailSent
            session.setClosingSoonEmailSent(
                    session.isClosed() || session.isClosedAfter(NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)
            );
        }

        // reset isPublishedEmailSent if the session has been published but is
        // going to be unpublished now, or else leave it as sent if so.
        if (session.isPublishedEmailSent()) {
            session.setPublishedEmailSent(session.isPublished());
        }
    }

    /**
     * Returns a list of sessions that are going to open in 24 hours.
     */
    public List<FeedbackSession> getFeedbackSessionsOpeningWithinTimeLimit() {
        List<FeedbackSession> requiredSessions = new ArrayList<>();
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpeningSoonEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSession session : sessions) {
            if (session.isOpeningWithinTimeLimit(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT)
                    && session.getCourse().getDeletedAt() == null) {
                requiredSessions.add(session);
            }
        }

        log.info(String.format("Number of sessions under consideration after filtering: %d",
                requiredSessions.size()));
        return requiredSessions;
    }
}
