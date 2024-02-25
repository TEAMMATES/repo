package teammates.sqllogic.core;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;

/**
 * Handles operations related to feedback response comments.
 *
 * @see FeedbackResponseComment
 * @see FeedbackResponseCommentsDb
 */
public final class FeedbackResponseCommentsLogic {

    private static final FeedbackResponseCommentsLogic instance = new FeedbackResponseCommentsLogic();
    private FeedbackResponseCommentsDb frcDb;

    private FeedbackResponseCommentsLogic() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code FeedbackResponseCommentsLogic}.
     */
    void initLogicDependencies(FeedbackResponseCommentsDb frcDb) {
        this.frcDb = frcDb;
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public FeedbackResponseComment getFeedbackResponseComment(Long id) {
        return frcDb.getFeedbackResponseComment(id);
    }

    /**
     * Gets all feedback response comments for a response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentForResponse(UUID feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }

    /**
     * Gets all response comments for a response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForResponse(UUID feedbackResponseId) {
        assert feedbackResponseId != null;

        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }

    /**
     * Gets the comment associated with the response.
     */
    public FeedbackResponseComment getFeedbackResponseCommentForResponseFromParticipant(
            UUID feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Creates a feedback response comment.
     * @throws EntityAlreadyExistsException if the comment alreadty exists
     * @throws InvalidParametersException if the comment is invalid
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment frc)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return frcDb.createFeedbackResponseComment(frc);
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(Long frcId) {
        frcDb.deleteFeedbackResponseComment(frcId);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseComment}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseComment updateFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException, EntityDoesNotExistException {

        return frcDb.updateFeedbackResponseComment(feedbackResponseComment);
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public FeedbackResponseComment updateFeedbackResponseComment(Long frcId,
            FeedbackResponseCommentUpdateRequest updateRequest, String updaterEmail)
            throws EntityDoesNotExistException {
        FeedbackResponseComment comment = frcDb.getFeedbackResponseComment(frcId);
        if (comment == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback response comment that does not exist.");
        }

        comment.setCommentText(updateRequest.getCommentText());
        comment.setShowCommentTo(updateRequest.getShowCommentTo());
        comment.setShowGiverNameTo(updateRequest.getShowGiverNameTo());
        comment.setLastEditorEmail(updaterEmail);

        return comment;
    }

    /**
     * Updates all feedback response comments with new emails.
     */
    public void updateFeedbackResponseCommentsEmails(String courseId, String oldEmail, String updatedEmail) {
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
    }

    /**
     * Updates all feedback response comments with new sections.
     */
    public void updateFeedbackResponseCommentsForResponse(FeedbackResponse response)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseComment> comments = getFeedbackResponseCommentForResponse(response.getId());
        for (FeedbackResponseComment comment : comments) {
            comment.setGiverSection(response.getGiverSection());
            comment.setRecipientSection(response.getRecipientSection());
            frcDb.updateFeedbackResponseComment(comment);
        }
    }

}
