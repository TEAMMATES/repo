package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackRubricQuestionDetails}.
 */
public class FeedbackRubricQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();

        assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        assertFalse(rubricDetails.hasAssignedWeights());
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testValidateQuestionDetails_invalidWeightListSize_errorReturned() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setNumOfRubricChoices(2);
        rubricDetails.setNumOfRubricSubQuestions(2);
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Collections.singletonList(1.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_validWeightListSize_errorListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setNumOfRubricChoices(2);
        rubricDetails.setNumOfRubricSubQuestions(2);
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Arrays.asList(1.0, 2.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }
}
