package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackPath;
import teammates.storage.entity.FeedbackQuestion;

public class FeedbackQuestionAttributes extends EntityAttributes implements Comparable<FeedbackQuestionAttributes> {
    public String feedbackSessionName;
    public String courseId;
    public String creatorEmail;
    /**
     * Contains the JSON formatted string that holds the information of the question details.
     *
     * <p>Don't use directly unless for storing/loading from data store.<br>
     * To get the question text use {@code getQuestionDetails().questionText}
     */
    public Text questionMetaData;
    public Text questionDescription;
    public int questionNumber;
    public FeedbackQuestionType questionType;
    public FeedbackParticipantType giverType;
    public FeedbackParticipantType recipientType;
    public int numberOfEntitiesToGiveFeedbackTo;
    public List<FeedbackParticipantType> showResponsesTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public List<FeedbackParticipantType> showRecipientNameTo;
    public List<FeedbackPathAttributes> feedbackPaths;
    protected transient Date createdAt;
    protected transient Date updatedAt;
    private String feedbackQuestionId;

    public FeedbackQuestionAttributes() {
        // attributes to be set after construction
    }

    public FeedbackQuestionAttributes(FeedbackQuestion fq) {
        this.feedbackQuestionId = fq.getId();
        this.feedbackSessionName = fq.getFeedbackSessionName();
        this.courseId = fq.getCourseId();
        this.creatorEmail = fq.getCreatorEmail();
        this.questionMetaData = fq.getQuestionMetaData();
        this.questionDescription = SanitizationHelper.sanitizeForRichText(fq.getQuestionDescription());
        this.questionNumber = fq.getQuestionNumber();
        this.questionType = fq.getQuestionType();
        this.giverType = fq.getGiverType();
        this.recipientType = fq.getRecipientType();
        this.numberOfEntitiesToGiveFeedbackTo = fq.getNumberOfEntitiesToGiveFeedbackTo();
        this.showResponsesTo = new ArrayList<FeedbackParticipantType>(fq.getShowResponsesTo());
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>(fq.getShowGiverNameTo());
        this.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(fq.getShowRecipientNameTo());

        this.createdAt = fq.getCreatedAt();
        this.updatedAt = fq.getUpdatedAt();

        this.feedbackPaths = getFeedbackPaths(fq.getFeedbackPaths());

        removeIrrelevantVisibilityOptions();
    }

    private FeedbackQuestionAttributes(FeedbackQuestionAttributes other) {
        this.feedbackQuestionId = other.getId();
        this.feedbackSessionName = other.getFeedbackSessionName();
        this.courseId = other.getCourseId();
        this.creatorEmail = other.getCreatorEmail();
        this.questionMetaData = other.getQuestionMetaData();
        this.questionNumber = other.getQuestionNumber();
        this.questionType = other.getQuestionType();
        this.giverType = other.getGiverType();
        this.recipientType = other.getRecipientType();
        this.numberOfEntitiesToGiveFeedbackTo = other.getNumberOfEntitiesToGiveFeedbackTo();
        this.showResponsesTo = new ArrayList<FeedbackParticipantType>(other.getShowResponsesTo());
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>(other.getShowGiverNameTo());
        this.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(other.getShowRecipientNameTo());

        this.createdAt = other.getCreatedAt();
        this.updatedAt = other.getUpdatedAt();

        this.feedbackPaths = other.feedbackPaths;

        removeIrrelevantVisibilityOptions();
    }

    public FeedbackQuestionAttributes getCopy() {
        return new FeedbackQuestionAttributes(this);
    }

    public Date getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    public String getId() {
        return feedbackQuestionId;
    }

    /** NOTE: Only use this to match and search for the ID of a known existing question entity. */
    public void setId(String id) {
        this.feedbackQuestionId = id;
    }

