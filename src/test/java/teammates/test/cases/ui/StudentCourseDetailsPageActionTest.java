package teammates.test.cases.ui;

import static org.testng.AssertJUnit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import teammates.test.util.TestHelper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.labs.repackaged.com.google.common.base.Joiner;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseDetailsPageAction;
import teammates.ui.controller.StudentCourseDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class StudentCourseDetailsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testAccessControl() throws Exception {

        String iDOfCourseOfStudent = dataBundle.students
                .get("student1InCourse1").course;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, iDOfCourseOfStudent
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);

        iDOfCourseOfStudent = dataBundle.students.get("student2InCourse1").course;
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, iDOfCourseOfStudent
        };

        verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
    }

    /*
     * This Parent's method is overridden because: 1. Parent's
     * verifyCannotAccess method only check whether there is a
     * UnauthorizedAccessException 2. In StudentCourseDetailsPageAction's
     * Execute() function, if student is not joined, it will just return a
     * redirectResult, leaving no chance for exception throwing 3. So using the
     * parent's method will fail to verify this case since it is waiting for
     * exception that will never be created or thrown
     * 
     * So, this Overriding method will check the returned result for
     * verification purpose
     */
    @Override
    protected void verifyCannotAccess(String... params) throws Exception {
        try {
            Action c = gaeSimulation.getActionObject(uri, params);

            ActionResult result = c.executeAndPostProcess();

            String classNameOfRedirectResult = RedirectResult.class.getName();
            assertEquals(classNameOfRedirectResult, result.getClass().getName());
            
        } catch (Exception e) {
            ignoreExpectedException();
        }
        
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {

        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");

        String iDOfCourseOfStudent = dataBundle.students
                .get("student1InCourse1").course;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, iDOfCourseOfStudent
        };

        ______TS("Invalid parameters");
        // parameters missing.
        verifyAssumptionFailure(new String[] {});

        ______TS("Typical case, student in the same course");
        String studentId = student1InCourse1.googleId;
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);
        StudentCourseDetailsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_DETAILS+ "?error=false&user=student1InCourse1" , r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        StudentCourseDetailsPageData pageData = (StudentCourseDetailsPageData) r.data;

        assertEquals(student1InCourse1.course, pageData.courseDetails.course.id);
        assertEquals(studentId, pageData.account.googleId);
        assertEquals(student1InCourse1.getIdentificationString(),pageData.student.getIdentificationString());
        assertEquals(student1InCourse1.team, pageData.team.name);

        List<StudentAttributes> expectedStudentsList = StudentsLogic.inst().getStudentsForTeam(student1InCourse1.team, student1InCourse1.course);
        List<StudentAttributes> actualStudentsList = pageData.team.students;
          
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedStudentsList,actualStudentsList));

        // assertEquals(StudentsLogic.inst().getStudentsForTeam(student1InCourse1.team,
        // student1InCourse1),pageData.);
        // above comparison method failed, so use the one below 
        List<InstructorAttributes> expectedInstructorsList = InstructorsLogic.inst().getInstructorsForCourse(student1InCourse1.course);
        List<InstructorAttributes> actualInstructorsList = pageData.instructors;
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedInstructorsList,actualInstructorsList));

        String expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true"
                + "|||Student|||Student 1 in course 1|||student1InCourse1|||sudent1inCourse1@gmail.com"
                + "|||studentCourseDetails Page Load<br>Viewing team details for <span class=\"bold\">"
                + "[idOfTypicalCourse1] Typical Course 1 with 2 Evals</span>|||/page/studentCourseDetailsPage";

        assertEquals(expectedLogMessage, a.getLogMessage());

    }

    private StudentCourseDetailsPageAction getAction(String... params)throws Exception {   
        return (StudentCourseDetailsPageAction) (gaeSimulation.getActionObject(uri, params));
    }

}
