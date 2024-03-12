package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a feedback session log.
 */
@Entity
@Table(name = "FeedbackSessionLogs")
public class FeedbackSessionLog extends BaseEntity{
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String studentEmail;
    
    @Column(nullable = false)
    private String feedbackSessionName;

    @Column(nullable = false)
    private String feedbackSessionLogType;

    @Column(nullable = false)
    private Instant timestamp;

    protected FeedbackSessionLog() {
        // required by Hibernate
    }

    public FeedbackSessionLog(String email, String feedbackSessionName, String feedbackSessionLogType,
            Instant timestamp) {
        this.setId(UUID.randomUUID());
        this.studentEmail = email;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String email) {
        this.studentEmail = email;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public void setFeedbackSessionLogType(String feedbackSessionLogType) {
        this.feedbackSessionLogType = feedbackSessionLogType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FeedbackSessionLog [id=" + id + ", email=" + studentEmail + ", feedbackSessionName=" + feedbackSessionName
                + ", feedbackSessionLogType=" + feedbackSessionLogType + ", timestamp=" + timestamp + "]";
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionLog otherUsageStatistics = (FeedbackSessionLog) other;
            return Objects.equals(this.getId(), otherUsageStatistics.getId());
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }
}
