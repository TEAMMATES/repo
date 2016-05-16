package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Sanitizer;
import teammates.storage.entity.FeedbackResponseComment;

import com.google.appengine.api.datastore.Text;

/**
 * Represents a data transfer object for {@link FeedbackResponseComment} entities.
 */
public class FeedbackResponseCommentAttributes extends EntityAttributes {

    private Long feedbackResponseCommentId;
    public String courseId;
    public String feedbackSessionName;
    public String feedbackQuestionId;
    public String giverEmail;
    /* Response giver section */
    public String giverSection;
    /* Response receiver section */
    public String receiverSection;
    public String feedbackResponseId;
    public CommentSendingState sendingState = CommentSendingState.SENT;
    public List<FeedbackParticipantType> showCommentTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public boolean isVisibilityFollowingFeedbackQuestion;
    public Date createdAt;
    public Text commentText;
    public String lastEditorEmail;
    public Date lastEditedAt;

    public FeedbackResponseCommentAttributes() {
        this.feedbackResponseCommentId = null;
        this.courseId = null;
        this.feedbackSessionName = null;
        this.feedbackQuestionId = null;
        this.giverEmail = null;
        this.feedbackResponseId = null;
        this.createdAt = null;
        this.commentText = null;
        this.giverSection = "None";
        this.receiverSection = "None";
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        this.lastEditorEmail = null;
        this.lastEditedAt = null;
    }
    
    public FeedbackResponseCommentAttributes(String courseId, String feedbackSessionName, String feedbackQuestionId,
            String giverEmail, String feedbackResponseId, Date createdAt, Text commentText) {
        this(courseId, feedbackSessionName, feedbackQuestionId, giverEmail, 
                feedbackResponseId, createdAt, commentText, "None", "None");
    }

    public FeedbackResponseCommentAttributes(String courseId, String feedbackSessionName, String feedbackQuestionId,
            String giverEmail, String feedbackResponseId, Date createdAt, Text commentText,
            String giverSection, String receiverSection) {
        this.feedbackResponseCommentId = null; //Auto generated by GAE
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackQuestionId = feedbackQuestionId;
        this.giverEmail = giverEmail;
        this.feedbackResponseId = feedbackResponseId;
        this.createdAt = createdAt;
        this.commentText = commentText;
        this.giverSection = giverSection;
        this.receiverSection = receiverSection;
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        this.lastEditorEmail = giverEmail;
        this.lastEditedAt = createdAt;
    }
    
