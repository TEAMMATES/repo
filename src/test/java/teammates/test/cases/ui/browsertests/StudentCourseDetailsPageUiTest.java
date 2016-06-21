package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentCourseDetailsPage;

/**
 * Tests Student Course Details page
 */
public class StudentCourseDetailsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/StudentCourseDetailsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception {

        ______TS("content");
        
        //with teammates"
        // This is the full HTML verification for Student Course Details Page, the rest can all be verifyMainHtml
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.alice", "/studentCourseDetailsWithTeammatesHTML.html", true);

        //without teammates
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.charlie",
                      "/studentCourseDetailsWithoutTeammatesHTML.html", false);
        
        ______TS("links, inputValidation, actions");
        
        //nothing to test here.

    }

    private void verifyContent(String courseObjectId, String studentObjectId, String filePath,
                               boolean isFullPageChecked) throws Exception {
        AppUrl detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE)
                                .withUserId(testData.students.get(studentObjectId).googleId)
                                .withCourseId(testData.courses.get(courseObjectId).getId());
        StudentCourseDetailsPage detailsPage = loginAdminToPage(browser, detailsPageUrl, StudentCourseDetailsPage.class);
        if (isFullPageChecked) {
            detailsPage.verifyHtml(filePath);
        } else {
            detailsPage.verifyHtmlMainContent(filePath);
        }
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
}
