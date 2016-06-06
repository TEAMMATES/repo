package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestionFormTemplates;
import teammates.common.util.Templates.FeedbackQuestionDetails.Slots;
import teammates.ui.controller.PageData;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackRankOptionsQuestionDetails extends FeedbackRankQuestionDetails {
    public static final transient int MIN_NUM_OF_OPTIONS = 2;
    public static final transient String ERROR_NOT_ENOUGH_OPTIONS =
            "Too little options for " + Const.FeedbackQuestionTypeNames.RANK_OPTION
            + ". Minimum number of options is: ";
    
    List<String> options;
    
    public FeedbackRankOptionsQuestionDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
        
        this.options = new ArrayList<String>();
    }

    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        super.extractQuestionDetails(requestParameters, questionType);
        List<String> options = new ArrayList<>();
      
        String numOptionsCreatedString =
                HttpRequestHelper.getValueFromParamMap(
                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
        Assumption.assertNotNull("Null number of choice for Rank", numOptionsCreatedString);
        int numOptionsCreated = Integer.parseInt(numOptionsCreatedString);
        
        for (int i = 0; i < numOptionsCreated; i++) {
            String rankOption = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" + i);
            if (rankOption != null && !rankOption.trim().isEmpty()) {
                options.add(rankOption);
            }
        }
        
        this.initialiseQuestionDetails(options);
        
        return true;
    }

    private void initialiseQuestionDetails(List<String> options) {
        this.options = options;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RANK_OPTION;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails) {
        
        FeedbackRankOptionsResponseDetails existingResponse = (FeedbackRankOptionsResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
     
        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            "${optionIdx}", Integer.toString(i),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                    Slots.RANK_OPTION_VISIBILITY, "",
                            "${options}", getSubmissionOptionsHtmlForRankingOptions(existingResponse.getAnswerList().get(i)),
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.RANK_OPTION_VALUE,  Sanitizer.sanitizeForHtml(options.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
            
        }
        
        return Templates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.RANK_OPTION_VISIBILITY, "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                "${rankToRecipientsValue}", "false",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                "${rankNumOptionValue}", Integer.toString(options.size()),
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}",
                        Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                "${areDuplicatesAllowedValue}", Boolean.toString(areDuplicatesAllowed)
                );
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            "${optionIdx}", Integer.toString(i),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                    Slots.RANK_OPTION_VISIBILITY, "",
                            "${options}", getSubmissionOptionsHtmlForRankingOptions(Const.INT_UNINITIALIZED),
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.RANK_OPTION_VALUE,  Sanitizer.sanitizeForHtml(options.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        return Templates.populateTemplate(
                            FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                            "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.RANK_OPTION_VISIBILITY, "",
                            "${rankToRecipientsValue}", "false",
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                            "${rankNumOptionValue}", Integer.toString(options.size()),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                            "${areDuplicatesAllowedValue}", Boolean.toString(areDuplicatesAllowed)
                            );
    }
    
    private String getSubmissionOptionsHtmlForRankingOptions(int rankGiven) {
        StringBuilder result = new StringBuilder(100);
     
        ElementTag option = PageData.createOption("", "", rankGiven == Const.INT_UNINITIALIZED);
        result.append("<option"
                     + option.getAttributesToString() + ">"
                     + option.getContent()
                     + "</option>");
        for (int i = 1; i <= options.size(); i++) {
            option = PageData.createOption(String.valueOf(i), String.valueOf(i), rankGiven == i);
            result.append("<option"
                        + option.getAttributesToString() + ">"
                        + option.getContent()
                        + "</option>");
        }
       
        return result.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_EDIT_FORM_OPTIONFRAGMENT;
        
        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            Slots.RANK_OPTION_VALUE,  Sanitizer.sanitizeForHtml(options.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        return Templates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_EDIT_OPTIONS_FORM,
                "${rankEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfRankOptions}", String.valueOf(options.size()),
                "${optionRecipientDisplayName}", "option",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}",
                        Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                "${areDuplicatesAllowedChecked}", areDuplicatesAllowed ? "checked" : "");
    
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        this.options.add("");
        this.options.add("");

        return "<div id=\"rankOptionsForm\">"
              + this.getQuestionSpecificEditFormHtml(-1)
              + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(100);
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";
        
        optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
        for (String option : options) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${msqChoiceValue}", option);
            
            optionListHtml.append(optionFragment);
        }
        
        optionListHtml.append("</ul>");
        additionalInfo = Templates.populateTemplate(
            FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
            "${questionTypeName}", this.getQuestionTypeDisplayName(),
            "${msqAdditionalInfoFragments}", optionListHtml.toString());

        return Templates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
    }

    @Override
    public String getQuestionResultStatisticsHtml(
                        List<FeedbackResponseAttributes> responses,
                        FeedbackQuestionAttributes question,
                        String studentEmail,
                        FeedbackSessionResultsBundle bundle,
                        String view) {
        
        if ("student".equals(view) || responses.isEmpty()) {
            return "";
        }
        
        StringBuilder fragments = new StringBuilder(100);
        
        Map<String, List<Integer>> optionRanks = generateOptionRanksMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, List<Integer>> entry : optionRanks.entrySet()) {
            
            List<Integer> ranks = entry.getValue();
            double average = computeAverage(ranks);
            String ranksReceived = getListOfRanksReceivedAsString(ranks);

            String option = entry.getKey();
            
            fragments.append(Templates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_STATS_OPTIONFRAGMENT,
                    Slots.RANK_OPTION_VALUE,  Sanitizer.sanitizeForHtml(option),
                                                                        "${ranksReceived}", ranksReceived,
                                                                        "${averageRank}", df.format(average)));
        
        }
 
        return Templates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_OPTION_STATS,
                Slots.OPTION_RECIPIENT_DISPLAY_NAME, "Option",
                                                             "${fragments}", fragments.toString());
    }

    @Override
    public String getQuestionResultStatisticsCsv(
                        List<FeedbackResponseAttributes> responses,
                        FeedbackQuestionAttributes question,
                        FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }
        
        StringBuilder fragments = new StringBuilder();
        Map<String, List<Integer>> optionRanks = generateOptionRanksMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, List<Integer>> entry : optionRanks.entrySet()) {
            String option = Sanitizer.sanitizeForCsv(entry.getKey());
          
            List<Integer> ranksAssigned = entry.getValue();
            double average = computeAverage(ranksAssigned);
            String fragment = option + "," + df.format(average) + Const.EOL;
            fragments.append(fragment);
        }

        return "Option, Average Rank" + Const.EOL + fragments.toString() + Const.EOL;
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of
     * ranks received for that option.
     * The key of the map returned is the option name.
     * The values of the map are list of ranks received by the key.
     * @param responses  a list of responses
     */
    private Map<String, List<Integer>> generateOptionRanksMapping(
                                            List<FeedbackResponseAttributes> responses) {
        Map<String, List<Integer>> optionRanks = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails) response.getResponseDetails();
            
            List<Integer> answers = frd.getAnswerList();
            Map<String, Integer> mapOfOptionToRank = new HashMap<>();
            
            Assumption.assertEquals(answers.size(), options.size());

            for (int i = 0; i < options.size(); i++) {
                int rankReceived = answers.get(i);
                mapOfOptionToRank.put(options.get(i), rankReceived);
            }
            
            Map<String, Integer> normalisedRankForOption =
                    obtainMappingToNormalisedRanksForRanking(mapOfOptionToRank, options);

            for (int i = 0; i < options.size(); i++) {
                String optionReceivingRanks = options.get(i);
                int rankReceived = normalisedRankForOption.get(optionReceivingRanks);
                
                if (rankReceived != Const.POINTS_NOT_SUBMITTED) {
                    updateOptionRanksMapping(optionRanks, optionReceivingRanks, rankReceived);
                }
            }
        }
        return optionRanks;
    }

    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRankOptionsQuestionDetails newRankQuestionDetails = (FeedbackRankOptionsQuestionDetails) newDetails;

        return this.options.size() != newRankQuestionDetails.options.size()
            || !this.options.containsAll(newRankQuestionDetails.options)
            || !newRankQuestionDetails.options.containsAll(this.options);
    }

    @Override
    public String getCsvHeader() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.options.size(); i++) {
            result.append(String.format("Rank %d,", i + 1));
        }
        result.deleteCharAt(result.length() - 1); // remove the last comma
        
        return result.toString();
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"" + FeedbackQuestionType.RANK_OPTIONS.name() + "\">"
                 + "<a>" + Const.FeedbackQuestionTypeNames.RANK_OPTION + "</a>"
             + "</li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (options.size() < MIN_NUM_OF_OPTIONS) {
            errors.add(ERROR_NOT_ENOUGH_OPTIONS + MIN_NUM_OF_OPTIONS + ".");
        }
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        if (responses.isEmpty()) {
            return new ArrayList<String>();
        }
        
        if (areDuplicatesAllowed) {
            return new ArrayList<String>();
        }
        List<String> errors = new ArrayList<>();
        
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails) response.getResponseDetails();
            Set<Integer> responseRank = new HashSet<>();
            
            for (int answer : frd.getFilteredSortedAnswerList()) {
                if (responseRank.contains(answer)) {
                    errors.add("Duplicate rank " + answer);
                }
                responseRank.add(answer);
            }
        }
    
        return errors;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

}
