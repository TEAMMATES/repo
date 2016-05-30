package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.ImageResult;
import teammates.ui.controller.StudentProfilePictureAction;

public class StudentProfilePictureActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    private StudentProfilePictureAction action;
    private ImageResult result;
    private final AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
    private final StudentAttributes student = dataBundle.students.get("student1InCourse1");

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_PROFILE_PICTURE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        testActionWithNoParams();
        testActionWithBlobKey();
        testActionWithEmailAndCourse();
    }

    public void testActionWithNoParams() throws Exception {

        ______TS("Failure case: no parameters given");
        gaeSimulation.loginAsStudent(account.googleId);

        String[] submissionParams = new String[] {};

        action = getAction(submissionParams);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals("expected blob-key, or student email with courseId", ae.getMessage());
        }

    }

    /**
     * Tests the branch of the Action handling a request from the Student
     * directly, where the parameters are simply the blobKey of the picture
     * itself.
     * 
     * @throws Exception
     */
    public void testActionWithBlobKey() throws Exception {
        testActionWithBlobKeySuccess();
        testActionWithBlobKeySuccessMasquerade();
    }

    protected void testActionWithBlobKeySuccess() throws Exception {
        ______TS("Typical case: using blobkey");
        gaeSimulation.loginAsStudent(account.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.BLOB_KEY, account.studentProfile.pictureKey
        };
        action = getAction(submissionParams);
        result = (ImageResult) action.executeAndPostProcess();

        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(account.studentProfile.pictureKey, result.blobKey);
        verifyLogMessageForActionWithBlobKey(false);
    }

    protected void testActionWithBlobKeySuccessMasquerade() throws Exception {
        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, account.googleId,
                Const.ParamsNames.BLOB_KEY, account.studentProfile.pictureKey
        };
        action = getAction(addUserIdToParams(account.googleId, submissionParams));
        result = (ImageResult) action.executeAndPostProcess();

        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());
        verifyLogMessageForActionWithBlobKey(true);
    }

    /**
     * Tests the branch of the Action handling a request from an Instructor,
     * where the parameters are the student's course and email
     * 
     * @throws Exception
     */
    public void testActionWithEmailAndCourse() throws Exception {
        AccountAttributes instructor = dataBundle.accounts.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor("idOfInstructor1OfCourse1");

        testActionWithEmailAndCourseSuccessTypical(instructor);
        testActionWithEmailAndCourseNoStudent();
        testActionWithEmailAndCourseForUnregStudent();
        testActionWithEmailAndCourseUnauthorisedInstructor();
    }

    protected void testActionWithEmailAndCourseSuccessTypical(AccountAttributes instructor)
            throws Exception {

        ______TS("Typical case: using email and course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };

        action = getAction(submissionParams);
        result = (ImageResult) action.executeAndPostProcess();

        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals("asdf34&hfn3!@", result.blobKey);
        verifyLogMessageForActionWithEmailAndCourse(instructor, false);
    }

    protected void testActionWithEmailAndCourseNoStudent() {
        ______TS("Failure case: student does not exist");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt("random-email"),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };

        action = getAction(submissionParams);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("Entity Does not exist");
        } catch (EntityNotFoundException enfe) {
            assertEquals("student with " + student.course + "/random-email", enfe.getMessage());
        }
    }

    protected void testActionWithEmailAndCourseForUnregStudent() throws Exception {
        InstructorAttributes unregCourseInstructor = createNewInstructorForUnregCourse();
        gaeSimulation.loginAsInstructor(unregCourseInstructor.googleId);

        testActionForStudentWithEmptyGoogleId();

        // remove new instructor
        AccountsLogic.inst().deleteAccountCascade(unregCourseInstructor.googleId);
    }

    @SuppressWarnings("deprecation")
    private InstructorAttributes createNewInstructorForUnregCourse()
            throws Exception {
        String course = dataBundle.courses.get("unregisteredCourse").getId();
        AccountsLogic.inst().createAccount(new AccountAttributes("unregInsId", "unregName", true,
                                                                 "unregIns@unregcourse.com", "unregInstitute"));
        InstructorAttributes instructor = new InstructorAttributes("unregInsId", course, "unregName",
                                                                   "unregIns@unregcourse.com");
        InstructorsLogic.inst().createInstructor(instructor);
        return instructor;
    }

    private void testActionForStudentWithEmptyGoogleId() throws Exception {
        ______TS("Failure case: no profile available (unreg student)");

        StudentAttributes student = dataBundle.students.get("student2InUnregisteredCourse");
        assertTrue(student.googleId.isEmpty());
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };

        action = getAction(submissionParams);
        result = (ImageResult) action.executeAndPostProcess();

        assertEquals("", result.blobKey);
    }

    protected void testActionWithEmailAndCourseUnauthorisedInstructor()
            throws Exception {
        ______TS("Failure case: instructor not from same course");
        AccountAttributes unauthInstructor = dataBundle.accounts.get("instructor1OfCourse2");
        gaeSimulation.loginAsInstructor(unauthInstructor.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, StringHelper.encrypt(student.email),
                Const.ParamsNames.COURSE_ID, StringHelper.encrypt(student.course)
        };

        action = getAction(submissionParams);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("Unauthorised Access");
        } catch (UnauthorizedAccessException uae) {
            assertEquals("User is not instructor of the course that student belongs to", uae.getMessage());
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- Helper Functions
    // -----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void verifyLogMessageForActionWithEmailAndCourse(AccountAttributes instructor,
                                                             boolean isMasquerade) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic"
                                  + "|||true|||Instructor" + (isMasquerade ? "(M)" : "") + "|||"
                                  + instructor.name + "|||" + instructor.googleId + "|||" + instructor.email
                                  + "|||Requested Profile Picture by instructor/other students|||/page/studentProfilePic";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    private void verifyLogMessageForActionWithBlobKey(boolean isMasquerade) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePic|||studentProfilePic"
                                  + "|||true|||Student" + (isMasquerade ? "(M)" : "") + "|||"
                                  + account.name + "|||" + account.googleId + "|||" + student.email
                                  + "|||Requested Profile Picture by student directly|||/page/studentProfilePic";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    private StudentProfilePictureAction getAction(String... params) {
        return (StudentProfilePictureAction) (gaeSimulation.getActionObject(uri, params));
    }

}
