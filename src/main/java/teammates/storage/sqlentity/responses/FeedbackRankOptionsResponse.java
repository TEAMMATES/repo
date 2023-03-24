package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback rank options response.
 */
@Entity
public class FeedbackRankOptionsResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackRankOptionsResponseDetailsConverter.class)
    private FeedbackRankOptionsResponseDetails answer;

    protected FeedbackRankOptionsResponse() {
        // required by Hibernate
    }

    public FeedbackRankOptionsResponse(
        FeedbackQuestion feedbackQuestion, String giver,
        Section giverSection, String receiver, Section receiverSection,
        FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, receiver, receiverSection);
        this.setAnswer((FeedbackRankOptionsResponseDetails) responseDetails);
    }

    public FeedbackRankOptionsResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackRankOptionsResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackRankOptionsResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackRankOptionsResponse specific attributes.
     */
    @Converter
    public static class FeedbackRankOptionsResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
