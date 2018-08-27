package teammates.e2e.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.e2e.pageobjects.Browser;
import teammates.e2e.pageobjects.BrowserPool;
import teammates.test.driver.TestProperties;

/**
 * Placeholder test to check if Selenium works.
 */
public class StaticPageE2ETest {

    private Browser browser;

    @BeforeClass
    public void getBrowser() {
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void placeholderTest() {
        browser.driver.get(TestProperties.TEAMMATES_URL);
        browser.driver.get(TestProperties.TEAMMATES_URL + "/web/home");
        browser.driver.get(TestProperties.TEAMMATES_URL + "/web/notfound");
    }

    @AfterClass
    public void releaseBrowser() {
        BrowserPool.release(browser);
    }

}
