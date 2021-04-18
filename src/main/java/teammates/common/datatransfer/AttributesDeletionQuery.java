package teammates.common.datatransfer;

import teammates.common.util.Assumption;

/**
 * The query for attributes deletion.
 */
public class AttributesDeletionQuery {

    private String courseId;
    private String feedbackSessionName;
    private String questionId;
    private String responseId;

    private AttributesDeletionQuery() {
        // use builder to construct query
    }

    public boolean isCourseIdPresent() {
        return courseId != null;
    }

    public boolean isFeedbackSessionNamePresent() {
        return feedbackSessionName != null;
    }

    public boolean isQuestionIdPresent() {
        return questionId != null;
    }

    public boolean isResponseIdPresent() {
        return responseId != null;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getResponseId() {
        return responseId;
    }

    /**
     * Returns a builder for {@link AttributesDeletionQuery}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link AttributesDeletionQuery}.
     */
    public static class Builder {

        private static final String INVALID_COMBINATION = "Invalid combination";

        private AttributesDeletionQuery attributesDeletionQuery;

        private Builder() {
            attributesDeletionQuery = new AttributesDeletionQuery();
        }

        public Builder withCourseId(String courseId) {
            assert courseId != null;
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.courseId = courseId;
            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            assert feedbackSessionName != null;
            assert attributesDeletionQuery.isCourseIdPresent() : "Session name must come together with course ID";
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.feedbackSessionName = feedbackSessionName;
            return this;
        }

        public Builder withQuestionId(String questionId) {
            assert questionId != null;
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isCourseIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isFeedbackSessionNamePresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.questionId = questionId;
            return this;
        }

        public Builder withResponseId(String responseId) {
            assert responseId != null;
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isCourseIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isFeedbackSessionNamePresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());

            attributesDeletionQuery.responseId = responseId;
            return this;
        }

        public AttributesDeletionQuery build() {
            assert attributesDeletionQuery.isCourseIdPresent()
                    || attributesDeletionQuery.isFeedbackSessionNamePresent()
                    || attributesDeletionQuery.isQuestionIdPresent()
                    || attributesDeletionQuery.isResponseIdPresent()
                    : INVALID_COMBINATION;

            return attributesDeletionQuery;
        }
    }
}
