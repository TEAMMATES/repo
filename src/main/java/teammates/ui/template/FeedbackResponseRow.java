package teammates.ui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackResponseRow {

    private int questionNumber;
    private String questionText;
    private String questionMoreInfo;
    private String responseText;
    private List<FeedbackResponseCommentRow> responseComments;

    public FeedbackResponseRow(int fbIndex, int personIndex, String personType,
                               FeedbackResponseAttributes response, FeedbackSessionResultsBundle results) {
        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = results.questions.get(questionId);
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        this.questionNumber = question.questionNumber;
        this.questionText = results.getQuestionText(questionId);
        this.questionMoreInfo = questionDetails.getQuestionAdditionalInfoHtml(this.questionNumber,
                                                                              personType + "-" + personIndex
                                                                                         + "-session-" + fbIndex);
        if ("recipient".equals(personType)) {
            this.responseText = response.getResponseDetails().getAnswerHtmlInstructorView(questionDetails);
        } else if ("giver".equals(personType)) {
            this.responseText = results.getResponseAnswerHtml(response, question);
        }
        this.responseComments = new ArrayList<>();
        List<FeedbackResponseCommentAttributes> frcs = results.responseComments.get(response.getId());

        Map<FeedbackParticipantType, Boolean> responseVisibilities = new HashMap<>();

        for (FeedbackParticipantType participant : question.showResponsesTo) {
            responseVisibilities.put(participant, true);
        }
        String giverName = results.getNameForEmail(response.giver);
        if (frcs != null) {
            for (FeedbackResponseCommentAttributes frc : frcs) {
                String showCommentTo = StringHelper.removeEnclosingSquareBrackets(frc.showCommentTo.toString());
                String showGiverNameToString = StringHelper.removeEnclosingSquareBrackets(frc.showGiverNameTo.toString());
                String recipientName = results.getNameForEmail(response.recipient);
                String giverEmail = frc.commentGiver;
                Map<String, String> commentGiverEmailToNameTable = results.commentGiverEmailToNameTable;
                FeedbackResponseCommentRow responseCommentRow = new FeedbackResponseCommentRow(frc,
                        giverEmail, giverName, recipientName, showCommentTo, showGiverNameToString, responseVisibilities,
                        commentGiverEmailToNameTable, results.getTimeZone(), question);
                boolean isInstructorGiver = results.roster.isInstructorOfCourse(giverEmail);
                // Instructor cannot edit/delete feedback participant's comments from results page
                if (isInstructorGiver) {
                    InstructorAttributes instructor = results.roster.getInstructorForEmail(giverEmail);
                    boolean isCommentGiverInstructor = giverEmail.equals(instructor.email);
                    boolean isAllowedToSubmitSessionsInBothSection =
                            instructor.isAllowedForPrivilege(response.giverSection, response.feedbackSessionName,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                                    && instructor.isAllowedForPrivilege(response.recipientSection,
                                    response.feedbackSessionName,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
                    if (isCommentGiverInstructor || isAllowedToSubmitSessionsInBothSection) {
                        responseCommentRow.enableEditDelete();
                    }
                }
                this.responseComments.add(responseCommentRow);
            }
        }
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText() {
        return SanitizationHelper.sanitizeForHtml(questionText);
    }

    public String getQuestionMoreInfo() {
        return questionMoreInfo;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<FeedbackResponseCommentRow> getResponseComments() {
        return responseComments;
    }

}