    @Override
    public FeedbackQuestion toEntity() {
        return new FeedbackQuestion(feedbackSessionName, courseId, creatorEmail,
                                    questionMetaData, questionDescription, questionNumber, questionType, giverType,
                                    recipientType, numberOfEntitiesToGiveFeedbackTo,
                                    showResponsesTo, showGiverNameTo, showRecipientNameTo, getFeedbackPathEntities());
    }

    @Override
    public String toString() {
        return "FeedbackQuestionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", creatorEmail=" + creatorEmail + ", questionText="
               + questionMetaData + ", questionDescription=" + questionDescription
               + ", questionNumber=" + questionNumber
               + ", questionType=" + questionType + ", giverType=" + giverType
               + ", recipientType=" + recipientType
               + ", numberOfEntitiesToGiveFeedbackTo="
               + numberOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
               + showResponsesTo + ", showGiverNameTo=" + showGiverNameTo
               + ", showRecipientNameTo=" + showRecipientNameTo
               + ", feedbackPaths=" + feedbackPaths + "]";
    }

    @Override
    public String getIdentificationString() {
        return this.questionNumber + ". " + this.questionMetaData.toString() + "/"
               + this.feedbackSessionName + "/" + this.courseId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Feedback Question";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackQuestionAttributes.class);
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();

        addNonEmptyError(validator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        // special case when additional text should be added to error text
        String error = validator.getInvalidityInfoForEmail(creatorEmail);
        if (!error.isEmpty()) {
            error = new StringBuffer()
                    .append("Invalid creator's email: ")
                    .append(error)
                    .toString();
        }
        addNonEmptyError(error, errors);

        errors.addAll(validator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        if (giverType == FeedbackParticipantType.CUSTOM && recipientType == FeedbackParticipantType.CUSTOM) {
            errors.addAll(validator.getInvalidityInfoForFeedbackPaths(feedbackPaths));
        }

        errors.addAll(validator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                                                                             showGiverNameTo,
                                                                             showRecipientNameTo));

        return errors;
    }

    // TODO: move following methods to PageData?
    // Answer: OK to move to the respective PageData class. Unit test this thoroughly.
    public List<String> getVisibilityMessage() {
        List<String> message = new ArrayList<String>();

        for (FeedbackParticipantType participant : showResponsesTo) {
            StringBuilder line = new StringBuilder(100);

            // Exceptional case: self feedback
            if (participant == FeedbackParticipantType.RECEIVER
                    && recipientType == FeedbackParticipantType.SELF) {
                message.add("You can see your own feedback in the results page later on.");
                continue;
            }

            // Front fragment: e.g. Other students in the course..., The receiving.., etc.
            line.append(participant.toVisibilityString()).append(' ');

            // Recipient fragment: e.g. student, instructor, etc.
            if (participant == FeedbackParticipantType.RECEIVER) {
                line.append(recipientType.toSingularFormString());

                if (numberOfEntitiesToGiveFeedbackTo > 1) {
                    line.append('s');
                }

                line.append(' ');
            }

            line.append("can see your response");

            // Visibility fragment: e.g. can see your name, but not...
            if (showRecipientNameTo.contains(participant)) {
                if (participant != FeedbackParticipantType.RECEIVER
                        && recipientType != FeedbackParticipantType.NONE) {
                    line.append(", the name of the recipient");
                }

                if (showGiverNameTo.contains(participant)) {
                    line.append(", and your name");
                } else {
                    line.append(", but not your name");
                }
            } else {
                if (showGiverNameTo.contains(participant)) {
                    line.append(", and your name");
                }

                if (recipientType == FeedbackParticipantType.NONE) {
                    if (!showGiverNameTo.contains(participant)) {
                        line.append(", but not your name");
                    }
                } else {
                    line.append(", but not the name of the recipient");

                    if (!showGiverNameTo.contains(participant)) {
                        line.append(", or your name");
                    }
                }

            }

            line.append('.');
            message.add(line.toString());
        }

        if (message.isEmpty()) {
            message.add("No-one can see your responses.");
        }

        return message;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    public boolean isGiverAStudent() {
        return giverType == FeedbackParticipantType.SELF
               || giverType == FeedbackParticipantType.STUDENTS;
    }

    public boolean isRecipientNameHidden() {
        return recipientType == FeedbackParticipantType.NONE
               || recipientType == FeedbackParticipantType.SELF
               || recipientType == FeedbackParticipantType.CUSTOM
               && hasClassAsRecipientInFeedbackPaths();
    }

    public boolean isRecipientAStudent() {
        return recipientType == FeedbackParticipantType.SELF
               || recipientType == FeedbackParticipantType.STUDENTS
               || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
               || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }

    public boolean isGiverATeam() {
        return giverType.isTeam()
                || giverType == FeedbackParticipantType.CUSTOM
                && isFeedbackPathsGiverTypeTeams();
    }

    public boolean isRecipientATeam() {
        return recipientType.isTeam()
                || recipientType == FeedbackParticipantType.CUSTOM
                && isFeedbackPathsRecipientTypeTeams();
    }

    public boolean isResponseVisibleTo(FeedbackParticipantType userType) {
        return showResponsesTo.contains(userType);
    }

    /**
     * Checks if updating this question to the {@code newAttributes} will
     * require the responses to be deleted for consistency.
     * Does not check if any responses exist.
     */
    public boolean areResponseDeletionsRequiredForChanges(FeedbackQuestionAttributes newAttributes) {
        if (!newAttributes.giverType.equals(this.giverType)
                || !newAttributes.recipientType.equals(this.recipientType)) {
            return true;
        }

        if (!this.showResponsesTo.containsAll(newAttributes.showResponsesTo)
                || !this.showGiverNameTo.containsAll(newAttributes.showGiverNameTo)
                || !this.showRecipientNameTo.containsAll(newAttributes.showRecipientNameTo)) {
            return true;
        }

        return this.getQuestionDetails().isChangesRequiresResponseDeletion(newAttributes.getQuestionDetails());
    }

    @Override
    public int compareTo(FeedbackQuestionAttributes o) {
        if (o == null) {
            return 1;
        }

        if (this.questionNumber != o.questionNumber) {
            return Integer.compare(this.questionNumber, o.questionNumber);
        }
        /**
         * Although question numbers ought to be unique in a feedback session,
         * eventual consistency can result in duplicate questions numbers.
         * Therefore, to ensure that the question order is always consistent to the user,
         * compare feedbackQuestionId, which is guaranteed to be unique,
         * when the questionNumbers are the same.
         */
        return this.feedbackQuestionId.compareTo(o.feedbackQuestionId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (courseId == null ? 0 : courseId.hashCode());

        result = prime * result + (creatorEmail == null ? 0 : creatorEmail.hashCode());

        result = prime * result + (feedbackSessionName == null ? 0 : feedbackSessionName.hashCode());

        result = prime * result + (giverType == null ? 0 : giverType.hashCode());

        result = prime * result + numberOfEntitiesToGiveFeedbackTo;

        result = prime * result + questionNumber;

        result = prime * result + (questionMetaData == null ? 0 : questionMetaData.hashCode());

        result = prime * result + (questionDescription == null ? 0 : questionDescription.hashCode());

        result = prime * result + (questionType == null ? 0 : questionType.hashCode());

        result = prime * result + (recipientType == null ? 0 : recipientType.hashCode());

        result = prime * result + (showGiverNameTo == null ? 0 : showGiverNameTo.hashCode());

        result = prime * result + (showRecipientNameTo == null ? 0 : showRecipientNameTo.hashCode());

        result = prime * result + (showResponsesTo == null ? 0 : showResponsesTo.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        FeedbackQuestionAttributes other = (FeedbackQuestionAttributes) obj;

        if (courseId == null) {
            if (other.courseId != null) {
                return false;
            }
        } else if (!courseId.equals(other.courseId)) {
            return false;
        }

        if (creatorEmail == null) {
            if (other.creatorEmail != null) {
                return false;
            }
        } else if (!creatorEmail.equals(other.creatorEmail)) {
            return false;
        }

        if (feedbackSessionName == null) {
            if (other.feedbackSessionName != null) {
                return false;
            }
        } else if (!feedbackSessionName.equals(other.feedbackSessionName)) {
            return false;
        }

        if (giverType != other.giverType) {
            return false;
        }

        if (numberOfEntitiesToGiveFeedbackTo != other.numberOfEntitiesToGiveFeedbackTo) {
            return false;
        }

        if (questionNumber != other.questionNumber) {
            return false;
        }

        if (questionMetaData == null) {
            if (other.questionMetaData != null) {
                return false;
            }
        } else if (!questionMetaData.equals(other.questionMetaData)) {
            return false;
        }

        if (questionDescription == null) {
            if (other.questionDescription != null) {
                return false;
            }
        } else if (!questionDescription.equals(other.questionDescription)) {
            return false;
        }

        if (questionType != other.questionType) {
            return false;
        }

        if (recipientType != other.recipientType) {
            return false;
        }

        if (showGiverNameTo == null) {
            if (other.showGiverNameTo != null) {
                return false;
            }
        } else if (!showGiverNameTo.equals(other.showGiverNameTo)) {
            return false;
        }

        if (showRecipientNameTo == null) {
            if (other.showRecipientNameTo != null) {
                return false;
            }
        } else if (!showRecipientNameTo.equals(other.showRecipientNameTo)) {
            return false;
        }

        if (showResponsesTo == null) {
            if (other.showResponsesTo != null) {
                return false;
            }
        } else if (!showResponsesTo.equals(other.showResponsesTo)) {
            return false;
        }

        return true;
    }

    public void updateValues(FeedbackQuestionAttributes newAttributes) {
        // These can't be changed anyway. Copy values to defensively avoid invalid parameters.
        newAttributes.feedbackSessionName = this.feedbackSessionName;
        newAttributes.courseId = this.courseId;
        newAttributes.creatorEmail = this.creatorEmail;

        if (newAttributes.questionMetaData == null) {
            newAttributes.questionMetaData = this.questionMetaData;
        }

        if (newAttributes.questionDescription == null) {
            newAttributes.questionDescription = this.questionDescription;
        }

        if (newAttributes.questionType == null) {
            newAttributes.questionType = this.questionType;
        }

        if (newAttributes.giverType == null) {
            newAttributes.giverType = this.giverType;
        }

        if (newAttributes.recipientType == null) {
            newAttributes.recipientType = this.recipientType;
        }

        if (newAttributes.showResponsesTo == null) {
            newAttributes.showResponsesTo = this.showResponsesTo;
        }

        if (newAttributes.showGiverNameTo == null) {
            newAttributes.showGiverNameTo = this.showGiverNameTo;
        }

        if (newAttributes.showRecipientNameTo == null) {
            newAttributes.showRecipientNameTo = this.showRecipientNameTo;
        }
    }

    public void removeIrrelevantVisibilityOptions() {
        List<FeedbackParticipantType> optionsToRemove = new ArrayList<FeedbackParticipantType>();

        switch (recipientType) {
        case NONE:
            optionsToRemove.add(FeedbackParticipantType.RECEIVER);
            optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
            break;
        case TEAMS:
        case INSTRUCTORS:
        case OWN_TEAM:
        case OWN_TEAM_MEMBERS:
            optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
            break;
        default:
            break;
        }

        switch (giverType) {
        case TEAMS:
        case INSTRUCTORS:
            optionsToRemove.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
            break;
        default:
            break;
        }

        removeVisibilities(optionsToRemove);
    }

    private void removeVisibilities(List<FeedbackParticipantType> optionsToRemove) {
        showResponsesTo.removeAll(optionsToRemove);
        showGiverNameTo.removeAll(optionsToRemove);
        showRecipientNameTo.removeAll(optionsToRemove);
    }

    @Override
    public void sanitizeForSaving() {
        this.questionDescription = SanitizationHelper.sanitizeForRichText(this.questionDescription);
    }

    private boolean isValidJsonString(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Converts the given Feedback*QuestionDetails object to JSON for storing.
     */
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        questionMetaData = new Text(JsonUtils.toJson(questionDetails, getFeedbackQuestionDetailsClass()));
    }

    /**
     * Retrieves the Feedback*QuestionDetails object for this question.
     *
     * @return The Feedback*QuestionDetails object representing the question's details
     */
    public FeedbackQuestionDetails getQuestionDetails() {
        final String questionMetaDataValue = questionMetaData.getValue();
        // For old Text questions, the questionText simply contains the question, not a JSON
        if (questionType == FeedbackQuestionType.TEXT && !isValidJsonString(questionMetaDataValue)) {
            return new FeedbackTextQuestionDetails(questionMetaDataValue);
        }
        return JsonUtils.fromJson(questionMetaDataValue, getFeedbackQuestionDetailsClass());
    }

    /**
     * This method gets the appropriate class type for the Feedback*QuestionDetails object for this question.
     *
     * @return The Feedback*QuestionDetails class type appropriate for this question.
     */
    private Class<? extends FeedbackQuestionDetails> getFeedbackQuestionDetailsClass() {
        return questionType.getQuestionDetailsClass();
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public Text getQuestionMetaData() {
        return questionMetaData;
    }

    public Text getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(Text questionDescription) {
        this.questionDescription = questionDescription;
    }

    public int getQuestionNumber() {
        return questionNumber;
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

    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackParticipantType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public String getQuestionAdditionalInfoHtml() {
        return getQuestionDetails().getQuestionAdditionalInfoHtml(questionNumber, "");
    }

    private List<FeedbackPathAttributes> getFeedbackPaths(List<FeedbackPath> feedbackPathEntities) {
        List<FeedbackPathAttributes> feedbackPaths =
                new ArrayList<FeedbackPathAttributes>();
        for (FeedbackPath feedbackPath : feedbackPathEntities) {
            feedbackPaths.add(new FeedbackPathAttributes(feedbackPath));
        }
        return feedbackPaths;
    }

    /**
     * Returns a list of feedback path entities converted from the question's feedback paths.
     */
    public List<FeedbackPath> getFeedbackPathEntities() {
        List<FeedbackPath> feedbackPathEntities = new ArrayList<FeedbackPath>();
        if (feedbackPaths != null) {
            for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
                feedbackPathEntities.add(feedbackPath.toEntity());
            }
        }

        return feedbackPathEntities;
    }

    /**
     * Returns true if the given student is a giver in the question's feedback paths.
     */
    public boolean hasStudentAsGiverInFeedbackPaths(String studentEmail) {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isStudentFeedbackPathGiver(studentEmail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given instructor is a giver in the question's feedback paths.
     */
    public boolean hasInstructorAsGiverInFeedbackPaths(String instructorEmail) {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isInstructorFeedbackPathGiver(instructorEmail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given team is a giver in the question's feedback paths.
     */
    public boolean hasTeamAsGiverInFeedbackPaths(String teamName) {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isTeamFeedbackPathGiver(teamName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if whether the class is a recipient in the question's feedback paths.
     */
    public boolean hasClassAsRecipientInFeedbackPaths() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathRecipientTheClass()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the question's feedback paths giver type is Students.
     */
    public boolean isFeedbackPathsGiverTypeStudents() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathGiverAStudent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the question's feedback paths giver type is Instructors.
     */
    public boolean isFeedbackPathsGiverTypeInstructors() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathGiverAnInstructor()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the question's feedback paths giver type is Teams.
     */
    public boolean isFeedbackPathsGiverTypeTeams() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathGiverATeam()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the question's feedback paths recipient type is Students.
     */
    public boolean isFeedbackPathsRecipientTypeStudents() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathRecipientAStudent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the question's feedback paths recipient type is Teams.
     */
    public boolean isFeedbackPathsRecipientTypeTeams() {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isFeedbackPathRecipientATeam()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of the question's response givers for which the student is a response recipient.
     */
    public List<String> getGiversFromFeedbackPathsForStudentRecipient(String studentEmail) {
        List<String> givers = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isStudentFeedbackPathRecipient(studentEmail)) {
                givers.add(feedbackPath.getGiverId());
            }
        }
        return givers;
    }

    /**
     * Returns a list of the question's response givers for which the instructor is a response recipient.
     */
    public List<String> getGiversFromFeedbackPathsForInstructorRecipient(String instructorEmail) {
        List<String> givers = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isInstructorFeedbackPathRecipient(instructorEmail)) {
                givers.add(feedbackPath.getGiverId());
            }
        }
        return givers;
    }

    /**
     * Returns a list of the question's response givers for which the team is a response recipient.
     */
    public List<String> getGiversFromFeedbackPathsForTeamRecipient(String teamName) {
        List<String> givers = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isTeamFeedbackPathRecipient(teamName)) {
                givers.add(feedbackPath.getGiverId());
            }
        }
        return givers;
    }

    /**
     * Returns a list of all the question's response givers.
     */
    public List<String> getAllGiversFromFeedbackPaths() {
        List<String> givers = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            givers.add(feedbackPath.getGiverId());
        }
        return givers;
    }

    /**
     * Returns a list of the question's response recipients for which the student is a response giver.
     */
    public List<String> getRecipientsFromFeedbackPathsForStudentGiver(String studentEmail) {
        List<String> recipients = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isStudentFeedbackPathGiver(studentEmail)) {
                recipients.add(feedbackPath.getRecipientId());
            }
        }
        return recipients;
    }

    /**
     * Returns a list of the question's response recipients for which the instructor is a response giver.
     */
    public List<String> getRecipientsFromFeedbackPathsForInstructorGiver(String instructorEmail) {
        List<String> recipients = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isInstructorFeedbackPathGiver(instructorEmail)) {
                recipients.add(feedbackPath.getRecipientId());
            }
        }
        return recipients;
    }

    /**
     * Returns a list of the question's response recipients for which the team is a response giver.
     */
    public List<String> getRecipientsFromFeedbackPathsForTeamGiver(String teamName) {
        List<String> recipients = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            if (feedbackPath.isTeamFeedbackPathGiver(teamName)) {
                recipients.add(feedbackPath.getRecipientId());
            }
        }
        return recipients;
    }

