package teammates.ui.template;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.ui.controller.InstructorFeedbackResultsPageData;

public class InstructorResultsQuestionTable {

    private String courseId;
    private String feedbackSessionName;
    
    private String questionStatisticsHtml;  
    private String panelClass;
    private String responsesBodyClass;
    
    private List<InstructorResultsResponseRow> responses;
    
    // store the details of the question for the non-display purposes 
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    private String questionStatisticsTable;
    
    private boolean isQuestionHasResponses;
    private boolean isShowResponseRows;
    
    private boolean isCollapsible;
    private boolean isBoldQuestionNumber;

    private List<ElementTag> columns;
    private Map<String, Boolean> isColumnSortable;

    public InstructorResultsQuestionTable(InstructorFeedbackResultsPageData data,
                                          List<FeedbackResponseAttributes> responses,
                                          String questionStatisticsHtml,
                                          List<InstructorResultsResponseRow> responseRows,
                                          FeedbackQuestionAttributes question,
                                          String additionalInfoId,
                                          List<ElementTag> columns,
                                          Map<String, Boolean> isColumnSortable) {
        this.courseId = question.courseId;
        this.feedbackSessionName = question.feedbackSessionName;
        
        this.questionStatisticsHtml = questionStatisticsHtml;
        this.responses = responseRows;
        
        this.isQuestionHasResponses = !responses.isEmpty(); //TODO: just check is response is empty in jsp? 
        
        this.question = question;
        
        this.questionText = data.bundle.getQuestionText(question.getId());
        
        this.panelClass = responses.isEmpty() ? 
                          "panel-default" : 
                          "panel-info";
        
        this.responsesBodyClass = data.bundle.isComplete() && !data.isShouldCollapsed() ? 
                                  "panel-collapse collapse in" :
                                  "panel-collapse collapse";
        
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        this.additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, additionalInfoId);        
        this.questionStatisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, data, data.bundle, "question");
        
        this.columns = columns;
        
        this.isBoldQuestionNumber = true;
        this.isColumnSortable = isColumnSortable;
    }

    public String getQuestionStatisticsHtml() {
        return questionStatisticsHtml;
    }

    public List<InstructorResultsResponseRow> getResponses() {
        return responses;
    }

    public String getPanelClass() {
        return panelClass;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalInfoText() {
        return additionalInfoText;
    }

    public String getQuestionStatisticsTable() {
        return questionStatisticsTable;
    }

    public boolean isQuestionHasResponses() {
        return isQuestionHasResponses;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isShowResponseRows() {
        return isShowResponseRows;
    }

    public void setShowResponseRows(boolean isShowResponseRows) {
        this.isShowResponseRows = isShowResponseRows;
    }

    public List<ElementTag> getColumns() {
        return columns;
    }

    public void setColumns(List<ElementTag> columns) {
        this.columns = columns;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setQuestionStatisticsHtml(String questionStatisticsHtml) {
        this.questionStatisticsHtml = questionStatisticsHtml;
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public void setResponses(List<InstructorResultsResponseRow> responses) {
        this.responses = responses;
    }

    public void setQuestion(FeedbackQuestionAttributes question) {
        this.question = question;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setAdditionalInfoText(String additionalInfoText) {
        this.additionalInfoText = additionalInfoText;
    }

    public void setQuestionStatisticsTable(String questionStatisticsTable) {
        this.questionStatisticsTable = questionStatisticsTable;
    }

    public void setQuestionHasResponses(boolean isQuestionHasResponses) {
        this.isQuestionHasResponses = isQuestionHasResponses;
    }

    public boolean isCollapsible() {
        return isCollapsible;
    }

    public void setCollapsible(boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }
    
    public boolean isBoldQuestionNumber() {
        return isBoldQuestionNumber;
    }

    public void setBoldQuestionNumber(boolean isBoldQuestionNumber) {
        this.isBoldQuestionNumber = isBoldQuestionNumber;
    }
    
    public Map<String, Boolean> getIsColumnSortable() {
        return isColumnSortable;
    }

    public void setIsColumnSortable(Map<String, Boolean> isColumnSortable) {
        this.isColumnSortable = isColumnSortable;
    }

    public String getResponsesBodyClass() {
        return responsesBodyClass;
    }

    public void setResponsesBodyClass(String responsesBodyClass) {
        this.responsesBodyClass = responsesBodyClass;
    }

    public static void sortByQuestionNumber(List<InstructorResultsQuestionTable> questionTables) {
        Collections.sort(questionTables, new Comparator<InstructorResultsQuestionTable>() {
            public int compare(InstructorResultsQuestionTable questionTable1, InstructorResultsQuestionTable questionTable2) {
                boolean result = questionTable1.question.questionNumber < questionTable2.question.questionNumber;

                return result ? -1 : 1;
            }
        });
    }
    
}
