package teammates.storage.sqlentity;

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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "Courses")
public class Course extends BaseEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String timeZone;

    @Column(nullable = false)
    private String institute;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<FeedbackSession> feedbackSessions = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    protected Course() {
        // required by Hibernate
    }

    private Course(CourseBuilder builder) {
        this.setId(builder.id);
        this.setName(builder.name);

        this.setTimeZone(StringUtils.defaultIfEmpty(builder.timeZone, Const.DEFAULT_TIME_ZONE));
        this.setInstitute(builder.institute);

        if (builder.deletedAt != null) {
            this.setDeletedAt(builder.deletedAt);
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(getId()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);

        return errors;
    }

    @Override
    public void sanitizeForSaving() {
        this.id = SanitizationHelper.sanitizeTitle(id);
        this.name = SanitizationHelper.sanitizeName(name);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public List<FeedbackSession> getFeedbackSessions() {
        return feedbackSessions;
    }

    public void setFeedbackSessions(List<FeedbackSession> feedbackSessions) {
        this.feedbackSessions = feedbackSessions;
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
        return "Course [id=" + id + ", name=" + name + ", timeZone=" + timeZone + ", institute=" + institute
                + ", feedbackSessions=" + feedbackSessions + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
        result = prime * result + ((institute == null) ? 0 : institute.hashCode());
        result = prime * result + ((feedbackSessions == null) ? 0 : feedbackSessions.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        result = prime * result + ((deletedAt == null) ? 0 : deletedAt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        }

        Course o = (Course) obj;
        return Objects.equals(this.id, o.id)
                && Objects.equals(this.name, o.name)
                && Objects.equals(this.timeZone, o.timeZone)
                && Objects.equals(this.institute, o.institute)
                && Objects.equals(this.feedbackSessions, o.feedbackSessions)
                && Objects.equals(this.createdAt, o.createdAt)
                && Objects.equals(this.updatedAt, o.updatedAt)
                && Objects.equals(this.deletedAt, o.deletedAt);
    }

    /**
     * Builder for Course.
     */
    public static class CourseBuilder {
        private String id;
        private String name;
        private String institute;

        private String timeZone;
        private Instant deletedAt;

        public CourseBuilder(String id) {
            this.id = id;
        }

        public CourseBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CourseBuilder withInstitute(String institute) {
            this.institute = institute;
            return this;
        }

        public CourseBuilder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public CourseBuilder withDeletedAt(Instant deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
