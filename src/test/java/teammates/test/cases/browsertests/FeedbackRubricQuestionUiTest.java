package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

public class FeedbackRubricQuestionUiTest extends FeedbackQuestionUiTest {
    private static InstructorFeedbackEditPage feedbackEditPage;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRubricQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }
    
    @BeforeClass
    protected void classSetup() {
        feedbackEditPage = getFeedbackEditPage();
    }

    @Test
    public void allTests() throws Exception {
        testEditPage();
        testInstructorSubmitPage();
        testStudentSubmitPage();
        testStudentResultsPage();
        testInstructorResultsPage();
    }

    private void testStudentResultsPage() throws Exception {
        ______TS("test rubric question simple student results page");

        StudentFeedbackResultsPage simpleResultsPage =
                                        loginToStudentFeedbackResultsPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        simpleResultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageRubric.html");

        ______TS("test rubric question extended student results page");

        StudentFeedbackResultsPage extendedResultsPage =
                loginToStudentFeedbackResultsPage("alice.tmms@FRubricQnUiT.CS2104", "openSession4");
        extendedResultsPage.verifyHtmlMainContent("/studentExtendedFeedbackResultsPageRubric.html");

    }
    
    private void testInstructorResultsPage() throws Exception {
        ______TS("test rubric question instructor results page");

        // Question view
        InstructorFeedbackResultsPage instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2",
                                                                 false, "question");
        instructorResultsPage.waitForPanelsToExpand();
        
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricQuestionView.html");

        // Giver Recipient Question View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "giver-recipient-question");
        instructorResultsPage.waitForPanelsToExpand();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGRQView.html");
        
        // Giver Question Recipient View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "giver-question-recipient");
        instructorResultsPage.waitForPanelsToExpand();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGQRView.html");
        
        // Recipient Question Giver View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "recipient-question-giver");
        instructorResultsPage.waitForPanelsToExpand();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRQGView.html");
        
        // Recipient Giver Question View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "recipient-giver-question");
        instructorResultsPage.waitForPanelsToExpand();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRGQView.html");
        
    }
    
    private void testInstructorSubmitPage() {
        
        ______TS("test rubric question input disabled for closed session");
        
        FeedbackSubmitPage submitPage = loginToInstructorFeedbackSubmitPage("teammates.test.instructor", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                     + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));

        ______TS("test rubric question submission");
        // Done in testStudentSubmitPage
        
    }

    private void testStudentSubmitPage() throws Exception {
        
        ______TS("test rubric question input disabled for closed session");
        
        FeedbackSubmitPage submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                     + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));

        ______TS("test rubric question submission");
        
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        assertTrue(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                    + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));
        
        // Select table cell

        submitPage.clickRubricRadio(1, 0, 0, 1);

        submitPage.clickRubricRadio(1, 1, 0, 1);
        submitPage.clickRubricRadio(1, 1, 1, 0);

        // Submit
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        
        // Go back to submission page and verify html
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        submitPage.waitForCellHoverToDisappear();
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageRubricSuccess.html");

        // Submit another feedback response using another student's account
        submitPage = loginToStudentFeedbackSubmitPage("benny.tmms@FRubricQnUiT.CS2104", "openSession2");

        submitPage.clickRubricRadio(1, 0, 0, 0);
        submitPage.clickRubricRadio(1, 0, 1, 1);

        submitPage.clickRubricRadio(1, 1, 0, 0);
        submitPage.clickRubricRadio(1, 1, 1, 0);

        submitPage.clickSubmitButton();

        submitPage = loginToStudentFeedbackSubmitPage("colin.tmms@FRubricQnUiT.CS2104", "openSession2");

        submitPage.clickRubricRadio(1, 0, 0, 1);
        submitPage.clickRubricRadio(1, 0, 1, 0);

        submitPage.clickSubmitButton();
    }

    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testInputJsValidationForRubricQuestion();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("RUBRIC: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RUBRIC");
        assertTrue(feedbackEditPage.verifyNewRubricQuestionFormIsDisplayed());
    }
    
    @Override
    public void testInputValidation() {
        
        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        ______TS("empty weight test");

        feedbackEditPage.fillNewQuestionBox("empty weight test");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.clickAssignWeightsCheckbox(-1);
        feedbackEditPage.fillRubricWeightBox("", -1, 3);
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.verifyStatus(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);
    }
    
    @Override
    public void testCustomizeOptions() {
        
        // TODO somebody do this?
        
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("RUBRIC: add question action success");
        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RUBRIC");
        feedbackEditPage.fillNewQuestionBox("RUBRIC qn");
        feedbackEditPage.fillNewQuestionDescription("more details");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("RUBRIC: edit question success");
        
        // Click edit button
        feedbackEditPage.clickEditQuestionButton(1);
        
        // Check that fields are editable
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEdit.html");
        
        feedbackEditPage.fillEditQuestionBox("edited RUBRIC qn text", 1);
        feedbackEditPage.fillEditQuestionDescription("more details", 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        
        // Check question text is updated
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSuccess.html");
        
        ______TS("RUBRIC: edit sub-questions success");
        feedbackEditPage.clickEditQuestionButton(1);
        
        // Edit sub-question for row 1
        feedbackEditPage.fillRubricSubQuestionBox("New(0) sub-question text", 1, 0);
        
        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(1) sub-question text", 1, 2);
        
        // Remove existing sub-questions
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
 
        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(2) sub-question text", 1, 3);
        
        // Remove new sub-question
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 2);
        
        // Should end up with 1 question
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSubQuestionSuccess.html");

        ______TS("RUBRIC: edit choices success");
        feedbackEditPage.clickEditQuestionButton(1);
        
        // Edit choice for col 1
        feedbackEditPage.fillRubricChoiceBox("New(0) choice", 1, 0);
        
        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(1) choice", 1, 4);
        
        // Remove existing choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);
 
        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(2) choice", 1, 5);
        
        // Remove new choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 4);
        
        // Should end up with 4 choices, including (1) and (2)
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditChoiceSuccess.html");

        ______TS("RUBRIC: edit weight success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Edit the weight of the first choice
        feedbackEditPage.clickAssignWeightsCheckbox(1);
        feedbackEditPage.fillRubricWeightBox("2.25", 1, 0);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditWeightSuccess.html");

        ______TS("RUBRIC: edit descriptions success");
        feedbackEditPage.clickEditQuestionButton(1);
        
        // Edit description for 0-0
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);
        
        // Edit description for a new row, to test if the js generated html works.
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 1);
       
        feedbackEditPage.fillRubricDescriptionBox("New(1) description", 1, 1, 0);
        
        // Should end up with 2 rubric descriptions, (0) and (1)
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditDescriptionSuccess.html");
    }
    
    @Override
    public void testDeleteQuestionAction() {
        ______TS("RUBRIC: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("RUBRIC: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    private void testInputJsValidationForRubricQuestion() {
        // this tests whether the JS validation disallows empty rubric options
        
        ______TS("JS validation test");

        // add a new question
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RUBRIC");
        
        // start editing it
        feedbackEditPage.fillNewQuestionBox("RUBRIC qn JS validation test");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.clickAddQuestionButton();
        
        feedbackEditPage.clickEditQuestionButton(1);
        
        // try to remove everything
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 3);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 2);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);
        
        // TODO check if the rubric column and link is indeed empty
        
        // add something so that we know that the elements are still there
        // and so that we don't get empty sub question error
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 0);
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        
        feedbackEditPage.verifyStatus("Too little choices for Rubric question. Minimum number of options is: 2");
    }
    
    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                .withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }
    
    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
            String instructorName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, FeedbackSubmitPage.class);
    }
    
    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, FeedbackSubmitPage.class);
    }
    
    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, StudentFeedbackResultsPage.class);
    }
    
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                    .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        
        if (needAjax) {
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }
        
        if (viewType != null) {
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }
        
        return loginAdminToPage(editUrl, InstructorFeedbackResultsPage.class);
    }

}
