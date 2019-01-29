package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * The feedback question response.
 */
public class FeedbackQuestion extends ApiOutput {
    private final String feedbackQuestionId;
    private int questionNumber;
    private final String questionBrief;
    private final String questionDescription;

    private final FeedbackQuestionDetails questionDetails;

    private final FeedbackQuestionType questionType;
    private final FeedbackParticipantType giverType;
    private final FeedbackParticipantType recipientType;

    private final NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
    private final Integer customNumberOfEntitiesToGiveFeedbackTo;

    private List<FeedbackVisibilityType> showResponsesTo;
    private List<FeedbackVisibilityType> showGiverNameTo;
    private List<FeedbackVisibilityType> showRecipientNameTo;

    public FeedbackQuestion(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        FeedbackQuestionDetails feedbackQuestionDetails = feedbackQuestionAttributes.getQuestionDetails();

        this.feedbackQuestionId = feedbackQuestionAttributes.getFeedbackQuestionId();
        this.questionNumber = feedbackQuestionAttributes.getQuestionNumber();
        this.questionBrief = feedbackQuestionDetails.getQuestionText();
        this.questionDescription = feedbackQuestionAttributes.getQuestionDescription();

        this.questionDetails = feedbackQuestionDetails;

        this.questionType = feedbackQuestionAttributes.getQuestionType();
        this.giverType = feedbackQuestionAttributes.getGiverType();
        this.recipientType = feedbackQuestionAttributes.getRecipientType();

        if (feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED;
            this.customNumberOfEntitiesToGiveFeedbackTo = null;
        } else {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
            this.customNumberOfEntitiesToGiveFeedbackTo =
                    feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo();
        }

        // the visibility types are mixed in feedback participant type
        // therefore, we convert them to visibility types
        this.showResponsesTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowResponsesTo());
        this.showGiverNameTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowGiverNameTo());
        this.showRecipientNameTo =
                convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowRecipientNameTo());

        // specially handling for contribution questions
        // TODO: remove the hack
        if (this.questionType == FeedbackQuestionType.CONTRIB
                && this.giverType == FeedbackParticipantType.STUDENTS
                && this.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                && this.showResponsesTo.contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
            // remove the redundant visibility type as GIVER_TEAM_MEMBERS is just RECIPIENT_TEAM_MEMBERS
            // contribution question keep the redundancy for legacy reason
            this.showResponsesTo.remove(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        }
    }

    /**
     * Converts a list of feedback participant type to a list of visibility type.
     */
    private List<FeedbackVisibilityType> convertToFeedbackVisibilityType(
            List<FeedbackParticipantType> feedbackParticipantTypeList) {
        return feedbackParticipantTypeList.stream().map(feedbackParticipantType -> {
            switch (feedbackParticipantType) {
            case STUDENTS:
                return FeedbackVisibilityType.STUDENTS;
            case INSTRUCTORS:
                return FeedbackVisibilityType.INSTRUCTORS;
            case RECEIVER:
                return FeedbackVisibilityType.RECIPIENT;
            case OWN_TEAM_MEMBERS:
                return FeedbackVisibilityType.GIVER_TEAM_MEMBERS;
            case RECEIVER_TEAM_MEMBERS:
                return FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS;
            default:
                Assumption.fail("Unknown feedbackParticipantType" + feedbackParticipantType);
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionBrief() {
        return questionBrief;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public FeedbackQuestionDetails getQuestionDetails() {
        return questionDetails;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public NumberOfEntitiesToGiveFeedbackToSetting getNumberOfEntitiesToGiveFeedbackToSetting() {
        return numberOfEntitiesToGiveFeedbackToSetting;
    }

    public Integer getCustomNumberOfEntitiesToGiveFeedbackTo() {
        return customNumberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackVisibilityType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public List<FeedbackVisibilityType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<FeedbackVisibilityType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }
}
