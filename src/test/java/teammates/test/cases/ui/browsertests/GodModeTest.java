package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.FileWriter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Assumption;
import teammates.common.util.FileHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class GodModeTest extends BaseUiTestCase {
    private static Browser browser;
    private static AppPage page;
    private static String initialContent; 

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        if (TestProperties.inst().isDevServer()) {
            injectRealAccountsIntoFile();
            writeToFile(getOutputFilePath(), 
                    "<div id='mainContent'>test</div>");
            browser = BrowserPool.getBrowser();
            page = AppPage.getNewPageInstance(browser).navigateTo(createLocalUrl("/godmode.html"));
        }
    }
    
    private static void injectRealAccountsIntoFile() throws Exception {
        initialContent = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/godmode.html");
        String changedContent = HtmlHelper.injectContextDependentValuesForTest(initialContent);
        writeToFile(TestProperties.TEST_PAGES_FOLDER + "/godmode.html", changedContent);
    }

    private static void writeToFile(String filePath, String content) throws Exception {
        try {
            FileWriter output = new FileWriter(new File(filePath));
            output.write(content);
            output.close();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testGodMode() throws Exception {
        if (!TestProperties.inst().isDevServer()) return;
        
        ______TS("test verifyHtml");
        
        System.clearProperty("godmode");
        Assumption.assertNull(System.getProperty("godmode"));
        
        try {
            // should fail as the expected file "godmodeOutpout
            page.verifyHtml("/godmodeOutput.html");
            signalFailureToDetectException(" - Assertion Error");
        } catch (AssertionError ae) {
            // expected
        }
        
        System.setProperty("godmode", "true");
        // automatically generates the file and hence passes
        page.verifyHtml("/godmodeOutput.html");
        
        System.clearProperty("godmode");
        Assumption.assertNull(System.getProperty("godmode"));
        
        // should pass without need for godmode
        // as the file has already been generated
        page.verifyHtml("/godmodeOutput.html");
        
        String expectedOutputPage = FileHelper.readFile(getExpectedOutputFilePath());
        String actualOutputPage = FileHelper.readFile(getOutputFilePath());
        
        // ensure that the generated file is as expected
        verifyOutput(expectedOutputPage, actualOutputPage);
        
        ______TS("test verifyHtmlMainContent");
        // repeat the same procedure as above for this
        
        writeToFile(getOutputFilePath(), 
                "<div id='mainContent'>test</div>");
        
        try {
            page.verifyHtml("/godmodeOutput.html");
            signalFailureToDetectException(" - Assertion Error");
        } catch (AssertionError ae) {
            // expected
        }
        
        System.setProperty("godmode", "true");
        page.verifyHtmlMainContent("/godmodeOutput.html");
        
        System.clearProperty("godmode");
        Assumption.assertNull(System.getProperty("godmode"));
        
        page.verifyHtmlMainContent("/godmodeOutput.html");
        
        expectedOutputPage = FileHelper.readFile(getExpectedOutputPartFilePath());
        actualOutputPage = FileHelper.readFile(getOutputFilePath());
        
        verifyOutput(expectedOutputPage, actualOutputPage);
        
        
    }

    private void verifyOutput(String expected, String actual) {
        String processedExpectedHtml = HtmlHelper.convertToStandardHtml(expected, true);
        String processedActualHtml = HtmlHelper.convertToStandardHtml(actual, true);
        
        assertEquals(processedExpectedHtml, processedActualHtml);
    }

    @AfterClass
    public static void closeClass() throws Exception {
        BrowserPool.release(browser);
        System.clearProperty("godmode");
        writeToFile(TestProperties.TEST_PAGES_FOLDER + "/godmode.html", initialContent);
        
        File file = new File(getOutputFilePath());
        // delete the output file generated
        if(!file.delete()){
            System.out.println("Delete failed. " + file.getAbsolutePath());
            file.deleteOnExit();
        }
    }

    private static String getOutputFilePath() throws Exception{
        return TestProperties.TEST_PAGES_FOLDER + "/godmodeOutput.html";
    }
    
    private static String getExpectedOutputFilePath() {
        return TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedOutput.html";
    }
    
    private String getExpectedOutputPartFilePath() {
        return TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedPartOutput.html";
    }

}
