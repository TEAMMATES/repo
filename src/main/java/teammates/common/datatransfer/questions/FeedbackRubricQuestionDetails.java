package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {

    private static final Logger log = Logger.getLogger();
    private static final String STATISTICS_NO_VALUE_STRING = "-";

    private boolean hasAssignedWeights;
    private List<Double> rubricWeights;
    private int numOfRubricChoices;
    private List<String> rubricChoices;
    private int numOfRubricSubQuestions;
    private List<String> rubricSubQuestions;
    private List<List<String>> rubricDescriptions;

    public FeedbackRubricQuestionDetails() {
        super(FeedbackQuestionType.RUBRIC);

        this.hasAssignedWeights = false;
        this.rubricWeights = new ArrayList<Double>();
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<String>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<String>();
        this.initializeRubricDescriptions();
    }

    public FeedbackRubricQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RUBRIC, questionText);

        this.hasAssignedWeights = false;
        this.rubricWeights = new ArrayList<Double>();
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<String>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<String>();
        this.initializeRubricDescriptions();
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String numOfRubricChoicesString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS);
        String numOfRubricSubQuestionsString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS);

        if (numOfRubricChoicesString == null || numOfRubricSubQuestionsString == null) {
            return false;
        }

        String hasAssignedWeightsString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED);

        boolean hasAssignedWeights = "on".equals(hasAssignedWeightsString);
        int numOfRubricChoices = Integer.parseInt(numOfRubricChoicesString);
        int numOfRubricSubQuestions = Integer.parseInt(numOfRubricSubQuestionsString);
        List<Double> rubricWeights = getRubricWeights(requestParameters, numOfRubricChoices, hasAssignedWeights);
        List<String> rubricChoices = getRubricChoices(requestParameters, numOfRubricChoices);
        List<String> rubricSubQuestions = getSubQuestions(requestParameters, numOfRubricSubQuestions);
        List<List<String>> rubricDescriptions = getRubricQuestionDescriptions(requestParameters,
                                                                              numOfRubricChoices,
                                                                              numOfRubricSubQuestions);

        // Set details
        setRubricQuestionDetails(hasAssignedWeights, rubricWeights, rubricChoices, rubricSubQuestions, rubricDescriptions);

        if (!isValidDescriptionSize()) {
            // If description sizes are invalid, default to empty descriptions.
            initializeRubricDescriptions();
        }

        return true;
    }

    private List<Double> getRubricWeights(Map<String, String[]> requestParameters, int numOfRubricChoices,
                                         boolean hasAssignedWeights) {
        List<Double> rubricWeights = new ArrayList<Double>();

        if (!hasAssignedWeights) {
            return rubricWeights;
        }

        for (int i = 0; i < numOfRubricChoices; i++) {

            String weight = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-" + i);
            String choice = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + i);

            if (choice == null) {
                continue;
            }

            try {
                rubricWeights.add(Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                // Do not add weight to rubricWeights if the weight cannot be parsed
                log.warning("Failed to parse weight for rubric question: " + weight);
            }
        }

        return rubricWeights;
    }

    private List<String> getRubricChoices(Map<String, String[]> requestParameters, int numOfRubricChoices) {
        List<String> rubricChoices = new ArrayList<String>();
        for (int i = 0; i < numOfRubricChoices; i++) {
            String choice = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                  Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + i);
            if (choice != null) {
                rubricChoices.add(choice);
            }
        }
        return rubricChoices;
    }

    private List<String> getSubQuestions(Map<String, String[]> requestParameters, int numOfRubricSubQuestions) {
        List<String> rubricSubQuestions = new ArrayList<String>();
        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            String subQuestion = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-" + i);
            if (subQuestion != null) {
                rubricSubQuestions.add(subQuestion);
            }
        }
        return rubricSubQuestions;
    }

    private List<List<String>> getRubricQuestionDescriptions(Map<String, String[]> requestParameters,
                                                             int numOfRubricChoices, int numOfRubricSubQuestions) {
        List<List<String>> rubricDescriptions = new ArrayList<List<String>>();
        int descRows = -1;
        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            boolean rowAdded = false;
            for (int j = 0; j < numOfRubricChoices; j++) {
                String paramName = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + "-" + i + "-" + j;
                String description = HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
                if (description != null) {
                    if (!rowAdded) {
                        descRows++;
                        rubricDescriptions.add(new ArrayList<String>());
                        rowAdded = true;
                    }
                    rubricDescriptions.get(descRows).add(description);
                }
            }
        }
        return rubricDescriptions;
    }

    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidDescriptionSize() {
        if (rubricDescriptions.size() != numOfRubricSubQuestions) {
            return false;
        }
        for (int i = 0; i < rubricDescriptions.size(); i++) {
            if (rubricDescriptions.get(i).size() != numOfRubricChoices) {
                return false;
            }
        }
        return true;
    }

    private void setRubricQuestionDetails(boolean hasAssignedWeights,
                                          List<Double> rubricWeights,
                                          List<String> rubricChoices,
                                          List<String> rubricSubQuestions,
                                          List<List<String>> rubricDescriptions) {
        this.hasAssignedWeights = hasAssignedWeights;
        this.rubricWeights = rubricWeights;
        this.numOfRubricChoices = rubricChoices.size();
        this.rubricChoices = rubricChoices;
        this.numOfRubricSubQuestions = rubricSubQuestions.size();
        this.rubricSubQuestions = rubricSubQuestions;
        this.rubricDescriptions = rubricDescriptions;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RUBRIC;
    }

    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRubricQuestionDetails newRubricDetails = (FeedbackRubricQuestionDetails) newDetails;
        // TODO: need to check for exact match.

        // Responses require deletion if choices change
        if (this.numOfRubricChoices != newRubricDetails.numOfRubricChoices
                || !this.rubricChoices.containsAll(newRubricDetails.rubricChoices)
                || !newRubricDetails.rubricChoices.containsAll(this.rubricChoices)) {
            return true;
        }

        // Responses require deletion if sub-questions change
        return this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions
            || !this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions)
            || !newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions);
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) existingResponseDetails;

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml =
                getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml =
                getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, true, frd);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString,
                                                        sessionIsOpen, true, frd);

        // Create submission form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.RESPONSE_INDEX, responseNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml,
                Slots.TABLE_BODY_HTML, tableBodyHtml,
                Slots.MOBILE_HTML, mobileHtml,
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {

        String questionNumberString = Integer.toString(qnIdx);
        String responseNumberString = Integer.toString(responseIdx);

        String tableHeaderFragmentHtml =
                getSubmissionFormTableHeaderFragmentHtml(questionNumberString, responseNumberString);
        String tableBodyHtml =
                getSubmissionFormTableBodyHtml(questionNumberString, responseNumberString, sessionIsOpen, false, null);
        String mobileHtml = getSubmissionFormMobileHtml(questionNumberString, responseNumberString,
                                                        sessionIsOpen, false, null);

        // Create submission form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.RESPONSE_INDEX, responseNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml,
                Slots.TABLE_BODY_HTML, tableBodyHtml,
                Slots.MOBILE_HTML, mobileHtml,
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
    }

    private String getSubmissionFormTableHeaderFragmentHtml(String questionNumberString, String responseNumberString) {
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT;

        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.RESPONSE_INDEX, responseNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(i)));
            // TODO display numerical value of option
            tableHeaderFragmentHtml.append(tableHeaderCell).append(Const.EOL);
        }
        return tableHeaderFragmentHtml.toString();
    }

    private String getSubmissionFormTableBodyHtml(String questionNumberString, String responseNumberString,
                                                  boolean sessionIsOpen, boolean isExistingResponse,
                                                  FeedbackRubricResponseDetails frd) {
        StringBuilder tableBodyHtml = new StringBuilder();
        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_BODY;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String tableBodyCell =
                        Templates.populateTemplate(tableBodyFragmentTemplate,
                                Slots.QUESTION_INDEX, questionNumberString,
                                Slots.RESPONSE_INDEX, responseNumberString,
                                Slots.COL, Integer.toString(j),
                                Slots.ROW, Integer.toString(i),
                                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(i, j)),
                                // Check if existing choice for sub-question == current choice
                                Slots.CHECKED, isExistingResponse && frd.getAnswer(i) == j ? "checked" : "",
                                Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                tableBodyFragmentHtml.append(tableBodyCell).append(Const.EOL);
            }
            // Get entire row
            String tableRow =
                    Templates.populateTemplate(tableBodyTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.RESPONSE_INDEX, responseNumberString,
                            Slots.ROW, Integer.toString(i),
                            Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                                              + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)),
                            Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow).append(Const.EOL);
        }
        return tableBodyHtml.toString();
    }

    private String getSubmissionFormMobileHtml(String questionNumberString, String responseNumberString,
            boolean sessionIsOpen, boolean isExistingResponse, FeedbackRubricResponseDetails frd) {
        StringBuilder mobileHtml = new StringBuilder();
        String mobilePanelTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL;
        String mobilePanelFragmentTemplate = FormTemplates.RUBRIC_SUBMISSION_FORM_MOBILE_PANEL_FRAGMENT;

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder panelBody = new StringBuilder();
            for (int j = 0; j < numOfRubricChoices; j++) {
                String panelBodyFragment = Templates.populateTemplate(mobilePanelFragmentTemplate,
                        Slots.QUESTION_INDEX, questionNumberString,
                        Slots.RESPONSE_INDEX, responseNumberString,
                        Slots.COL, Integer.toString(j),
                        Slots.ROW, Integer.toString(i),
                        Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                        Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(i, j)),
                        // Check if existing choice for sub-question == current choice
                        Slots.CHECKED, isExistingResponse && frd.getAnswer(i) == j ? "checked" : "",
                        Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(j)),
                        Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
                panelBody.append(panelBodyFragment);
            }
            String panel = Templates.populateTemplate(mobilePanelTemplate,
                    Slots.PANEL_BODY, panelBody.toString(),
                    Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                            + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)));
            mobileHtml.append(panel).append(Const.EOL);
        }
        return mobileHtml.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        String questionNumberString = Integer.toString(questionNumber);
        DecimalFormat weightFormat = new DecimalFormat("#.##");

        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_HEADER_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_CHOICE_VALUE, SanitizationHelper.sanitizeForHtml(rubricChoices.get(i)),
                            Slots.RUBRIC_PARAM_CHOICE, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE);
            tableHeaderFragmentHtml.append(tableHeaderCell).append(Const.EOL);
        }

        // Create rubric weights row
        StringBuilder tableWeightFragmentHtml = new StringBuilder();
        String tableWeightFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_WEIGHT_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {
            String tableWeightCell =
                    Templates.populateTemplate(tableWeightFragmentTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.COL, Integer.toString(i),
                            Slots.RUBRIC_WEIGHT, hasAssignedWeights ? weightFormat.format(rubricWeights.get(i)) : "0",
                            Slots.RUBRIC_PARAM_WEIGHT, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT);
            tableWeightFragmentHtml.append(tableWeightCell).append(Const.EOL);
        }

        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();

        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_EDIT_FORM_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_EDIT_FORM_BODY;

        for (int j = 0; j < numOfRubricSubQuestions; j++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            for (int i = 0; i < numOfRubricChoices; i++) {
                String tableBodyCell =
                        Templates.populateTemplate(tableBodyFragmentTemplate,
                                Slots.QUESTION_INDEX, questionNumberString,
                                Slots.COL, Integer.toString(i),
                                Slots.ROW, Integer.toString(j),
                                Slots.DESCRIPTION, SanitizationHelper.sanitizeForHtml(this.getDescription(j, i)),
                                Slots.RUBRIC_PARAM_DESCRIPTION, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION);
                tableBodyFragmentHtml.append(tableBodyCell).append(Const.EOL);
            }

            // Get entire row
            String tableRow =
                    Templates.populateTemplate(tableBodyTemplate,
                            Slots.QUESTION_INDEX, questionNumberString,
                            Slots.ROW, Integer.toString(j),
                            Slots.SUB_QUESTION, SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(j)),
                            Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString(),
                            Slots.RUBRIC_PARAM_SUB_QUESTION, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION);
            tableBodyHtml.append(tableRow).append(Const.EOL);
        }

        // Create edit form
        return Templates.populateTemplate(
                FormTemplates.RUBRIC_EDIT_FORM,
                Slots.QUESTION_INDEX, questionNumberString,
                Slots.CURRENT_ROWS, Integer.toString(this.numOfRubricSubQuestions),
                Slots.CURRENT_COLS, Integer.toString(this.numOfRubricChoices),
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml.toString(),
                Slots.RUBRIC_TABLE_WEIGHT_ROW_FRAGMENT_HTML, tableWeightFragmentHtml.toString(),
                Slots.TABLE_BODY_HTML, tableBodyHtml.toString(),
                Slots.RUBRIC_PARAM_NUM_ROWS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS,
                Slots.RUBRIC_PARAM_NUM_COLS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS,
                Slots.CHECK_ASSIGN_WEIGHTS, hasAssignedWeights ? "checked" : "",
                Slots.RUBRIC_TOOLTIPS_ASSIGN_WEIGHTS, Const.Tooltips.FEEDBACK_QUESTION_RUBRIC_ASSIGN_WEIGHTS,
                Slots.RUBRIC_PARAM_ASSIGN_WEIGHTS, Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED);
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add some choices by default
        numOfRubricChoices = 4;
        rubricChoices.add("Strongly Disagree");
        rubricChoices.add("Disagree");
        rubricChoices.add("Agree");
        rubricChoices.add("Strongly Agree");

        hasAssignedWeights = false;

        // Add some sub-questions by default
        numOfRubricSubQuestions = 2;
        rubricSubQuestions.add("This student participates well in online discussions.");
        rubricSubQuestions.add("This student completes assigned tasks on time.");

        initializeRubricDescriptions();

        setDescription(0, 0, "Rarely or never responds.");
        setDescription(0, 1, "Occasionally responds, but never initiates discussions.");
        setDescription(0, 2, "Takes part in discussions and sometimes initiates discussions.");
        setDescription(0, 3, "Initiates discussions frequently, and engages the team.");

        setDescription(1, 0, "Rarely or never completes tasks.");
        setDescription(1, 1, "Often misses deadlines.");
        setDescription(1, 2, "Occasionally misses deadlines.");
        setDescription(1, 3, "Tasks are always completed before the deadline.");

        return "<div id=\"rubricForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    private void initializeRubricDescriptions() {
        rubricDescriptions = new ArrayList<List<String>>();
        for (int subQns = 0; subQns < numOfRubricSubQuestions; subQns++) {
            List<String> descList = new ArrayList<String>();
            for (int ch = 0; ch < numOfRubricChoices; ch++) {
                descList.add("");
            }
            rubricDescriptions.add(descList);
        }
    }

    private void setDescription(int row, int col, String description) {
        this.rubricDescriptions.get(row).set(col, description);
    }

    /**
     * Gets the description for given sub-question and choice
     * @param subQuestion
     * @param choice
     */
    private String getDescription(int subQuestion, int choice) {
        return rubricDescriptions.get(subQuestion).get(choice);
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder subQuestionListHtml = new StringBuilder();

        if (numOfRubricSubQuestions > 0) {
            subQuestionListHtml.append("<p>");
            for (int i = 0; i < numOfRubricSubQuestions; i++) {
                String subQuestionFragment =
                        StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1)
                        + ") " + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i));
                subQuestionListHtml.append(subQuestionFragment);
                subQuestionListHtml.append("<br>");
            }
            subQuestionListHtml.append("</p>");
        }

        String additionalInfo = Templates.populateTemplate(
                FormTemplates.RUBRIC_ADDITIONAL_INFO,
                Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
                Slots.RUBRIC_ADDITIONAL_INFO_FRAGMENTS, subQuestionListHtml.toString());

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
                                                  FeedbackQuestionAttributes question, String studentEmail,
                                                  FeedbackSessionResultsBundle bundle, String view) {

        List<FeedbackResponseAttributes> responsesForStatistics =
                filterResponsesForStatistics(responses, question, studentEmail, bundle, view);

        FeedbackRubricQuestionDetails fqd =
                (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        int[][] responseFrequency = calculateResponseFrequency(responsesForStatistics, fqd);
        float[][] rubricStats = calculateRubricStats(responsesForStatistics, fqd);

        DecimalFormat weightFormat = new DecimalFormat("#.##");

        // Create table row header fragments
        StringBuilder tableHeaderFragmentHtml = new StringBuilder();
        String tableHeaderFragmentTemplate = FormTemplates.RUBRIC_RESULT_STATS_HEADER_FRAGMENT;
        for (int i = 0; i < numOfRubricChoices; i++) {

            String header = SanitizationHelper.sanitizeForHtml(rubricChoices.get(i))
                          + (fqd.hasAssignedWeights
                            ? "<span style=\"font-weight:normal;\"> (Weight: "
                              + weightFormat.format(rubricWeights.get(i)) + ")</span>"
                            : "");

            String tableHeaderCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate, Slots.RUBRIC_CHOICE_VALUE, header);
            tableHeaderFragmentHtml.append(tableHeaderCell).append(Const.EOL);
        }

        if (fqd.hasAssignedWeights) {
            String tableHeaderAverageCell =
                    Templates.populateTemplate(tableHeaderFragmentTemplate,
                            Slots.RUBRIC_CHOICE_VALUE, "Average");
            tableHeaderFragmentHtml.append(tableHeaderAverageCell).append(Const.EOL);
        }

        // Create table body
        StringBuilder tableBodyHtml = new StringBuilder();

        String tableBodyFragmentTemplate = FormTemplates.RUBRIC_RESULT_STATS_BODY_FRAGMENT;
        String tableBodyTemplate = FormTemplates.RUBRIC_RESULT_STATS_BODY;
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfAverage = new DecimalFormat("0.00");

        for (int i = 0; i < numOfRubricSubQuestions; i++) {
            StringBuilder tableBodyFragmentHtml = new StringBuilder();
            boolean isSubQuestionRespondedTo = responseFrequency[i][numOfRubricChoices] > 0;

            for (int j = 0; j < numOfRubricChoices; j++) {
                String percentageFrequencyString = isSubQuestionRespondedTo
                                                 ? df.format(rubricStats[i][j] * 100) + "%"
                                                 : STATISTICS_NO_VALUE_STRING;
                String tableBodyCell = Templates.populateTemplate(tableBodyFragmentTemplate,
                        Slots.RUBRIC_PERCENTAGE_FREQUENCY_OR_AVERAGE,
                        percentageFrequencyString + " (" + responseFrequency[i][j] + ")");
                tableBodyFragmentHtml.append(tableBodyCell).append(Const.EOL);
            }

            if (fqd.hasAssignedWeights) {
                String averageString = isSubQuestionRespondedTo
                                     ? dfAverage.format(rubricStats[i][numOfRubricChoices])
                                     : STATISTICS_NO_VALUE_STRING;
                String tableBodyAverageCell = Templates.populateTemplate(tableBodyFragmentTemplate,
                        Slots.RUBRIC_PERCENTAGE_FREQUENCY_OR_AVERAGE, averageString);
                tableBodyFragmentHtml.append(tableBodyAverageCell).append(Const.EOL);
            }

            // Get entire row
            String tableRow = Templates.populateTemplate(tableBodyTemplate,
                    Slots.SUB_QUESTION, StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1) + ") "
                            + SanitizationHelper.sanitizeForHtml(rubricSubQuestions.get(i)),
                    Slots.RUBRIC_ROW_BODY_FRAGMENTS, tableBodyFragmentHtml.toString());
            tableBodyHtml.append(tableRow).append(Const.EOL);
        }

        String statsTitle = "Response Summary";

        if ("student".equals(view)) {
            if (responses.size() == responsesForStatistics.size()) {
                statsTitle = "Response Summary (of visible responses)";
            } else {
                statsTitle = "Response Summary (of received responses)";
            }
        }

        return Templates.populateTemplate(
                FormTemplates.RUBRIC_RESULT_STATS,
                Slots.STATS_TITLE, statsTitle,
                Slots.TABLE_HEADER_ROW_FRAGMENT_HTML, tableHeaderFragmentHtml.toString(),
                Slots.TABLE_BODY_HTML, tableBodyHtml.toString());
    }

    /**
     * Returns a list of FeedbackResponseAttributes filtered according to view, question recipient type
     * for the Statistics Table
     */
    private List<FeedbackResponseAttributes> filterResponsesForStatistics(
            List<FeedbackResponseAttributes> responses, FeedbackQuestionAttributes question,
            String studentEmail, FeedbackSessionResultsBundle bundle, String view) {

        boolean isViewedByStudent = "student".equals(view);
        if (!isViewedByStudent) {
            return responses;
        }

        FeedbackParticipantType recipientType = question.getRecipientType();

        boolean isFilteringSkipped = recipientType.equals(FeedbackParticipantType.INSTRUCTORS)
                || recipientType.equals(FeedbackParticipantType.NONE)
                || recipientType.equals(FeedbackParticipantType.SELF);

        if (isFilteringSkipped) {
            return responses;
        }

        boolean isFilteringByTeams = recipientType.equals(FeedbackParticipantType.OWN_TEAM)
                || recipientType.equals(FeedbackParticipantType.TEAMS);

        List<FeedbackResponseAttributes> receivedResponses = new ArrayList<>();
        String recipientString = isFilteringByTeams ? bundle.getTeamNameForEmail(studentEmail) : studentEmail;

        for (FeedbackResponseAttributes response : responses) {
            boolean isReceivedResponse = response.recipient.equals(recipientString);
            if (isReceivedResponse) {
                receivedResponses.add(response);
            }
        }

        return receivedResponses;
    }

    /**
     * Calculates the statistics for rubric question
     * 
     * Returns a 2D float array to indicate the percentage frequency
     * a choice is selected and the average weight for the responses for each sub-question.
     *
     * Values are set to 0 if there are no responses to that sub-question.
     * Average value is set to 0 if there are no assigned weights.
     * 
     * e.g.
     * percentageFrequencyAndAverageValue[subQuestionIndex][choiceIndex]
     *  -> is the percentage choiceIndex is chosen for subQuestionIndex, for the given question/responses.
     * percentageFrequencyAndAverageValue[subQuestionIndex][numOfRubricChoices]
     *  -> is the average weight of the responses for the given sub-question
     *
     */
    private float[][] calculateRubricStats(List<FeedbackResponseAttributes> responses,
                                           FeedbackRubricQuestionDetails fqd) {
        int[][] responseFrequency = calculateResponseFrequency(responses, fqd);
        int numOfRubricChoices = fqd.numOfRubricChoices;
        float[][] percentageFrequencyAndAverageValue =
                new float[fqd.numOfRubricSubQuestions][numOfRubricChoices + 1];

        // calculate percentage frequencies and average value
        for (int i = 0; i < percentageFrequencyAndAverageValue.length; i++) {
            int totalForSubQuestion = responseFrequency[i][fqd.numOfRubricChoices];
            //continue to next row if no response for this sub-question
            if (totalForSubQuestion == 0) {
                continue;
            }
            // Divide responsesFrequency by totalForSubQuestion to get percentage
            for (int j = 0; j < numOfRubricChoices; j++) {
                percentageFrequencyAndAverageValue[i][j] =
                        (float) responseFrequency[i][j] / totalForSubQuestion;
            }
            // Calculate the average for each sub-question
            if (fqd.hasAssignedWeights) {
                for (int j = 0; j < numOfRubricChoices; j++) {
                    float choiceWeight =
                            (float) (fqd.rubricWeights.get(j) * percentageFrequencyAndAverageValue[i][j]);
                    percentageFrequencyAndAverageValue[i][numOfRubricChoices] += choiceWeight;
                }
            }
        }

        return percentageFrequencyAndAverageValue;
    }

    /**
     * Calculates the response frequency for each choice
     * Last element in the row stores the total number of responses for the sub-question.
     */
    private int[][] calculateResponseFrequency(List<FeedbackResponseAttributes> responses,
                                               FeedbackRubricQuestionDetails fqd) {
        int[][] responseFrequency =
                new int[fqd.numOfRubricSubQuestions][fqd.numOfRubricChoices + 1];
        int responseTotalIndex = fqd.numOfRubricChoices;

        // Count frequencies
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) response.getResponseDetails();
            for (int i = 0; i < fqd.numOfRubricSubQuestions; i++) {
                int chosenChoice = frd.getAnswer(i);
                if (chosenChoice != -1) {
                    responseFrequency[i][chosenChoice] += 1;
                    responseFrequency[i][responseTotalIndex] += 1;
                }
            }
        }
        return responseFrequency;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        StringBuilder csv = new StringBuilder();
        DecimalFormat dfWeight = new DecimalFormat("#.##");

        // table header
        for (int i = 0; i < rubricChoices.size(); i++) {

            String header = rubricChoices.get(i)
                          + (hasAssignedWeights
                            ? " (Weight: " + dfWeight.format(rubricWeights.get(i)) + ")"
                            : "");

            csv.append(',').append(SanitizationHelper.sanitizeForCsv(header));
        }

        if (hasAssignedWeights) {
            csv.append(",Average");
        }

        csv.append(Const.EOL);

        // table body
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfAverage = new DecimalFormat("0.00");

        int[][] responseFrequency = calculateResponseFrequency(responses, this);
        float[][] rubricStats = calculateRubricStats(responses, this);

        for (int i = 0; i < rubricSubQuestions.size(); i++) {
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);
            csv.append(SanitizationHelper.sanitizeForCsv(alphabeticalIndex + ") " + rubricSubQuestions.get(i)));
            boolean isSubQuestionRespondedTo = responseFrequency[i][numOfRubricChoices] > 0;
            for (int j = 0; j < rubricChoices.size(); j++) {
                String percentageFrequencyString = isSubQuestionRespondedTo
                                                 ? df.format(rubricStats[i][j] * 100) + "%"
                                                 : STATISTICS_NO_VALUE_STRING;
                csv.append("," + percentageFrequencyString + " (" + responseFrequency[i][j] + ")");
            }

            if (hasAssignedWeights) {
                String averageString = isSubQuestionRespondedTo
                                     ? dfAverage.format(rubricStats[i][rubricWeights.size()])
                                     : STATISTICS_NO_VALUE_STRING;
                csv.append(',').append(averageString);
            }

            csv.append(Const.EOL);
        }

        return csv.toString();
    }

    @Override
    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
        return SanitizationHelper.sanitizeForCsv("All Sub-Questions") + ","
             + SanitizationHelper.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
    }

    @Override
    public String getCsvHeader() {
        return "Choice Value";
    }

    @Override
    public String getCsvDetailedResponsesHeader() {
        return "Team" + "," + "Giver's Full Name" + ","
                + "Giver's Last Name" + "," + "Giver's Email" + ","
                + "Recipient's Team" + "," + "Recipient's Full Name" + ","
                + "Recipient's Last Name" + "," + "Recipient's Email" + ","
                + "Sub Question" + "," + getCsvHeader() + ","
                + "Choice Number" + Const.EOL;
    }

    @Override
    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
            FeedbackResponseAttributes feedbackResponseAttributes,
            FeedbackQuestionAttributes question) {

        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giver);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giver);
        String giverTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giver);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);

        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipient);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipient);
        String recipientTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipient);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);

        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) feedbackResponseAttributes.getResponseDetails();
        StringBuilder detailedResponsesRow = new StringBuilder(100);
        for (int i = 0; i < frd.answer.size(); i++) {
            int chosenIndex = frd.answer.get(i);
            String chosenChoiceNumber = "";
            String chosenChoiceValue = "";
            String chosenIndexString = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);

            if (chosenIndex == -1) {
                chosenChoiceValue = Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
            } else {
                chosenChoiceNumber = Integer.toString(chosenIndex + 1);
                chosenChoiceValue = rubricChoices.get(frd.answer.get(i));
            }

            detailedResponsesRow.append(
                    SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenIndexString) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceValue) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceNumber)
                    + Const.EOL);
        }

        return detailedResponsesRow.toString();
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"RUBRIC\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.RUBRIC + "</a></li>";
    }

    /**
     * For rubric questions,
     *      1) Description size should be valid
     *      2) At least 2 choices
     *      3) At least 1 sub-question
     *      4) Choices and sub-questions should not be empty
     *      5) Choices must have corresponding weights if weights are assigned
     */
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();

        if (!isValidDescriptionSize()) {
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_DESC_INVALID_SIZE);
        }

        if (numOfRubricChoices < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES);
        }

        if (this.numOfRubricSubQuestions < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
        }

        //Rubric choices are now allowed to be empty.
        /*
        for (String choice : this.rubricChoices) {
            if (choice.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_CHOICE);
                break;
            }
        }
        */

        for (String subQn : rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }

        if (hasAssignedWeights && rubricChoices.size() != rubricWeights.size()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);
        }

        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        return new ArrayList<String>();
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public int getNumOfRubricChoices() {
        return numOfRubricChoices;
    }

    public void setNumOfRubricChoices(int numOfRubricChoices) {
        this.numOfRubricChoices = numOfRubricChoices;
    }

    public List<String> getRubricChoices() {
        return rubricChoices;
    }

    public int getNumOfRubricSubQuestions() {
        return numOfRubricSubQuestions;
    }

    public void setNumOfRubricSubQuestions(int numOfRubricSubQuestions) {
        this.numOfRubricSubQuestions = numOfRubricSubQuestions;
    }

    public List<String> getRubricSubQuestions() {
        return rubricSubQuestions;
    }

}