    public FeedbackResponseCommentAttributes(String courseId, String feedbackSessionName,
            String feedbackQuestionId, String feedbackResponseId) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackResponseId = feedbackResponseId;
    }
    
    public FeedbackResponseCommentAttributes(FeedbackResponseComment comment) {
        this.feedbackResponseCommentId = comment.getFeedbackResponseCommentId();
        this.courseId = comment.getCourseId();
        this.feedbackSessionName = comment.getFeedbackSessionName();
        this.feedbackQuestionId = comment.getFeedbackQuestionId();
        this.giverEmail = comment.getGiverEmail();
        this.feedbackResponseId = comment.getFeedbackResponseId();
        this.sendingState = comment.getSendingState() != null ? comment.getSendingState() : CommentSendingState.SENT;
        this.createdAt = comment.getCreatedAt();
        this.commentText = comment.getCommentText();
        this.giverSection = comment.getGiverSection() != null ? comment.getGiverSection() : "None";
        this.receiverSection = comment.getReceiverSection() != null ? comment.getReceiverSection() : "None";
        this.lastEditorEmail = comment.getLastEditorEmail() != null ?
                comment.getLastEditorEmail() : comment.getGiverEmail();
        this.lastEditedAt = comment.getLastEditedAt() != null ? comment.getLastEditedAt() : comment.getCreatedAt();
        if (comment.getIsVisibilityFollowingFeedbackQuestion() != null
                && !comment.getIsVisibilityFollowingFeedbackQuestion()) {
            this.showCommentTo = comment.getShowCommentTo();
            this.showGiverNameTo = comment.getShowGiverNameTo();
        } else {
            setDefaultVisibilityOptions();
        }
    }

    private void setDefaultVisibilityOptions() {
        isVisibilityFollowingFeedbackQuestion = true;
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
    }
    
    public boolean isVisibleTo(FeedbackParticipantType viewerType) {
        return showCommentTo.contains(viewerType);
    }
    
    public Long getId() {
        return feedbackResponseCommentId;
    }
    
    /** 
     * Use only to match existing and known Comment
     */
    public void setId(Long id) {
        this.feedbackResponseCommentId = id;
    }
    
    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error = validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.EMAIL, giverEmail);
        if (!error.isEmpty()) { errors.add(error); }
        
        //TODO: handle the new attributes showCommentTo and showGiverNameTo
        
        return errors;
    }

    @Override
    public FeedbackResponseComment toEntity() {
        return new FeedbackResponseComment(courseId, feedbackSessionName, feedbackQuestionId, giverEmail,
                feedbackResponseId, sendingState, createdAt, commentText, giverSection, receiverSection,
                showCommentTo, showGiverNameTo, lastEditorEmail, lastEditedAt);
    }

    @Override
    public String getIdentificationString() {
        return toString();
    }

    @Override
    public String getEntityTypeAsString() {
        return "FeedbackResponseComment";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, FeedbackResponseCommentAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        this.courseId = this.courseId.trim();
        this.feedbackSessionName = this.feedbackSessionName.trim();
        this.commentText = Sanitizer.sanitizeTextField(this.commentText);
        this.courseId = Sanitizer.sanitizeForHtml(courseId);
        this.feedbackSessionName = Sanitizer.sanitizeForHtml(feedbackSessionName);
        this.feedbackQuestionId = Sanitizer.sanitizeForHtml(feedbackQuestionId);
        this.giverEmail = Sanitizer.sanitizeForHtml(giverEmail);
        this.feedbackResponseId = Sanitizer.sanitizeForHtml(feedbackResponseId);
        if (commentText != null) {
            //replacing "\n" with "\n<br>" here is to make comment text support displaying breakline
            String sanitizedText = Sanitizer.sanitizeForHtml(commentText.getValue()).replace("\n", "\n<br>");
            this.commentText = new Text(sanitizedText);
        }
    }
    
    @Override
    public String toString() {
        //TODO: print visibilityOptions also
        return "FeedbackResponseCommentAttributes ["
                + "feedbackResponseCommentId = " + feedbackResponseCommentId 
                + ", courseId = " + courseId 
                + ", feedbackSessionName = " + feedbackSessionName
                + ", feedbackQuestionId = " + feedbackQuestionId
                + ", giverEmail = " + giverEmail 
                + ", feedbackResponseId = " + feedbackResponseId
                + ", commentText = " + commentText.getValue() 
                + ", createdAt = " + createdAt
                + ", lastEditorEmail = " + lastEditorEmail
                + ", lastEditedAt = " + lastEditedAt + "]";
    }
    
    public static void sortFeedbackResponseCommentsByCreationTime(List<FeedbackResponseCommentAttributes> frcs) {
        Collections.sort(frcs, new Comparator<FeedbackResponseCommentAttributes>() {
            public int compare(FeedbackResponseCommentAttributes frc1, FeedbackResponseCommentAttributes frc2) {
                return frc1.createdAt.compareTo(frc2.createdAt);
            }
        });
    }
    
    public String getEditedAtText(Boolean isGiverAnonymous) {
        if (this.lastEditedAt != null && !this.lastEditedAt.equals(this.createdAt)) {
            return "(last edited "
                  + (isGiverAnonymous ? "" : "by " + this.lastEditorEmail + " ")
                  + "at " + TimeHelper.formatDateTimeForComments(this.lastEditedAt) + ")";
        } else {
            return "";
        }
    }

}
