package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCoursesPageData;
import teammates.ui.controller.ShowPageResult;

/**
 * Test case for adding a course for an instructor
 * This test case will not fully cover the path in checking archived courses.
 * This will be tested in UI testing
 */
public class InstructorCourseAddActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, "ticac.tac.id",
                Const.ParamsNames.COURSE_NAME, "ticac tac name"};
        
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
    
    @Test
    public void testExecute() throws Exception{

        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        String adminUserId = "admin.user";
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();
        verifyAssumptionFailure(
                Const.ParamsNames.COURSE_NAME, "ticac tac name");
        
        ______TS("Error: Invalid parameter for Course ID");
        
        Action addAction = getAction(
                Const.ParamsNames.COURSE_ID, "ticac,tpa1,id",
                Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        ShowPageResult pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        assertEquals(
                Const.ViewURIs.INSTRUCTOR_COURSES+"?message=Please+use+only+alphabets%2C+numbers%2C+dots%2C+hyphens%2C+underscores+and+dollar+signs+in+course+ID.&error=true&user=idOfInstructor1OfCourse1", 
                pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INVALID_ID, pageResult.getStatusMessage());
        
        InstructorCoursesPageData pageData = (InstructorCoursesPageData)pageResult.data;
        assertEquals(1, pageData.allCourses.size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com"
                + "|||Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs in course ID."
                + "|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());

        ______TS("Typical case, 1 existing course");
        
        addAction = getAction(
                Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        pageData = (InstructorCoursesPageData)pageResult.data;
        assertEquals(2, pageData.allCourses.size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd" 
                + "|||instructorCourseAdd|||true|||Instructor|||Instructor 1 of Course 1" 
                + "|||idOfInstructor1OfCourse1|||instr1@course1.com" 
                + "|||Course added : ticac.tpa1.id<br>Total courses: 2|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        ______TS("Error: Try to add the same course again");
        
        addAction = getAction(
                Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        pageResult = (ShowPageResult)addAction.executeAndPostProcess();
        
        assertEquals(
                Const.ViewURIs.INSTRUCTOR_COURSES+"?message=A+course+by+the+same+ID+already+exists+in+the+system%2C+possibly+created+by+another+user.+Please+choose+a+different+course+ID&error=true&user=idOfInstructor1OfCourse1", 
                pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_EXISTS, pageResult.getStatusMessage());
        
        pageData = (InstructorCoursesPageData)pageResult.data;
        assertEquals(2, pageData.allCourses.size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.com|||A course by the same ID already exists in the system, possibly created by another user. Please choose a different course ID"
                + "|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        ______TS("Masquerade mode, 0 courses");
        
        CoursesLogic.inst().deleteCourseCascade(instructor1OfCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("ticac.tpa1.id");
        gaeSimulation.loginAsAdmin(adminUserId);
        addAction = getAction(
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
                Const.ParamsNames.COURSE_NAME, "ticac tpa2 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        String expectedDestination = Const.ViewURIs.INSTRUCTOR_COURSES + "?message=The+course+has+been+added.." +
                "+Click+%3Ca+href%3D%22%2Fpage%2FinstructorCourseEnrollPage%3Fcourseid%3Dticac.tpa2.id%26user%3DidOfInstructor1OfCourse1%22%3Ehere" +
                "%3C%2Fa%3E+to+add+students+to+the+course+or+click+%3Ca+href%3D%22%2Fpage%2FinstructorCourseEditPage%3Fcourseid%3Dticac.tpa2.id%26user%3DidOfInstructor1OfCourse1" +
                "%22%3Ehere%3C%2Fa%3E+to+add+other+instructors.%3Cbr%3EIf+you+don%27t+see+the+course+in+the+list+below%2C+please+refresh+the+page+after+a+few+moments.&error=false&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        String expectedStatus = "The course has been added.. Click <a href=\"/page/instructorCourseEnrollPage?courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add students to the course or " +
                        "click <a href=\"/page/instructorCourseEditPage?courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add other instructors.<br>If you don't see the course in the list below, please refresh the page after a few moments.";
        assertEquals(expectedStatus, pageResult.getStatusMessage());
        
        pageData = (InstructorCoursesPageData)pageResult.data;
        assertEquals(1, pageData.allCourses.size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd" 
                + "|||true|||Instructor(M)|||Instructor 1 of Course 1" 
                + "|||idOfInstructor1OfCourse1|||instr1@course1.com|||Course added : ticac.tpa2.id<br>Total courses: 1" 
                + "|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
    }
    
    private Action getAction(String... parameters) throws Exception {
        return (Action)gaeSimulation.getActionObject(uri, parameters);
    }

}
