package teammates.test.cases.ui.browsertests;

import java.net.MalformedURLException;

import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.ThreadHelper;
import teammates.test.cases.common.FieldValidatorTest;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.InstructorHelpPage;
import teammates.test.pageobjects.InstructorHomePage;

/**
 * Tests Home page and login page for instructors.
 * SUT: {@link InstructorHomePage}.<br>
 * Uses a real account.
 * 
 */
public class InstructorHomePageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private static InstructorHomePage homePage;
    
    private static FeedbackSessionAttributes feedbackSessionAwaiting;
    private static FeedbackSessionAttributes feedbackSessionOpen;
    private static FeedbackSessionAttributes feedbackSessionClosed;
    private static FeedbackSessionAttributes feedbackSessionPublished;

    // TODO: refactor this test. try to use admin login or create instructors and courses not using json
    
    @BeforeClass
    public static void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorHomePageUiTest1.json");
        removeTestDataOnServer(loadDataBundle("/InstructorHomePageUiTest3.json"));
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    private static void loadFinalHomePageTestData() {
        
        testData = loadDataBundle("/InstructorHomePageUiTest3.json");
        removeAndRestoreTestDataOnServer(testData);
        
        feedbackSessionAwaiting = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSessionOpen = testData.feedbackSessions.get("First Feedback Session");
        feedbackSessionClosed = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSessionPublished = testData.feedbackSessions.get("Fourth Feedback Session");
    }
    
    @Test
    public void allTests() throws Exception {
        testPersistenceCheck();
        testLogin();
        testContent();
        testAjaxCourseTableLoad();
        testShowFeedbackStatsLink();
        testHelpLink();
        testCourseLinks();
        testSearchAction();
        testSortAction();
        testRemindActions();
        testPublishUnpublishActions();
        testArchiveCourseAction();
        testCopyToFsAction();
        testDeleteCourseAction();
    }
    
    private void testAjaxCourseTableLoad() throws Exception {
        DataBundle unloadedCourseTestData = loadDataBundle("/InstructorHomePageUiTestUnloadedCourse.json");
        removeAndRestoreTestDataOnServer(unloadedCourseTestData);
        loginAsInstructor("CHomeUiT.instructor.tmms.unloaded");
        
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLWithUnloadedCourse.html");
        
        loginAsCommonInstructor();
        removeTestDataOnServer(unloadedCourseTestData);
    }

    private void testPersistenceCheck() throws Exception {
        
        ______TS("persistence check");
        
        loginWithPersistenceProblem();

        // This is the full HTML verification for Instructor Home Page, the rest can all be verifyMainHtml
        homePage.verifyHtml("/InstructorHomeHTMLPersistenceCheck.html");
    }

    public void testLogin() {
        
        ______TS("login");
        
        loginAsCommonInstructor();
        assertTrue(browser.driver.getCurrentUrl().contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
    }

    private void testShowFeedbackStatsLink() throws Exception {
        WebElement viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        
        String currentValidUrl = viewResponseLink.getAttribute("href");
        
        ______TS("test case: fail, fetch response rate of invalid url");
        homePage.setViewResponseLinkValue(viewResponseLink, "/invalid/url");
        viewResponseLink.click();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLResponseRateFail.html");
        
        ______TS("test case: fail to fetch response rate again, check consistency of fail message");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        viewResponseLink.click();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLResponseRateFail.html");
        
        ______TS("test case: pass with valid url after multiple fails");
        viewResponseLink = homePage.getViewResponseLink("CHomeUiT.CS2104", "Fourth Feedback Session");
        homePage.setViewResponseLinkValue(viewResponseLink, currentValidUrl);
        viewResponseLink.click();
        homePage.verifyHtmlMainContent("/instructorHomeHTMLResponseRatePass.html");
    }
    
    public void testContent() throws Exception {
        
        ______TS("content: no courses");
        
        //this case is implicitly tested when testing for 'delete course' action and
        //new instructor without sample course
        //loginAsInstructor(testData.accounts.get("newInstructorWithSampleCourse").email);
        ______TS("content: new instructor, with status message HINT_FOR_NEW_INSTRUCTOR");
        
        //already logged in
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeNewInstructorWithoutSampleCourse.html");
        
        testData = loadDataBundle("/InstructorHomePageUiTest2.json");
        removeAndRestoreTestDataOnServer(testData);
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeNewInstructorWithSampleCourse.html");
        
        ______TS("content: multiple courses");
        
        loadFinalHomePageTestData();
        homePage.loadInstructorHomeTab();
        // Should not see private session
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLWithHelperView.html");
        updateInstructorToCoownerPrivileges();
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeHTML.html");
    }

    private void updateInstructorToCoownerPrivileges() {
        // update current instructor for CS1101 to have Co-owner privileges
        InstructorAttributes instructor = testData.instructors.get("CHomeUiT.instr.CS1101");
        BackDoor.deleteInstructor(instructor.courseId, instructor.email);
        instructor.privileges.setDefaultPrivilegesForCoowner();
        BackDoor.createInstructor(instructor);
    }
    
    public void testHelpLink() {
        
        ______TS("link: help page");
        
        InstructorHelpPage helpPage = homePage.loadInstructorHelpTab();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();
        
    }
    
    public void testCourseLinks() {
        String courseId = testData.courses.get("CHomeUiT.CS1101").getId();
        String instructorId = testData.accounts.get("account").googleId;
        
        ______TS("link: course enroll");
        InstructorCourseEnrollPage enrollPage = homePage.clickCourseErollLink(courseId);
        enrollPage.verifyContains("Enroll Students for CHomeUiT.CS1101");
        String expectedEnrollLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedEnrollLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course view");
        InstructorCourseDetailsPage detailsPage = homePage.clickCourseViewLink(courseId);
        detailsPage.verifyContains("Course Details");
        String expectedViewLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedViewLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course edit");
        InstructorCourseEditPage editPage = homePage.clickCourseEditLink(courseId);
        editPage.verifyContains("Edit Course Details");
        String expectedEditLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                                        .withCourseId(courseId)
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        assertEquals(expectedEditLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("link: course add session");
        InstructorFeedbacksPage feedbacksPage = homePage.clickCourseAddEvaluationLink(courseId);
        feedbacksPage.verifyContains("Add New Feedback Session");
        String expectedAddSessionLinkText = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                        .withUserId(instructorId)
                                        .withCourseId(courseId)
                                        .toAbsoluteString();
        assertEquals(expectedAddSessionLinkText, browser.driver.getCurrentUrl());
        homePage.goToPreviousPage(InstructorHomePage.class);
        
    }

    public void testRemindActions() {
        
        ______TS("remind action: AWAITING feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionAwaiting.getCourseId(),
                                                          feedbackSessionAwaiting.getFeedbackSessionName()));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionAwaiting.getCourseId(),
                                                                 feedbackSessionAwaiting.getFeedbackSessionName()));
        
        ______TS("remind action: OPEN feedback session - outer button");
        
        homePage.clickAndCancel(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                                                       feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickAndConfirm(homePage.getRemindLink(feedbackSessionOpen.getCourseId(),
                                                        feedbackSessionOpen.getFeedbackSessionName()));
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Feedbacks' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: OPEN feedback session - inner button");
        
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickAndCancel(homePage.getRemindInnerLink(feedbackSessionOpen.getCourseId(),
                                                            feedbackSessionOpen.getFeedbackSessionName()));
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickAndConfirm(homePage.getRemindInnerLink(feedbackSessionOpen.getCourseId(),
                                                             feedbackSessionOpen.getFeedbackSessionName()));
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        
        //go back to previous page because 'send reminder' redirects to the 'Feedbacks' page.
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind particular users action: OPEN feedback session");
        
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.cancelRemindParticularUsersForm();
        
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.submitRemindParticularUsersForm();
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT);
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        homePage.clickRemindOptionsLink(feedbackSessionOpen.getCourseId(), feedbackSessionOpen.getFeedbackSessionName());
        homePage.clickRemindParticularUsersLink(feedbackSessionOpen.getCourseId(),
                                                feedbackSessionOpen.getFeedbackSessionName());
        homePage.fillRemindParticularUsersForm();
        homePage.submitRemindParticularUsersForm();
        ThreadHelper.waitFor(1000);
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT);
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("remind action: CLOSED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionClosed.getCourseId(),
                                                          feedbackSessionClosed.getFeedbackSessionName()));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionClosed.getCourseId(),
                                                                 feedbackSessionClosed.getFeedbackSessionName()));
        
        ______TS("remind action: PUBLISHED feedback session");
        
        homePage.verifyUnclickable(homePage.getRemindLink(feedbackSessionPublished.getCourseId(),
                                                          feedbackSessionPublished.getFeedbackSessionName()));
        homePage.verifyUnclickable(homePage.getRemindOptionsLink(feedbackSessionPublished.getCourseId(),
                                                                 feedbackSessionPublished.getFeedbackSessionName()));

    }

    public void testPublishUnpublishActions() {
        ______TS("publish action: AWAITING feedback session");
        
        homePage.verifyUnclickable(homePage.getPublishLink(feedbackSessionAwaiting.getCourseId(),
                                                           feedbackSessionAwaiting.getFeedbackSessionName()));

        ______TS("publish action: OPEN feedback session");
        
        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionOpen.getCourseId(),
                                                        feedbackSessionOpen.getFeedbackSessionName()));

        ______TS("publish action: CLOSED feedback session");
        
        homePage.clickAndCancel(homePage.getPublishLink(feedbackSessionClosed.getCourseId(),
                                                        feedbackSessionClosed.getFeedbackSessionName()));
        
        ______TS("unpublish action: PUBLISHED feedback session");
        homePage.clickFeedbackSessionUnpublishLink(feedbackSessionPublished.getCourseId(),
                                                   feedbackSessionPublished.getFeedbackSessionName());
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
        assertFalse(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
                                                feedbackSessionPublished.getFeedbackSessionName()).isPublished());

        ______TS("publish action: PUBLISHED feedback session");
        homePage.clickFeedbackSessionPublishLink(feedbackSessionPublished.getCourseId(),
                                                 feedbackSessionPublished.getFeedbackSessionName());
        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED);
        assertTrue(BackDoor.getFeedbackSession(feedbackSessionPublished.getCourseId(),
                                               feedbackSessionPublished.getFeedbackSessionName()).isPublished());
    }
    
    public void testArchiveCourseAction() throws Exception {
        String courseIdForCS1101 = testData.courses.get("CHomeUiT.CS1101").getId();

        ______TS("archive course action: click and cancel");
        
        homePage.clickArchiveCourseLinkAndCancel(courseIdForCS1101);

        InstructorAttributes instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        InstructorAttributes helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);

        // Both will be false before it is archived for testing
        assertFalse(instructor.isArchived);
        assertFalse(helper.isArchived);

        assertFalse(BackDoor.getCourse(courseIdForCS1101).isArchived);
        
        ______TS("archive course action: click and confirm");
        
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        
        // archiving should only modify the isArchived status on the instructor
        instructor = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms", courseIdForCS1101);
        helper = BackDoor.getInstructorByGoogleId("CHomeUiT.instructor.tmms.helper", courseIdForCS1101);
        assertTrue(instructor.isArchived);
        assertTrue(helper.isArchived == null || !helper.isArchived);
        
        // the course's isArchived status should not be modified
        assertFalse(BackDoor.getCourse(courseIdForCS1101).isArchived);
        
        homePage.verifyHtmlMainContent("/instructorHomeCourseArchiveSuccessful.html");
        
        ______TS("archive action failed");
        
        String courseIdForCS2104 = testData.courses.get("CHomeUiT.CS2104").getId();
        
        //delete the course, then submit archive request to it
        String archiveLinkString = homePage.getArchiveCourseLink(courseIdForCS2104);
        AppUrl urlToArchive;
        try {
            // the link returned here might be absolute; make it relative first
            urlToArchive = createUrl(AppUrl.getRelativePath(archiveLinkString));
        } catch (MalformedURLException e) {
            // the link is already relative
            urlToArchive = createUrl(archiveLinkString);
        }
        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseIdForCS2104));
        browser.driver.get(urlToArchive.toAbsoluteString());
        assertTrue(browser.driver.getCurrentUrl().endsWith(Const.ViewURIs.UNAUTHORIZED));
        
        //restore
        testData = loadDataBundle("/InstructorHomePageUiTest3.json");
        removeAndRestoreTestDataOnServer(testData);
        loginAsCommonInstructor();
        homePage.clickArchiveCourseLinkAndConfirm(courseIdForCS1101);
        homePage.loadInstructorHomeTab();
    }
    
    public void testCopyToFsAction() {
        String feedbackSessionName = "First Feedback Session";
        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();
        
        ______TS("Submit empty course list: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().clickSubmitButton();
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        homePage.getFsCopyModal().verifyStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        
        homePage.getFsCopyModal().clickCloseButton();
        
        ______TS("Copying fails due to fs with same name in course selected: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected(feedbackSessionName);
        
        homePage.getFsCopyModal().clickSubmitButton();
        
        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                     feedbackSessionName, courseId);
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        homePage.getFsCopyModal().verifyStatusMessage(error);
        
        homePage.getFsCopyModal().clickCloseButton();
        
        ______TS("Copying fails due to fs with invalid name: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        String invalidFeedbackSessionName = "Invalid name | for feedback session";
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected(invalidFeedbackSessionName);
        
        homePage.getFsCopyModal().clickSubmitButton();
        homePage.getFsCopyModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(homePage.getFsCopyModal().isFormSubmissionStatusMessageVisible());
        
        homePage.getFsCopyModal().verifyStatusMessage(
                FieldValidatorTest.getInterpolatedErrorMessage(
                    FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidFeedbackSessionName,
                    FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR));
        homePage.getFsCopyModal().clickCloseButton();
        
        ______TS("Successful case: Home Page");
        
        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        homePage.getFsCopyModal().waitForModalToLoad();
        homePage.getFsCopyModal().fillFormWithAllCoursesSelected("New name!");
        
        homePage.getFsCopyModal().clickSubmitButton();

        homePage.waitForPageToLoad();
        homePage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        
        homePage.goToPreviousPage(InstructorHomePage.class);
        
        ______TS("Failure case: Ajax error");
        
        // Change action link so that ajax will fail
        homePage.changeFsCopyButtonActionLink(courseId, feedbackSessionName, "/page/nonExistentPage?");

        homePage.clickFsCopyButton(courseId, feedbackSessionName);
        // Wait for modal to appear and show error.
        homePage.getFsCopyModal().waitForModalLoadingError();

    }

    public void testDeleteCourseAction() throws Exception {
        
        ______TS("delete course action");
        
        String courseId = testData.courses.get("CHomeUiT.CS2104").getId();
        homePage.clickAndCancel(homePage.getDeleteCourseLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));
        
        homePage.clickAndConfirm(homePage.getDeleteCourseLink(courseId));
        assertTrue(BackDoor.isCourseNonExistent(courseId));
        homePage.verifyHtmlMainContent("/instructorHomeCourseDeleteSuccessful.html");
        
        //delete the other course as well
        courseId = testData.courses.get("CHomeUiT.CS1101").getId();
        BackDoor.deleteCourse(courseId);
        
        homePage.loadInstructorHomeTab();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLEmpty.html");
        
    }
    
    public void testSearchAction() {
        // Tested in student list page
    }
    
    public void testSortAction() throws Exception {
        ______TS("sort courses by id");
        homePage.clickSortByIdButton();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortById.html");

        ______TS("sort courses by name");
        homePage.clickSortByNameButton();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortByName.html");

        ______TS("sort courses by date");
        homePage.clickSortByDateButton();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortByDate.html");

        ______TS("sort sessions by session name");
        homePage.sortTablesByName();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortSessionsByName.html");

        ______TS("sort sessions by session start date");
        homePage.sortTablesByStartDate();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortSessionsByStartDate.html");

        ______TS("sort sessions by session end date");
        homePage.sortTablesByEndDate();
        homePage.verifyHtmlMainContent("/InstructorHomeHTMLSortSessionsByEndDate.html");
    }
    
    private void loginAsCommonInstructor() {
        String commonInstructor = "CHomeUiT.instructor.tmms";
        loginAsInstructor(commonInstructor);
    }
    
    private void loginAsInstructor(String googleId) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withUserId(googleId);
        
        homePage = loginAdminToPage(browser, editUrl, InstructorHomePage.class);
    }

    private void loginWithPersistenceProblem() {
        AppUrl homeUrl = ((AppUrl) createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                    .withParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "something"))
                    .withUserId("unreg_user");
        
        homePage = loginAdminToPage(browser, homeUrl, InstructorHomePage.class);
        
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
