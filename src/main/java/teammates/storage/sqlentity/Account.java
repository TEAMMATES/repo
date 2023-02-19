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
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a unique account in the system.
 */
@Entity
@Table(name = "Accounts")
public class Account extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String googleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "account")
    private List<ReadNotification> readNotifications;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected Account() {
        // required by Hibernate
    }

    public Account(String googleId, String name, String email) {
        this.setGoogleId(googleId);
        this.setName(name);
        this.setEmail(email);
        this.readNotifications = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = SanitizationHelper.sanitizeEmail(email);
    }

    public List<ReadNotification> getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(List<ReadNotification> readNotifications) {
        this.readNotifications = readNotifications;
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

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Account otherAccount = (Account) other;
            return Objects.equals(this.googleId, otherAccount.googleId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getGoogleId().hashCode();
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", googleId=" + googleId + ", name=" + name + ", email=" + email
                + ", readNotifications=" + readNotifications + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + "]";
    }
}
