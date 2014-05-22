package teammates.common.datatransfer;

import java.util.Arrays;

import teammates.common.util.Assumption;

/** A class holding the details for the response of a specific question type.
 * This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the 
 * question type.
 */
public abstract class FeedbackAbstractResponseDetails {
    public FeedbackQuestionType questionType;
    
    public FeedbackAbstractResponseDetails(FeedbackQuestionType questionType){
        this.questionType = questionType;
    }
    
    public abstract String getAnswerString();
    
    public abstract String getAnswerHtml();
    
    public abstract String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails);
    
    public static FeedbackAbstractResponseDetails createResponseDetails(
            String[] answer, FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails) {
        
        FeedbackAbstractResponseDetails responseDetails = null;
        
        switch(questionType) {
        case TEXT:
            //For essay questions the response is saved as plain-text due to legacy format before there were multiple question types
            responseDetails = new FeedbackTextResponseDetails(answer[0]);
            break;
        case MCQ:
            //TODO check whether other is chosen and construct accordingly when implementing other field
            responseDetails = new FeedbackMcqResponseDetails(answer[0], false);
            break;
        case MSQ:
            responseDetails = new FeedbackMsqResponseDetails(Arrays.asList(answer));
            break;
        case NUMSCALE:
            FeedbackNumericalScaleQuestionDetails numericalScaleQuestionDetails= (FeedbackNumericalScaleQuestionDetails) questionDetails;
            int minScale = numericalScaleQuestionDetails.minScale;
            int maxScale = numericalScaleQuestionDetails.maxScale;
            
            try {
                double numscaleAnswer = Double.parseDouble(answer[0]);
                if (numscaleAnswer < minScale) {
                    numscaleAnswer = minScale;
                } else if (numscaleAnswer > maxScale) {
                    numscaleAnswer = maxScale;
                }
                
                responseDetails = new FeedbackNumericalScaleResponseDetails(numscaleAnswer);
            } catch (NumberFormatException e) {
                responseDetails = null;
            }
            break;
        default:
            Assumption.fail("Question type not supported");
            break;
        }
        
        return responseDetails;
    }
}
