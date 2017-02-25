package teammates.storage.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.util.SanitizationHelper;

import com.google.appengine.api.datastore.Text;

/**
 * An association class that represents the association Giver
 * --> [comments about] --> Receiver.
 * Both giver and receiver must be in the same course.
 * Currently giver is restricted only to Instructor, and
 * receiver is restricted to Student.
 */
@PersistenceCapable
public class Comment {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private transient Long commentId;

    /** The foreign key to locate the Course object. */
    @Persistent
    private String courseId;

    /** The giver's email used for this comment. */
    @Persistent
    private String giverEmail;

    /** The recipient type for this comment. */
    @Persistent
    private CommentParticipantType recipientType;

    /** The recipients' id used for this comment. E.g.
     * if the recipient type is PERSON, then it stands for
     * recipients' email; if it's TEAM, it stands for the
     * team id; if it's COURSE, it will store the course
     * id. */
    @Persistent
    private Set<String> recipients;

    /** The comment's status */
    @Persistent
    private CommentStatus status;

    /** Is this comment pending to be sent to recipient (through email) or sending or sent */
    @Persistent
    private CommentSendingState sendingState;

    /** Visibility options **/
    @Persistent
    private List<CommentParticipantType> showCommentTo;

    @Persistent
    private List<CommentParticipantType> showGiverNameTo;

    @Persistent
    private List<CommentParticipantType> showRecipientNameTo;

    //TODO: remove this property after data migration
    /** The receiver's email used for this comment. */
    @Persistent
    private String receiverEmail;

    /** The creation time of this comment. */
    @Persistent
    private Date createdAt;

    /** The comment from giver for receiver */
    @Persistent
    private Text commentText;

    /** The e-mail of the account that last edited the comment */
    @Persistent
    private String lastEditorEmail;

    /** The time in which the comment is last edited */
    @Persistent
    private Date lastEditedAt;

    public Comment(String courseId, String giverEmail, CommentParticipantType recipientType,
                   Set<String> recipients, CommentStatus status, CommentSendingState sendingState,
                   List<CommentParticipantType> showCommentTo, List<CommentParticipantType> showGiverNameTo,
                   List<CommentParticipantType> showRecipientNameTo, Text comment, Date date,
                   String lastEditorEmail, Date lastEditedAt) {
        this.commentId = null; //Auto generated by GAE
        this.courseId = courseId;
        this.giverEmail = giverEmail;
        this.recipientType = recipientType;
        this.recipients = recipients;
        this.status = status;
        this.sendingState = sendingState;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
        this.createdAt = date;
        this.commentText = SanitizationHelper.sanitizeForRichText(comment);
        this.lastEditorEmail = lastEditorEmail == null ? giverEmail : lastEditorEmail;
        this.lastEditedAt = lastEditedAt == null ? date : lastEditedAt;
    }

    public Long getId() {
        return commentId;
    }

    /* Auto generated. Don't set this.
    public void setId(Long id) {
        this.id = id;
    }*/

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getGiverEmail() {
        return giverEmail;
    }

    public void setGiverEmail(String giverEmail) {
        this.giverEmail = giverEmail;
    }

    public CommentParticipantType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(CommentParticipantType recipientType) {
        this.recipientType = recipientType;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public CommentSendingState getSendingState() {
        return sendingState;
    }

    public void setSendingState(CommentSendingState sendingState) {
        this.sendingState = sendingState;
    }

    public List<CommentParticipantType> getShowCommentTo() {
        return showCommentTo;
    }

    public void setShowCommentTo(List<CommentParticipantType> showCommentTo) {
        this.showCommentTo = showCommentTo;
    }

    public List<CommentParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<CommentParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<CommentParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public void setShowRecipientNameTo(List<CommentParticipantType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }

    @Deprecated
    public String getReceiverEmail() {
        return receiverEmail;
    }

    @Deprecated
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Text getCommentText() {
        return commentText;
    }

    public void setCommentText(Text commentText) {
        this.commentText = commentText;
    }

    public void setLastEditorEmail(String lastEditorEmail) {
        this.lastEditorEmail = lastEditorEmail;
    }

    public String getLastEditorEmail() {
        return this.lastEditorEmail;
    }

    public Date getLastEditedAt() {
        return this.lastEditedAt;
    }

    public void setLastEditedAt(Date lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

}
