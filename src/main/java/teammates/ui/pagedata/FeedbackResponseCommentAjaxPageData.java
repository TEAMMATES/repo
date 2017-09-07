package teammates.ui.pagedata;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.FeedbackResponseCommentRow;

/*
 * PageData: to be used for {@link FeedbackResponseCommentAttributes} in Ajax operations
 */
public class FeedbackResponseCommentAjaxPageData extends PageData {
    public FeedbackResponseCommentAttributes comment;
    public String commentId;
    public String giverName;
    public String recipientName;
    public String showCommentToString;
    public String showGiverNameToString;
    public String errorMessage;
    public String editedCommentDetails;
    public String giverRole;
    public boolean isError;
    public FeedbackQuestionAttributes question;
    public Map<String, String> commentGiverNameEmailTable;
    public String moderatedPersonEmail;
    public boolean moderation;
    public double sessionTimeZone;

    public FeedbackResponseCommentAjaxPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public FeedbackResponseCommentRow getComment() {
        FeedbackResponseCommentRow frc =
                new FeedbackResponseCommentRow(comment, comment.giverEmail, giverName, recipientName,
                                               showCommentToString, showGiverNameToString,
                                               getResponseVisibilities(), commentGiverNameEmailTable, sessionTimeZone);
        frc.enableEditDelete();

        return frc;
    }

    private Map<FeedbackParticipantType, Boolean> getResponseVisibilities() {
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };

        Map<FeedbackParticipantType, Boolean> responseVisibilities = new HashMap<>();
        for (FeedbackParticipantType type : relevantTypes) {
            responseVisibilities.put(type, isResponseVisibleTo(type, question));
        }

        return responseVisibilities;
    }

    public String[] getCommentIds() {
        return commentId.split("-");
    }

    public boolean isResponseVisibleTo(FeedbackParticipantType participantType, FeedbackQuestionAttributes question) {
        switch (participantType) {
        case GIVER:
            return question.isResponseVisibleTo(FeedbackParticipantType.GIVER);
        case INSTRUCTORS:
            return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
        case OWN_TEAM_MEMBERS:
            return question.giverType != FeedbackParticipantType.INSTRUCTORS
                   && question.giverType != FeedbackParticipantType.SELF
                   && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        case RECEIVER:
            return question.recipientType != FeedbackParticipantType.SELF
                   && question.recipientType != FeedbackParticipantType.NONE
                   && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
        case RECEIVER_TEAM_MEMBERS:
            return question.recipientType != FeedbackParticipantType.INSTRUCTORS
                    && question.recipientType != FeedbackParticipantType.SELF
                    && question.recipientType != FeedbackParticipantType.NONE
                    && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        case STUDENTS:
            return question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
        default:
            Assumption.fail("Invalid participant type");
            return false;
        }
    }

    public String getGiverRole() {
        return giverRole;
    }

    public void setGiverRole(String giverRole) {
        this.giverRole = giverRole;
    }

    public String getModeratedPersonEmail() {
        return moderatedPersonEmail;
    }

    public void setModeratedPersonEmail(String moderatedPersonEmail) {
        this.moderatedPersonEmail = moderatedPersonEmail;
    }

    public boolean isModeration() {
        return moderation;
    }

    public void setModeration(boolean moderation) {
        this.moderation = moderation;
    }

    public String createEditedCommentDetails(String giverName, String editorName) {
        boolean isGiverAnonymous = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT.equals(giverName);
        return "From: " + giverName + " [" + TimeHelper.formatDateTimeForSessions(comment.createdAt, sessionTimeZone) + "] "
                + "(last edited " + (isGiverAnonymous ? "" : "by " + editorName + " ")
                + "at " + TimeHelper.formatDateTimeForSessions(comment.lastEditedAt, sessionTimeZone) + ")";
    }
}
