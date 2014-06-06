package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCourseRemindAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseRemindActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        String adminUserId = "admin.user";

        ______TS("Typical case: Send email to remind an instructor to register for the course");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorAttributes anotherInstructorOfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email
        };
        
        Action remindAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) remindAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, redirectResult.destination);
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO +  anotherInstructorOfCourse1.email,
                     redirectResult.getStatusMessage());
             
        String expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br/>"
                + anotherInstructorOfCourse1.name + "<span class=\"bold\"> (" 
                + anotherInstructorOfCourse1.email + ")" + "</span>.<br/>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        ______TS("Typical case: Send email to remind a student to register for the course");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        
        remindAction = getAction(submissionParams);
        redirectResult = (RedirectResult) remindAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO +  student1InCourse1.email,
                     redirectResult.getStatusMessage());
             
        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br/>"
                + student1InCourse1.name + "<span class=\"bold\"> (" 
                + student1InCourse1.email + ")" + "</span>.<br/>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        ______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");
        gaeSimulation.loginAsAdmin(adminUserId);
        StudentAttributes unregisteredStudent1 = new StudentAttributes("Section 1", "Team Unregistered", "Unregistered student 1",
                                                                           "unregistered1@email.com", "", courseId);
        StudentAttributes unregisteredStudent2 = new StudentAttributes("Section 1", "Team Unregistered", "Unregistered student 2",
                                                                           "unregistered2@email.com", "", courseId);
        StudentsLogic.inst().createStudentCascade(unregisteredStudent1);
        StudentsLogic.inst().createStudentCascade(unregisteredStudent2);
        
        /* Reassign the attributes to retrieve their keys */
        unregisteredStudent1 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent1.email);
        unregisteredStudent2 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent2.email);
        
        submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, courseId
        };
        remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = (RedirectResult) remindAction.executeAndPostProcess();
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDERS_SENT,
                     redirectResult.getStatusMessage());
             
        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br/>"
                + unregisteredStudent1.name + "<span class=\"bold\"> (" 
                + unregisteredStudent1.email + ")" + "</span>.<br/>"
                + StringHelper.encrypt(unregisteredStudent1.key) + "<br/>"
                + unregisteredStudent2.name + "<span class=\"bold\"> (" 
                + unregisteredStudent2.email + ")" + "</span>.<br/>"
                + StringHelper.encrypt(unregisteredStudent2.key) + "<br/>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());
        
        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent1.email);
        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent2.email);

        ______TS("Failure case: Invalid email parameter");

        String invalidEmail = "invalidEmail.com";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail
        };
        
        try { 
            remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
            redirectResult = (RedirectResult) remindAction.executeAndPostProcess();
        } catch(EntityDoesNotExistException e){
            assertEquals("Instructor ["+ invalidEmail + "] does not exist in course [" + courseId + "]", e.getMessage());    
        }
        
    }

     private InstructorCourseRemindAction getAction(String... parameters) throws Exception {
        return (InstructorCourseRemindAction)gaeSimulation.getActionObject(uri, parameters);
    }

}
