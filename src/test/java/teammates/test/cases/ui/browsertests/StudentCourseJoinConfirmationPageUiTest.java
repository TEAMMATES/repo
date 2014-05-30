package teammates.test.cases.ui.browsertests;

import java.lang.reflect.Constructor;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentCourseJoinConfirmationPage;
import teammates.test.pageobjects.StudentHomePage;

public class StudentCourseJoinConfirmationPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private static StudentCourseJoinConfirmationPage confirmationPage;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = getTypicalDataBundle();
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageUiTest.json");
        restoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }

    @Test
    public void testJoinConfirmation() throws Exception {
        
        ______TS("click join link then cancel");
        
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL
                + Const.ActionURIs.STUDENT_COURSE_JOIN;

        String joinLink = Url.addParamToUrl(
                joinActionUrl,
                Const.ParamsNames.REGKEY, "ThisIsAnInvalidKey");
        
        browser.driver.get(joinLink);
        confirmationPage =
                createCorrectLoginPageType(browser.driver.getPageSource())
                        .loginAsJoiningStudent(
                                TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                TestProperties.inst().TEST_STUDENT1_PASSWORD);
        confirmationPage.clickCancelButton();
        
        ______TS("click join link then confirm: fail: invalid key");
        
        browser.driver.get(joinLink);
        confirmationPage =
                createCorrectLoginPageType(browser.driver.getPageSource())
                        .loginAsJoiningStudent(
                                TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                TestProperties.inst().TEST_STUDENT1_PASSWORD);
        StudentHomePage studentHome = confirmationPage.clickConfirmButton();
        String expectedMsg = "You have used an invalid join link: /page/studentCourseJoin?regkey=ThisIsAnInvalidKey";
        studentHome.verifyStatus(expectedMsg);
        
        ______TS("click join link then confirm: success: valid key");

        String courseId = testData.courses.get("SCJConfirmationUiT.CS2104").id;
        String studentEmail = testData.students
                .get("alice.tmms@SCJConfirmationUiT.CS2104").email;

        joinLink = Url.addParamToUrl(
                joinActionUrl,
                Const.ParamsNames.REGKEY,
                BackDoor.getKeyForStudent(courseId, studentEmail));
        
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        studentHome = confirmationPage.clickConfirmButton();
        expectedMsg = "";
        studentHome.verifyStatus(expectedMsg);
        
        ______TS("already joined, no confirmation page");
                
        browser.driver.get(joinLink);
        studentHome = createNewPage(browser, StudentHomePage.class);
        expectedMsg = TestProperties.inst().TEST_STUDENT1_ACCOUNT + " has already joined this course";
        studentHome.verifyStatus(expectedMsg);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private LoginPage createCorrectLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :"    + pageSource);
        }
    }

    private <T extends AppPage> T createNewPage(Browser browser, Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(browser);
            PageFactory.initElements(browser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
