package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackMsqQuestionDetails}.
 */
public class FeedbackMsqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        assertEquals(FeedbackQuestionType.MSQ, msqDetails.getQuestionType());
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        msqDetails.setMsqChoices(Collections.singletonList("Choice 1"));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Collections.singletonList(1.22));
        msqDetails.setHasAssignedWeights(true);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Arrays.asList(1.22, 1.55));
        msqDetails.setHasAssignedWeights(true);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setHasAssignedWeights(true);
        msqDetails.setMsqWeights(Arrays.asList(1.22, -1.55));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Arrays.asList(1.22, 1.55));
        msqDetails.setOtherEnabled(true);
        msqDetails.setHasAssignedWeights(true);
        msqDetails.setMsqOtherWeight(-2);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_duplicateMsqOptions_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("choice 1", "choice 1"));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_DUPLICATE_MSQ_OPTION, errors.get(0));

        //duplicate cases that has trailing and leading spaces
        msqDetails.setMsqChoices(Arrays.asList("choice 1", " choice 1 "));
        errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_DUPLICATE_MSQ_OPTION, errors.get(0));

    }

    @Test
    public void testValidateQuestionDetails_maxSelectableChoicesMoreThanTotalNumberOfChoice_shouldReturnError() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("a", "b"));
        // 'other' is NOT one of the choices
        msqDetails.setOtherEnabled(false);
        msqDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        msqDetails.setHasAssignedWeights(false);
        msqDetails.setMsqOtherWeight(0);
        msqDetails.setMsqWeights(new ArrayList<>());
        msqDetails.setMaxSelectableChoices(3);
        msqDetails.setMinSelectableChoices(Integer.MIN_VALUE);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        AssertHelper.assertContains(Const.FeedbackQuestion.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_maxSelectableChoicesEqualTotalNumberOfChoice_shouldNotReturnError() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("a", "b"));
        // 'other' is one of the choices
        msqDetails.setOtherEnabled(true);
        msqDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        msqDetails.setHasAssignedWeights(false);
        msqDetails.setMsqOtherWeight(0);
        msqDetails.setMsqWeights(new ArrayList<>());
        msqDetails.setMaxSelectableChoices(3);
        msqDetails.setMinSelectableChoices(Integer.MIN_VALUE);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }
}
