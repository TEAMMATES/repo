package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.QUnitPage;

/**
 * Loads all JavaScript unit tests (done in QUnit) into a browser window and
 * ensures all tests passed. This class is not using the PageObject pattern
 * because it is not a regular UI test.
 */
public class AllJsTests extends BaseUiTestCase {
    
    private static Browser browser;
    private static QUnitPage page;
    private static final float MIN_COVERAGE_REQUIREMENT = 30;
    
    @BeforeClass
    public static void setUp() throws IOException {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
        page = AppPage.getNewPageInstance(browser)
                      .navigateTo(createLocalUrl("/allJsUnitTests.html?coverage"))
                      .changePageType(QUnitPage.class);
        page.waitForPageToLoad();
    }

    @Test
    public void executeJsTests() {
        int totalCases = page.getTotalCases();
        int failedCases = page.getFailedCases();
        
        print("Executed " + totalCases + " JavaScript Unit tests...");

        assertSame(failedCases, 0);
        assertNotSame(totalCases, 0);
        
        print("As expected, 0 failed tests out of " + totalCases + " tests.");

        float coverage = page.getCoverage();
        
        print(coverage + "% of scripts covered, the minimum requirement is " + MIN_COVERAGE_REQUIREMENT + "%");
        assertTrue(coverage >= MIN_COVERAGE_REQUIREMENT);
    }

    @AfterClass
    public static void tearDown() {
        BrowserPool.release(browser);
    }
}
