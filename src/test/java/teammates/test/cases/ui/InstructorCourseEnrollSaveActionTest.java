package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollPageData;
import teammates.ui.controller.InstructorCourseEnrollResultPageData;
import teammates.ui.controller.InstructorCourseEnrollSaveAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseEnrollSaveActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, ""
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        String enrollString = "";
        String[] submissionParams = new String[]{};
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Typical case: add and edit students for non-empty course, without header row");        
        // A new student
        enrollString = "Team 1\tJean Wong\tjean@email.com\tExchange student";
        // A student to be modified
        enrollString += Const.EOL + "Team 1.1\tstudent1 In Course1\tstudent1InCourse1@gmail.com\tNew comment added";
        // An existing student with no modification
        enrollString += Const.EOL + "Team 1.1\tstudent2 In Course1\tstudent2InCourse1@gmail.com\t";
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        InstructorCourseEnrollSaveAction enrollAction = getAction(submissionParams);
        
        ShowPageResult pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT + "?error=false&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseEnrollResultPageData pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.courseId);
        
        StudentAttributes newStudent = new StudentAttributes("jean", "jean@email.com", "Jean Wong", "Exchange student", courseId, "Team 1");
        newStudent.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudent, pageData.students);
        
        StudentAttributes modifiedStudent = dataBundle.students.get("student1InCourse1");
        modifiedStudent.comments = "New comment added";
        modifiedStudent.updateStatus = StudentAttributes.UpdateStatus.MODIFIED;
        verifyStudentEnrollmentStatus(modifiedStudent, pageData.students);
        
        StudentAttributes unmodifiedStudent = dataBundle.students.get("student2InCourse1");
        unmodifiedStudent.updateStatus = StudentAttributes.UpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudent, pageData.students);
        
        String expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" 
                + courseId + "]:</span><br>" + enrollString.replace("\n", "<br>"); 
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Masquerade mode, enrollment into empty course, with header row");
        
        if (CoursesLogic.inst().isCoursePresent("new-course")){
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
                    
        courseId = "new-course";
        CoursesLogic.inst().createCourseAndInstructor(instructorId, courseId, "New course");
        
        gaeSimulation.loginAsAdmin("admin.user");
        
        String headerRow = "Name\tEmail\tTeam\tComment";
        String studentsInfo = "Jean Wong\tjean@email.com\tTeam 1\tExchange student"
                            + Const.EOL + "James Tan\tjames@email.com\tTeam 2\t";
        enrollString = headerRow + Const.EOL +  studentsInfo;
        
        submissionParams = new String[]{
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT + "?error=false&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.courseId);

        StudentAttributes student1 = new StudentAttributes("jean", "jean@email.com", "Jean Wong", "Exchange student", courseId, "Team 1");
        student1.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student1, pageData.students);
        
        StudentAttributes student2 = new StudentAttributes("james", "james@email.com", "James Tan", "", courseId, "Team 2");
        student2.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student2, pageData.students);
        
        expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" 
                + courseId + "]:</span><br>" + enrollString.replace("\n", "<br>"); 
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Failure case: enrollment failed due to invalid lines");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        String studentWithoutEnoughParam = "Team 1\tStudentWithNoEmailInput";
        String studentWithInvalidEmail = "Team 2\tBenjamin Tan\tinvalid.email.com";
        enrollString = studentWithoutEnoughParam + Const.EOL + studentWithInvalidEmail;
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageResult.destination);
        assertEquals(true, pageResult.isError);
        String expectedStatusMessage = "<p><span class=\"bold\">Problem in line : <span class=\"invalidLine\">"
                            + studentWithoutEnoughParam + "</span></span><br><span class=\"problemDetail\">&bull; "
                            + StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS + "</span></p>" +
                            "<br>" + "<p><span class=\"bold\">Problem in line : <span class=\"invalidLine\">"
                            + studentWithInvalidEmail + "</span></span><br><span class=\"problemDetail\">&bull; "
                            + "\"invalid.email.com\" is not acceptable to TEAMMATES as an email because it is not in the correct format. An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than 45 characters. It cannot be empty and it cannot have spaces."
                            + "</span></p>";
        assertEquals(expectedStatusMessage, pageResult.getStatusMessage());
        
        InstructorCourseEnrollPageData enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.courseId);
        assertEquals(enrollString, enrollPageData.enrollStudents);
        
        expectedLogSegment = expectedStatusMessage + "<br>Enrollment string entered by user:<br>" + (enrollString).replace("\n", "<br>");
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Failure case: empty input");

        enrollString = "";
    
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL +"?message=Please+input+at+least+one+student+detail."
                    + "&error=true&user=idOfInstructor1OfCourse1", pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, pageResult.getStatusMessage());
        
        enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.courseId);
        assertEquals(enrollString, enrollPageData.enrollStudents);
        
        AssertHelper.assertContains(Const.StatusMessages.ENROLL_LINE_EMPTY, enrollAction.getLogMessage());
            
        CoursesLogic.inst().deleteCourseCascade("new-course");
    }
    
    /**
     * Verify if <code>student exists in the <code>studentsAfterEnrollment
     */
    private void verifyStudentEnrollmentStatus(StudentAttributes student, List<StudentAttributes>[] studentsAfterEnrollment) {
        boolean result = false;
        
        StudentAttributes.UpdateStatus status = student.updateStatus;
        for (StudentAttributes s : studentsAfterEnrollment[status.numericRepresentation]) {
            if (s.isEnrollInfoSameAs(student)) {
                result = true;
                break;
            }
        }
        
        assertEquals(true, result);
    }
    
    private InstructorCourseEnrollSaveAction getAction(String... params) throws Exception {
        return (InstructorCourseEnrollSaveAction) (gaeSimulation.getActionObject(uri, params));
    }

}
