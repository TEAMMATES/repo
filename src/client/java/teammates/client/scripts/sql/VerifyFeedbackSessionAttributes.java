package teammates.client.scripts.sql;

import java.time.Duration;

import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackSession;

/**
 * Verification of the feedback session attributes.
 */
public class VerifyFeedbackSessionAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<FeedbackSession, teammates.storage.sqlentity.FeedbackSession> {

    public VerifyFeedbackSessionAttributes() {
        super(FeedbackSession.class, teammates.storage.sqlentity.FeedbackSession.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.FeedbackSession sqlEntity) {
        return FeedbackSession.generateId(sqlEntity.getName(), sqlEntity.getCourse().getId());
    }

    @Override
    protected boolean equals(teammates.storage.sqlentity.FeedbackSession sqlEntity, FeedbackSession datastoreEntity) {
        try {
            return sqlEntity.getCourse().getId().equals(datastoreEntity.getCourseId())
                    && sqlEntity.getName().equals(datastoreEntity.getFeedbackSessionName())
                    && sqlEntity.getCreatorEmail().equals(datastoreEntity.getCreatorEmail())
                    && sqlEntity.getInstructions()
                            .equals(SanitizationHelper.sanitizeForRichText(datastoreEntity.getInstructions()))
                    && sqlEntity.getStartTime().equals(datastoreEntity.getStartTime())
                    && sqlEntity.getEndTime().equals(datastoreEntity.getEndTime())
                    && sqlEntity.getSessionVisibleFromTime().equals(datastoreEntity.getSessionVisibleFromTime())
                    && sqlEntity.getResultsVisibleFromTime().equals(datastoreEntity.getResultsVisibleFromTime())
                    && sqlEntity.getGracePeriod().equals(Duration.ofMinutes(datastoreEntity.getGracePeriod()))
                    && sqlEntity.isOpeningEmailEnabled() == datastoreEntity.isOpeningEmailEnabled()
                    && sqlEntity.isClosingEmailEnabled() == datastoreEntity.isClosingEmailEnabled()
                    && sqlEntity.isOpenEmailSent() == datastoreEntity.isSentOpenEmail()
                    && sqlEntity.isOpeningSoonEmailSent() == datastoreEntity.isSentOpeningSoonEmail()
                    && sqlEntity.isClosedEmailSent() == datastoreEntity.isSentClosedEmail()
                    && sqlEntity.isClosingSoonEmailSent() == datastoreEntity.isSentClosingEmail()
                    && sqlEntity.isPublishedEmailSent() == datastoreEntity.isSentPublishedEmail()
                    && (sqlEntity.getDeletedAt() == datastoreEntity.getDeletedTime()
                        || sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedTime()));
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public static void main(String[] args) {
        VerifyFeedbackSessionAttributes script = new VerifyFeedbackSessionAttributes();
        script.doOperationRemotely();
    }

}
