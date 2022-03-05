package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;

import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Notification;

/**
 * The data transfer object for the notification.
 */
public class NotificationAttributes extends EntityAttributes<Notification> {

    private String notificationId;
    private Instant startTime;
    private Instant endTime;
    private String type;
    private String targetUser;
    private String title;
    private String message;
    private boolean shown;
    private Instant createdAt;
    private Instant updatedAt;

    private NotificationAttributes(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the {@link NotificationAttributes} instance of the given {@link Notification}.
     */
    public static NotificationAttributes valueOf(Notification n) {
        NotificationAttributes notificationAttributes = new NotificationAttributes(n.getNotificationId());

        notificationAttributes.startTime = n.getStartTime();
        notificationAttributes.endTime = n.getEndTime();
        notificationAttributes.type = n.getType();
        notificationAttributes.targetUser = n.getTargetUser();
        notificationAttributes.title = n.getTitle();
        notificationAttributes.message = n.getMessage();
        notificationAttributes.createdAt = n.getCreatedAt();
        notificationAttributes.updatedAt = n.getUpdatedAt();
        notificationAttributes.shown = n.isShown();

        return notificationAttributes;
    }

    /**
     * Returns a builder for {@link NotificationAttributes}.
     */
    public static Builder builder(String notificationId) {
        return new Builder(notificationId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public NotificationAttributes getCopy() {
        NotificationAttributes notificationAttributes = new NotificationAttributes(this.notificationId);

        notificationAttributes.startTime = this.startTime;
        notificationAttributes.endTime = this.endTime;
        notificationAttributes.type = this.type;
        notificationAttributes.targetUser = this.targetUser;
        notificationAttributes.title = this.title;
        notificationAttributes.message = this.message;
        notificationAttributes.createdAt = this.createdAt;
        notificationAttributes.updatedAt = this.updatedAt;
        notificationAttributes.shown = this.shown;

        return notificationAttributes;
    }

    public String getNotificationId() {
        return notificationId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShown() {
        return shown;
    }

    /**
     * Sets the notification as shown to the user.
     * Only allowed to change value from false to true.
     */
    public void setShown() {
        this.shown = true;
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
        // TODO: perform various checks on the fields of this object, e.g. length of title, etc.
        List<String> errors = new ArrayList<>();

        // addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        // No validation for createdAt and updatedAt fields.

        // startTime valid
        // endtime valid
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification visible time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification expiry time", endTime), errors);

        // startTime < endTime
        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForNotificationStartAndEnd(startTime, endTime), errors);

        // type valid
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationType(type), errors);

        // target user valid
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUser), errors);

        // title not blank
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTitle(title), errors);

        // message not blank
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationBody(message), errors);

        return errors; 
    }

    @Override
    public Notification toEntity() {
        return new Notification(notificationId, startTime, endTime, type,
                targetUser, title, message, shown, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, NotificationAttributes.class);
    }

    @Override
    public int hashCode() {
        // Notification ID uniquely identifies a notification.
        return this.getNotificationId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            NotificationAttributes otherNotification = (NotificationAttributes) other;
            return Objects.equals(this.notificationId, otherNotification.notificationId);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.title = SanitizationHelper.sanitizeTitle(title);
        this.message = SanitizationHelper.sanitizeForRichText(message);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.startTimeOption.ifPresent(s -> startTime = s);
        updateOptions.endTimeOption.ifPresent(e -> endTime = e);
        updateOptions.typeOption.ifPresent(t -> type = t);
        updateOptions.targetUserOption.ifPresent(u -> targetUser = u);
        updateOptions.titleOption.ifPresent(t -> title = t);
        updateOptions.messageOption.ifPresent(m -> message = m);
        updateOptions.shownOption.ifPresent(s -> shown = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a notification.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String notificationId) {
        return new UpdateOptions.Builder(notificationId);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build on top of {@code updateOptions}.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link NotificationAttributes}.
     */
    public static class Builder extends BasicBuilder<NotificationAttributes, Builder> {
        private final NotificationAttributes notificationAttributes;

        private Builder(String notificationId) {
            super(new UpdateOptions(notificationId));
            thisBuilder = this;

            notificationAttributes = new NotificationAttributes(notificationId);
        }

        @Override
        public NotificationAttributes build() {
            notificationAttributes.update(updateOptions);

            return notificationAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link NotificationAttributes}.
     */
    public static class UpdateOptions {
        private String notificationId;

        private UpdateOption<Instant> startTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<String> typeOption = UpdateOption.empty();
        private UpdateOption<String> targetUserOption = UpdateOption.empty();
        private UpdateOption<String> titleOption = UpdateOption.empty();
        private UpdateOption<String> messageOption = UpdateOption.empty();
        private UpdateOption<Boolean> shownOption = UpdateOption.empty();

        private UpdateOptions(String notificationId) {
            assert notificationId != null;

            this.notificationId = notificationId;
        }

        public String getNotificationId() {
            return notificationId;
        }

        @Override
        public String toString() {
            return "NotificationAttributes.UpdateOptions ["
                    + "startTime = " + startTimeOption
                    + ", endTime = " + endTimeOption
                    + ", type = " + typeOption
                    + ", targetUser = " + targetUserOption
                    + ", title = " + titleOption
                    + ", message = " + messageOption
                    + ", shown = " + shownOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                assert updateOptions != null;
                thisBuilder = this;
            }

            private Builder(String notificationId) {
                super(new UpdateOptions(notificationId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link NotificationAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withStartTime(Instant startTime) {
            assert startTime != null;

            updateOptions.startTimeOption = UpdateOption.of(startTime);
            return thisBuilder;
        }

        public B withEndTime(Instant endTime) {
            assert endTime != null;

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withType(String type) {
            updateOptions.typeOption = UpdateOption.of(type);
            return thisBuilder;
        }

        public B withTargetUser(String targetUser) {
            assert targetUser != null;

            updateOptions.targetUserOption = UpdateOption.of(targetUser);
            return thisBuilder;
        }

        public B withTitle(String title) {
            assert title != null;

            updateOptions.titleOption = UpdateOption.of(title);
            return thisBuilder;
        }

        public B withMessage(String message) {
            assert message != null;

            updateOptions.messageOption = UpdateOption.of(message);
            return thisBuilder;
        }

        public B withShown() {
            updateOptions.shownOption = UpdateOption.of(true);
            return thisBuilder;
        }

        public abstract T build();

    }
}
