package teammates.storage.sqlentity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "FeedbackSessions")
public class FeedbackSession extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String creatorEmail;

    @Column(nullable = false)
    private String instructions;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private Instant sessionVisibleFromTime;

    @Column(nullable = false)
    private Instant resultsVisibleFromTime;

    @Column(nullable = false)
    @Convert(converter = DurationLongConverter.class)
    private Duration gracePeriod;

    @Column(nullable = false)
    private boolean isOpeningEmailEnabled;

    @Column(nullable = false)
    private boolean isClosingEmailEnabled;

    @Column(nullable = false)
    private boolean isPublishedEmailEnabled;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    protected FeedbackSession() {
        // required by Hibernate
    }

    public FeedbackSession(String name, Course course, String creatorEmail, String instructions, Instant startTime,
            Instant endTime, Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, Duration gracePeriod,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled) {
        this.setName(name);
        this.setCourse(course);
        this.setCreatorEmail(creatorEmail);
        this.setInstructions(StringUtils.defaultString(instructions));
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setSessionVisibleFromTime(sessionVisibleFromTime);
        this.setResultsVisibleFromTime(resultsVisibleFromTime);
        this.setGracePeriod(Objects.requireNonNullElse(gracePeriod, Duration.ZERO));
        this.setOpeningEmailEnabled(isOpeningEmailEnabled);
        this.setClosingEmailEnabled(isClosingEmailEnabled);
        this.setPublishedEmailEnabled(isPublishedEmailEnabled);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, name), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.COURSE_ID_FIELD_NAME, course), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("instructions to students", instructions),
                errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(creatorEmail), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGracePeriod(gracePeriod), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission opening time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission closing time", endTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the responses to become visible", resultsVisibleFromTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(startTime, endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                sessionVisibleFromTime, startTime), errors);

        Instant actualSessionVisibleFromTime = sessionVisibleFromTime;

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = startTime;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                actualSessionVisibleFromTime, resultsVisibleFromTime), errors);

        // TODO: add once extended dealines added to entity
        // addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
        // endTime, studentDeadlines), errors);

        // addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
        // endTime, instructorDeadlines), errors);

        return errors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Instant sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Instant getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Instant resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    public Duration getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Duration gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public boolean isOpeningEmailEnabled() {
        return isOpeningEmailEnabled;
    }

    public void setOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setClosingEmailEnabled(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "FeedbackSession [id=" + id + ", course=" + course + ", name=" + name + ", creatorEmail=" + creatorEmail
                + ", instructions=" + instructions + ", startTime=" + startTime + ", endTime=" + endTime
                + ", sessionVisibleFromTime=" + sessionVisibleFromTime + ", resultsVisibleFromTime="
                + resultsVisibleFromTime + ", gracePeriod=" + gracePeriod + ", isOpeningEmailEnabled="
                + isOpeningEmailEnabled + ", isClosingEmailEnabled=" + isClosingEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.course, this.name);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSession otherFs = (FeedbackSession) other;
            return Objects.equals(this.name, otherFs.name)
                    && Objects.equals(this.course, otherFs.course);
        } else {
            return false;
        }
    }
}
