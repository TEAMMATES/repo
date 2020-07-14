package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.SanitizationHelper;

/**
 * An association class that represents the association
 * Giver --> [comments about] --> FeedbackResponse.
 * Giver can be a student or an instructor or a team
 */
@Entity
@Index
public class FeedbackResponseComment extends BaseEntity {

    @Id
    private transient Long feedbackResponseCommentId;

    /** The foreign key to locate the Course object. */
    private String courseId;

    /** The foreign key to locate the FeedbackSession object. */
    private String feedbackSessionName;

    /** The foreign key to locate the FeedbackQuestion object. */
    private String feedbackQuestionId;

    /**
     * The giver of the comment.
     *
     * <p>It is email in case when comment giver is a student or instructor, and team name in case of team.
     */
    private String giverEmail;

    /**
     * Role of a comment giver.
     *
     * <p>Can only be INSTRUCTORS, STUDENTS or TEAMS.
     */
    private FeedbackParticipantType commentGiverType;

    /** The foreign key to locate the FeedbackResponse object commented on. */
    private String feedbackResponseId;

    /** Response giver section. */
    private String giverSection;

    /** Response receiver section. */
    private String receiverSection;

    private List<FeedbackParticipantType> showCommentTo = new ArrayList<>();

    private List<FeedbackParticipantType> showGiverNameTo = new ArrayList<>();

    private Boolean isVisibilityFollowingFeedbackQuestion;

    /** True if the comment is given by a feedback participant. */
    private boolean isCommentFromFeedbackParticipant;

    /** The creation time of this comment. */
    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    /** The comment from giver about the feedback response. */
    @Unindex
    private Text commentText;

    /** The e-mail of the account that last edited the comment. */
    private String lastEditorEmail;

    /** The time in which the comment is last edited. */
    @Translate(InstantTranslatorFactory.class)
    private Instant lastEditedAt;

    @SuppressWarnings("unused")
    private FeedbackResponseComment() {
        // required by Objectify
    }

    public FeedbackResponseComment(String courseId, String feedbackSessionName, String feedbackQuestionId,
            String giverEmail, FeedbackParticipantType commentGiverType, String feedbackResponseId, Instant createdAt,
            String commentText, String giverSection, String receiverSection, List<FeedbackParticipantType> showCommentTo,
            List<FeedbackParticipantType> showGiverNameTo, String lastEditorEmail, Instant lastEditedAt,
            boolean isCommentFromFeedbackParticipant, boolean isVisibilityFollowingFeedbackQuestion) {
        this.feedbackResponseCommentId = null; // Auto generated by GAE
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackQuestionId = feedbackQuestionId;
        this.giverEmail = giverEmail;
        this.commentGiverType = commentGiverType;
        this.feedbackResponseId = feedbackResponseId;
        this.createdAt = createdAt;
        setCommentText(SanitizationHelper.sanitizeForRichText(commentText));
        this.giverSection = giverSection;
        this.receiverSection = receiverSection;
        this.showCommentTo = showCommentTo == null ? new ArrayList<>() : showCommentTo;
        this.showGiverNameTo = showGiverNameTo == null ? new ArrayList<>() : showGiverNameTo;
        this.isVisibilityFollowingFeedbackQuestion = isVisibilityFollowingFeedbackQuestion;
        this.lastEditorEmail = lastEditorEmail == null ? giverEmail : lastEditorEmail;
        this.lastEditedAt = lastEditedAt == null ? this.createdAt : lastEditedAt;
        this.isCommentFromFeedbackParticipant = isCommentFromFeedbackParticipant;
    }

    /**
     * Use only if the comment already persisted in the datastore and id generated by GAE.
     * @return the feedbackResponseCommentId
     */
    public Long getFeedbackResponseCommentId() {
        return feedbackResponseCommentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }

    /**
     * Gets whether the visibility setting of the comment follow the corresponding question.
     */
    public boolean getIsVisibilityFollowingFeedbackQuestion() {
        if (this.isVisibilityFollowingFeedbackQuestion == null) {
            // true as the default value if the field is null
            return true;
        }
        return this.isVisibilityFollowingFeedbackQuestion;
    }

    public void setIsVisibilityFollowingFeedbackQuestion(Boolean isVisibilityFollowingFeedbackQuestion) {
        this.isVisibilityFollowingFeedbackQuestion = isVisibilityFollowingFeedbackQuestion;
    }

    public String getGiverEmail() {
        return giverEmail;
    }

    /**
     * Sets the giver email of the response comment.
     *
     * @param giverEmail the giverEmail to set.
     *         This is the email used by the user in the course, not the one associated with the user's google account.
     */
    public void setGiverEmail(String giverEmail) {
        this.giverEmail = giverEmail;
    }

    /**
     * Gets the giver type of the comment.
     */
    public FeedbackParticipantType getCommentGiverType() {
        // TODO: Remove after data migration
        if (commentGiverType == null) {
            return FeedbackParticipantType.INSTRUCTORS;
        }
        return commentGiverType;
    }

    /**
     * Sets the giver type of the comment.
     */
    public void setCommentGiverType(FeedbackParticipantType commentGiverType) {
        // TODO: Remove after data migration
        if (commentGiverType == null) {
            this.commentGiverType = FeedbackParticipantType.INSTRUCTORS;
        }
        this.commentGiverType = commentGiverType;
    }

    public void setShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
        this.showCommentTo = showCommentTo;
    }

    public List<FeedbackParticipantType> getShowCommentTo() {
        return showCommentTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;
    }

    public void setFeedbackResponseId(String feedbackResponseId) {
        this.feedbackResponseId = feedbackResponseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommentText() {
        return commentText == null ? null : commentText.getValue();
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText == null ? null : new Text(commentText);
    }

    public String getGiverSection() {
        return giverSection;
    }

    public void setGiverSection(String giverSection) {
        this.giverSection = giverSection;
    }

    public String getReceiverSection() {
        return receiverSection;
    }

    public void setReceiverSection(String receiverSection) {
        this.receiverSection = receiverSection;
    }

    public void setLastEditorEmail(String lastEditorEmail) {
        this.lastEditorEmail = lastEditorEmail;
    }

    public String getLastEditorEmail() {
        return this.lastEditorEmail;
    }

    public Instant getLastEditedAt() {
        return this.lastEditedAt;
    }

    public void setLastEditedAt(Instant lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public boolean getIsCommentFromFeedbackParticipant() {
        return this.isCommentFromFeedbackParticipant;
    }

    public void setIsCommentFromFeedbackParticipant(boolean isCommentFromFeedbackParticipant) {
        this.isCommentFromFeedbackParticipant = isCommentFromFeedbackParticipant;
    }
}