    /**
     * Returns a list of all the question's response recipients.
     */
    public List<String> getAllRecipientsFromFeedbackPaths() {
        List<String> recipients = new ArrayList<String>();
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            recipients.add(feedbackPath.getRecipientId());
        }
        return recipients;
    }

    /**
     * Returns a list of feedback paths converted from the spreadsheet data.
     */
    public static List<FeedbackPathAttributes> getFeedbackPathsFromSpreadsheetData(
            String courseId, String customFeedbackPathsSpreadsheetData) {
        Gson gson = new Gson();
        TypeToken<List<List<String>>> token = new TypeToken<List<List<String>>>(){};
        List<List<String>> customFeedbackPaths =
                gson.fromJson(customFeedbackPathsSpreadsheetData, token.getType());
        List<FeedbackPathAttributes> feedbackPaths = new ArrayList<FeedbackPathAttributes>();
        for (List<String> feedbackPath : customFeedbackPaths) {
            feedbackPaths.add(
                    new FeedbackPathAttributes(courseId, feedbackPath.get(0), feedbackPath.get(1)));
        }

        return feedbackPaths;
    }

    /**
     * Checks that the feedback path participants are valid for this question.
     */
    public String validateCustomFeedbackPathsParticipants(
            List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        Set<String> studentEmails = new HashSet<String>();
        Set<String> instructorEmails = new HashSet<String>();
        Set<String> teamNames = new HashSet<String>();
        Map<String, String> studentEmailToTeamNameMap = new HashMap<String, String>();
        Map<String, Set<String>> teamNameToStudentEmailsMap = new HashMap<String, Set<String>>();

        populateCourseData(students, instructors, studentEmails, instructorEmails, teamNames,
                           studentEmailToTeamNameMap, teamNameToStudentEmailsMap);

        // Check for non-existent participants
        Set<String> nonExistentParticipants =
                getNonExistentParticipantsFromFeedbackPaths(studentEmails, instructorEmails, teamNames);

        if (!nonExistentParticipants.isEmpty()) {
            return "Unable to save question as the following feedback path participants do not exist: "
                    + StringHelper.removeEnclosingSquareBrackets(
                            SanitizationHelper.sanitizeForHtml(nonExistentParticipants).toString()) + ".";
        }

        // Check validity of feedback paths for contrib questions
        // Both the giver and recipient must be a student
        // If a student is a giver, all the student's team members must also be givers
        // Each giver should have all the members in his/her team as a recipient
        StringBuilder errorMsg = new StringBuilder(200);

        if (questionType == FeedbackQuestionType.CONTRIB) {
            Map<String, Set<String>> teamNameToGiverIdsMap = new HashMap<String, Set<String>>();
            Map<String, Set<String>> giverIdToRecipientIdsMap = new HashMap<String, Set<String>>();

            if (isFeedbackPathsGiverTypeStudents()
                    && isFeedbackPathsRecipientTypeStudents()) {
                populateFeedbackPathsMappings(
                        studentEmailToTeamNameMap, teamNameToGiverIdsMap, giverIdToRecipientIdsMap);

                if (!isAllStudentsInTeamGivers(teamNameToStudentEmailsMap, teamNameToGiverIdsMap)) {
                    errorMsg.append("All the students in a team must be a giver. ");
                }

                if (!isAllGiversTeamMembersRecipients(
                        studentEmailToTeamNameMap, teamNameToStudentEmailsMap, giverIdToRecipientIdsMap)) {
                    errorMsg.append("The student must give feedback to all his/her team members"
                                    + " including himself/herself. ");
                }
            } else {
                errorMsg.append("Both the giver and recipient must be a student. ");
            }
        }

        return errorMsg.toString().trim();
    }

    private static void populateCourseData(
            List<StudentAttributes> students, List<InstructorAttributes> instructors,
            Set<String> studentEmails, Set<String> instructorEmails, Set<String> teamNames,
            Map<String, String> studentEmailToTeamNameMap, Map<String, Set<String>> teamNameToStudentEmailsMap) {
        for (StudentAttributes student : students) {
            studentEmails.add(student.getEmail());
            teamNames.add(student.getTeam());
            studentEmailToTeamNameMap.put(student.getEmail(), student.getTeam());
            Set<String> teamMembers = teamNameToStudentEmailsMap.get(student.getTeam());
            if (teamMembers == null) {
                teamMembers = new HashSet<String>();
            }
            teamMembers.add(student.getEmail());
            teamNameToStudentEmailsMap.put(student.getTeam(), teamMembers);
        }

        for (InstructorAttributes instructor : instructors) {
            instructorEmails.add(instructor.getEmail());
        }
    }

    private Set<String> getNonExistentParticipantsFromFeedbackPaths(
            Set<String> studentEmails, Set<String> instructorEmails, Set<String> teamNames) {
        Set<String> nonExistentParticipants = new HashSet<String>();

        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            boolean isFeedbackPathGiverTypeNonExistent =
                    feedbackPath.getFeedbackPathGiverType().isEmpty();
            boolean isFeedbackPathGiverStudentNonExistent =
                    feedbackPath.isFeedbackPathGiverAStudent()
                    && !studentEmails.contains(feedbackPath.getGiverId());
            boolean isFeedbackPathGiverInstructorNonExistent =
                    feedbackPath.isFeedbackPathGiverAnInstructor()
                    && !instructorEmails.contains(feedbackPath.getGiverId());
            boolean isFeedbackPathGiverTeamNonExistent =
                    feedbackPath.isFeedbackPathGiverATeam()
                    && !teamNames.contains(feedbackPath.getGiverId());

            boolean isFeedbackPathRecipientTypeNonExistent =
                    feedbackPath.getFeedbackPathRecipientType().isEmpty();
            boolean isFeedbackPathRecipientStudentNonExistent =
                    feedbackPath.isFeedbackPathRecipientAStudent()
                    && !studentEmails.contains(feedbackPath.getRecipientId());
            boolean isFeedbackPathRecipientInstructorNonExistent =
                    feedbackPath.isFeedbackPathRecipientAnInstructor()
                    && !instructorEmails.contains(feedbackPath.getRecipientId());
            boolean isFeedbackPathRecipientTeamNonExistent =
                    feedbackPath.isFeedbackPathRecipientATeam()
                    && !teamNames.contains(feedbackPath.getRecipientId());

            if (isFeedbackPathGiverTypeNonExistent
                    || isFeedbackPathGiverStudentNonExistent
                    || isFeedbackPathGiverInstructorNonExistent
                    || isFeedbackPathGiverTeamNonExistent) {
                nonExistentParticipants.add(feedbackPath.getGiver());
            }

            if (isFeedbackPathRecipientTypeNonExistent
                    || isFeedbackPathRecipientStudentNonExistent
                    || isFeedbackPathRecipientInstructorNonExistent
                    || isFeedbackPathRecipientTeamNonExistent) {
                nonExistentParticipants.add(feedbackPath.getRecipient());
            }
        }

        return nonExistentParticipants;
    }

    private void populateFeedbackPathsMappings(
            Map<String, String> studentEmailToTeamNameMap, Map<String, Set<String>> teamNameToGiverIdsMap,
            Map<String, Set<String>> giverIdToRecipientIdsMap) {
        for (FeedbackPathAttributes feedbackPath : feedbackPaths) {
            String giverId = feedbackPath.getGiverId();
            String giverTeam = studentEmailToTeamNameMap.get(giverId);
            Set<String> giverIds = teamNameToGiverIdsMap.get(giverTeam);
            if (giverIds == null) {
                giverIds = new HashSet<String>();
            }
            giverIds.add(giverId);
            teamNameToGiverIdsMap.put(giverTeam, giverIds);

            Set<String> recipientsOfGiver = giverIdToRecipientIdsMap.get(giverId);
            if (recipientsOfGiver == null) {
                recipientsOfGiver = new HashSet<String>();
            }
            recipientsOfGiver.add(feedbackPath.getRecipientId());
            giverIdToRecipientIdsMap.put(giverId, recipientsOfGiver);
        }
    }

    private static boolean isAllStudentsInTeamGivers(
            Map<String, Set<String>> teamNameToStudentEmailsMap, Map<String, Set<String>> teamNameToGiverIdsMap) {
        for (String teamName : teamNameToGiverIdsMap.keySet()) {
            if (!teamNameToStudentEmailsMap.get(teamName).equals(teamNameToGiverIdsMap.get(teamName))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllGiversTeamMembersRecipients(
            Map<String, String> studentEmailToTeamNameMap, Map<String, Set<String>> teamNameToStudentEmailsMap,
            Map<String, Set<String>> giverIdToRecipientIdsMap) {
        for (String giverId : giverIdToRecipientIdsMap.keySet()) {
            String giverTeam = studentEmailToTeamNameMap.get(giverId);
            if (!teamNameToStudentEmailsMap.get(giverTeam).equals(giverIdToRecipientIdsMap.get(giverId))) {
                return false;
            }
        }
        return true;
    }
}
