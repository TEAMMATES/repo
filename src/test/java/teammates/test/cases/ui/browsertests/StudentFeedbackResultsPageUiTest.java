package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * Tests 'Feedback Results' view of students.
 * SUT: {@link StudentFeedbackResultsPage}.
 */
public class StudentFeedbackResultsPageUiTest extends BaseUiTestCase {

    private static DataBundle testData;
    private static Browser browser;
    private StudentFeedbackResultsPage resultsPage;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentFeedbackResultsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser(); 
    }
    
    @Test
    public void testAll() throws Exception {
        
        ______TS("no responses");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "Empty Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageEmpty.html");
            
        ______TS("standard session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "Open Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageOpen.html");
        
        ______TS("team-to-team session results");
        // TODO: change all but one verifyHtml to verifyHtmlMainContent
        
        resultsPage = loginToStudentFeedbackResultsPage("Benny", "Open Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageTeamToTeam.html");
        
        ______TS("MCQ session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "MCQ Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageMCQ.html");
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(4,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(4,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(5,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(5,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        
        ______TS("MSQ session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "MSQ Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageMSQ.html");
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(4,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(4,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(5,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(5,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
        
        ______TS("NUMSCALE session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "NUMSCALE Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageNUMSCALE.html");
        
        ______TS("CONSTSUM session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "CONSTSUM Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageCONSTSUM.html");
        

        ______TS("CONTRIB session results");
        
        resultsPage = loginToStudentFeedbackResultsPage("Alice", "CONTRIB Session");
        resultsPage.verifyHtml("/studentFeedbackResultsPageCONTRIB.html");
        
        ______TS("unreg student");
        // should automatically logout.
        // Open Session
        StudentAttributes unreg = testData.students.get("DropOut");
        resultsPage = loginToStudentFeedbackResultsPage(unreg, "Open Session");
        resultsPage.verifyHtml("/unregisteredStudentFeedbackResultsPageOpen.html");
        
        // Mcq Session
        resultsPage = loginToStudentFeedbackResultsPage(unreg, "MCQ Session");
        resultsPage.verifyHtml("/unregisteredStudentFeedbackResultsPageMCQ.html");
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
        closeBrowser(browser);
    }
    
    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(
            String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl,
                StudentFeedbackResultsPage.class);
    }
    
    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(StudentAttributes s, String fsDataId) {
            
        String submitUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withCourseId(s.course)
                .withStudentEmail(s.email)
                .withSessionName(testData.feedbackSessions.get(fsDataId).feedbackSessionName)
                .withRegistrationKey(BackDoor.getKeyForStudent(s.course, s.email))
                .toString();
        browser.driver.get(submitUrl);
        return AppPage.getNewPageInstance(browser, StudentFeedbackResultsPage.class);
    }

}