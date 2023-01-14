package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackSessionLogEntry;

/**
 * The data transfer object for {@link FeedbackSessionLogEntry} entities.
 */
public final class FeedbackSessionLogEntryAttributes extends EntityAttributes<FeedbackSessionLogEntry> {

    private final String feedbackSessionLogEntryId;
    private String studentEmail;
    private final String courseId;
    private String feedbackSessionName;
    private final String feedbackSessionLogType;
    private String remarks;
    private final long timestamp;
    private transient Instant createdAt;

    private FeedbackSessionLogEntryAttributes(FeedbackSessionLogEntry fslEntry) {
        this.feedbackSessionLogEntryId = fslEntry.getFeedbackSessionLogEntryId();
        this.studentEmail = fslEntry.getStudentEmail();
        this.feedbackSessionName = fslEntry.getFeedbackSessionName();
        this.feedbackSessionLogType = fslEntry.getFeedbackSessionLogType();
        this.remarks = fslEntry.getRemarks();
        this.timestamp = fslEntry.getTimestamp();
        this.createdAt = fslEntry.getCreatedAt();
        this.courseId = fslEntry.getCourseId();
    }

    public FeedbackSessionLogEntryAttributes(
            String studentEmail, String courseId, String feedbackSessionName,
            String fslType, long timestamp) {
        this.feedbackSessionLogEntryId = UUID.randomUUID().toString();
        this.studentEmail = studentEmail;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackSessionLogType = fslType;
        this.timestamp = timestamp;
        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.courseId = courseId;
    }

    /**
     * Gets the {@link FeedbackSessionLogEntryAttributes} instance of the given {@link FeedbackSessionLogEntry}.
     */
    public static FeedbackSessionLogEntryAttributes valueOf(FeedbackSessionLogEntry fslEntry) {
        return new FeedbackSessionLogEntryAttributes(fslEntry);
    }

    public String getFeedbackSessionLogEntryId() {
        return feedbackSessionLogEntryId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(studentEmail), errors);

        return errors;
    }

    @Override
    public FeedbackSessionLogEntry toEntity() {
        FeedbackSessionLogEntry entry = new FeedbackSessionLogEntry(
                studentEmail, courseId, feedbackSessionName, feedbackSessionLogType, timestamp);

        entry.setRemarks(this.remarks);

        return entry;
    }

    @Override
    public void sanitizeForSaving() {
        this.studentEmail = SanitizationHelper.sanitizeEmail(this.studentEmail);
        this.feedbackSessionName = SanitizationHelper.sanitizeName(feedbackSessionName);
    }

    @Override
    public int hashCode() {
        return this.feedbackSessionLogEntryId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionLogEntryAttributes otherEntry = (FeedbackSessionLogEntryAttributes) other;
            return Objects.equals(this.feedbackSessionLogEntryId, otherEntry.feedbackSessionLogEntryId);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, FeedbackSessionLogEntryAttributes.class);
    }

}
