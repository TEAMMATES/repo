package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a text question.
 */
@Entity
public class FeedbackTextQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackTextQuestionDetailsConverter.class)
    private FeedbackTextQuestionDetails questionDetails;

    protected FeedbackTextQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackTextQuestion [id=" + super.getId() + ", createdAt=" + super.getCreatedAt()
                + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackTextQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackTextQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackTextQuestion specific attributes.
     */
    @Converter
    public static class FeedbackTextQuestionDetailsConverter
            extends JsonConverter<FeedbackTextQuestionDetails> {
    }
}
