package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a Section.
 */
@Entity
@Table(name = "Sections")
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "section")
    private List<Team> teams;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected Section() {
        // required by hibernate
    }

    public Section(Course course, String name) {
        this.setCourse(course);
        this.setName(name);
        this.setTeams(new ArrayList<>());
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
            Section otherSection = (Section) other;
            return Objects.equals(this.name, otherSection.name)
                    && Objects.equals(this.course, otherSection.course);
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("section name", name), errors);

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
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
