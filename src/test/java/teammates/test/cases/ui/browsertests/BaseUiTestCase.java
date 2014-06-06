package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;

public class BaseUiTestCase extends BaseTestCase {

    protected static final String appUrl = TestProperties.inst().TEAMMATES_URL;
    
    /** 
     * Creates a {@link Url} for the supplied {@code url} parameter.
     * If url given is a relative one (e.g., "/page/adminHomePage"), 
     * adds test.app.url (from test.properties) to it.
     */
    protected static Url createUrl(String url){
        if(url.startsWith("/")){
            url = TestProperties.inst().TEAMMATES_URL + url;
        }
        return new Url(url);
    }

    /**
     * Logs in a page using admin credentials (i.e. in masquerade mode).
     */
    protected static <T extends AppPage> T loginAdminToPage(Browser browser, Url url, Class<T> typeOfPage) {
        
        String adminUsername = TestProperties.inst().TEST_ADMIN_ACCOUNT; 
        String adminPassword = TestProperties.inst().TEST_ADMIN_PASSWORD;
        
        String instructorId = url.get(Const.ParamsNames.USER_ID);
        
        if(instructorId==null){ //admin using system as admin
            instructorId = "defaultAdmin";
        }
        
        if(browser.isAdminLoggedIn){
            browser.driver.get(url.toString());
        } else {
            //logout and attempt to load the requested URL. This will be 
            //  redirected to a dev-server/google login page
            AppPage.logout(browser);
            browser.driver.get(url.toString());
            String pageSource = browser.driver.getPageSource();
            
            //login based on the login page type
            if(DevServerLoginPage.containsExpectedPageContents(pageSource)){
                DevServerLoginPage loginPage = AppPage.getNewPageInstance(browser, DevServerLoginPage.class);
                loginPage.loginAdminAsInstructor(adminUsername, adminPassword, instructorId);
    
            } else if(GoogleLoginPage.containsExpectedPageContents(pageSource)){
                GoogleLoginPage loginPage = AppPage.getNewPageInstance(browser, GoogleLoginPage.class);
                loginPage.loginAdminAsInstructor(adminUsername, adminPassword, instructorId);
            
            } else {
                throw new IllegalStateException("Not a valid login page :" + pageSource);
            }
        }
        
        //After login, the browser should be redirected to the page requested originally.
        //  No need to reload. In fact, reloading might results in duplicate request to the server.
        return AppPage.getNewPageInstance(browser, typeOfPage);
    }
    
    /**
     * Deletes are recreates the given data on the datastore.
     */
    protected static void restoreTestDataOnServer(DataBundle testData) {

        int counter = 0;
        while(counter != 20){
            String backDoorOperationStatus = BackDoor.restoreDataBundle(testData);
            if(backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS)){
                break;
            }
            ThreadHelper.waitFor((int)( Math.random() * 2000));
            counter++;
        }
        if(counter == 20){
            Assumption.fail("Fail to restore data");
        }
    }

    protected static AdminHomePage loginAdmin(Browser currentBrowser) {
        return loginAdminToPage(currentBrowser, createUrl(Const.ActionURIs.ADMIN_HOME_PAGE), AdminHomePage.class);
    }

}
