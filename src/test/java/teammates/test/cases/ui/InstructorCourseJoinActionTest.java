package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseJoinAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseJoinActionTest extends BaseActionTest {
    DataBundle dataBundle;
    String invalidEncryptedKey = StringHelper.encrypt("invalidKey");
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorsDb instrDb = new InstructorsDb();
        // Reassign to let "key" variable in "instructor" not to be null
        instructor = instrDb.getInstructorForGoogleId(instructor.courseId, instructor.googleId);

        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("Invalid key, redirect for confirmation again");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        InstructorCourseJoinAction confirmAction = getAction(submissionParams);
        ShowPageResult pageResult = (ShowPageResult) confirmAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION 
                        + "?error=false&user=" + instructor.googleId, pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        String expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br/>Google ID: " + instructor.googleId
                + "<br/>Key: " + invalidEncryptedKey;
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());

        ______TS("Already registered instructor, redirect straight to authentication page");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor.key)
        };
        
        confirmAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) confirmAction.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED 
                        + "?regkey=" + StringHelper.encrypt(instructor.key)
                        + "&error=false&user=" + instructor.googleId, redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("", redirectResult.getStatusMessage());
        
        expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br/>Google ID: " + instructor.googleId
                + "<br/>Key: " + StringHelper.encrypt(instructor.key);
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());
        
        ______TS("Typical case: unregistered instructor, redirect to confirmation page");
        
        instructor = new InstructorAttributes("ICJAT.instr", instructor.courseId, "New Instructor", "ICJAT.instr@email.com");
        InstructorsLogic.inst().addInstructor(instructor.courseId, instructor.name, instructor.email);
        
        AccountAttributes newInstructorAccount = new AccountAttributes(
                instructor.googleId, instructor.name, false,
                instructor.email, "NUS");
        AccountsLogic.inst().createAccount(newInstructorAccount);
        
        InstructorAttributes newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        
        gaeSimulation.loginUser(instructor.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };
        
        confirmAction = getAction(submissionParams);
        pageResult = (ShowPageResult) confirmAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION + "?error=false&user=ICJAT.instr",
                        pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br/>Google ID: " + instructor.googleId
                + "<br/>Key: " + StringHelper.encrypt(newInstructor.key);
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());
    }
    
    private InstructorCourseJoinAction getAction(String... params) throws Exception {
        return (InstructorCourseJoinAction) (gaeSimulation.getActionObject(uri, params));
    }
}
