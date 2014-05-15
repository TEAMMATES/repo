package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;
import com.google.gson.Gson;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationSubmissionAdjustmentAction;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.FeedbackSubmissionAdjustmentAction;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;
import teammates.test.cases.logic.SubmissionTaskQueueLogicTest.SubmissionTaskQueueCallback;

public class SubmissionsAdjustmentTest extends
        BaseComponentUsingTaskQueueTestCase {
    
    protected static StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
    protected static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    protected static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    protected static AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static CoursesLogic coursesLogic = CoursesLogic.inst();
    protected static EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    
    @SuppressWarnings("serial")
    public static class SubmissionsAdjustmentTaskQueueCallback 
                extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.COURSE_ID));
            assertNotNull(paramMap.get(ParamsNames.COURSE_ID));
            
            assertTrue(paramMap.containsKey(ParamsNames.ENROLLMENT_DETAILS));
            assertNotNull(paramMap.get(ParamsNames.ENROLLMENT_DETAILS));
            
            if (paramMap.containsKey(ParamsNames.EVALUATION_NAME)) {
                assertNotNull(paramMap.get(ParamsNames.EVALUATION_NAME));
            } else {
                assertTrue(paramMap.containsKey(ParamsNames.FEEDBACK_SESSION_NAME));
                assertNotNull(paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME));
            }
            
            SubmissionTaskQueueCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.setupWithTaskQueueCallbackClass(
                SubmissionsAdjustmentTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
    }
    
    @Test
    public void testAll() throws Exception {
        testAdjustmentOfResponses();
        testEnrollStudentsWithScheduledSubmissionAdjustment();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(EvaluationsLogic.class);
    }
    
    private void testEnrollStudentsWithScheduledSubmissionAdjustment() throws Exception{
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        dataBundle = getTypicalDataBundle();
        
        restoreTypicalDataInDatastore();
        
        ______TS("enrolling students to a non-existent course");
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0);
        
        String newStudentLine = "Team 1.1|n|s@g|c";
        String nonExistentCourseId = "courseDoesNotExist";
        String enrollLines = newStudentLine + Const.EOL;
        
        List<StudentAttributes> studentsInfo;
        try {
            studentsInfo = studentsLogic
                    .enrollStudents(enrollLines, nonExistentCourseId);
            assertTrue(false);
        } catch (EntityDoesNotExistException e) {
            assertTrue(true);
        }
        
        //Verify no tasks sent to the task queue
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0);
        
        ______TS("try to enroll with empty input enroll lines");
        enrollLines = "";
        
        try {
            studentsInfo = studentsLogic
                    .enrollStudents(enrollLines, course1.id);
            fail("Failure cause : Invalid enrollment executed without exceptions");
        } catch (EnrollException e) {
            String errorMessage = e.getLocalizedMessage();
            assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, errorMessage);
        }
        
        //Verify no tasks sent to the task queue
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0);
        
        ______TS("enroll new students to existing course" +
                "(to check the cascade logic of the SUT)");

        //enroll string can also contain whitespace lines
        enrollLines = newStudentLine + Const.EOL + "\t";
        
        studentsInfo = studentsLogic
                .enrollStudents(enrollLines, course1.id);
        
        //Check whether students are present in database
        assertNotNull(studentsLogic.getStudentForEmail(course1.id, "s@g"));

        //Verify no tasks sent to the task queue
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(
                fsLogic.getFeedbackSessionsForCourse(course1.id).size() +
                evaluationsLogic.getEvaluationsForCourse(course1.id).size());
        
        ______TS("change an existing students email and verify update "
                + "of responses");
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        
        String oldEmail = studentsInfo.get(0).email;
        StudentAttributes updatedAttributes = new StudentAttributes();
        updatedAttributes.email = "newEmail@g";
        updatedAttributes.course = course1.id;

        studentsLogic.updateStudentCascade(oldEmail, updatedAttributes);

        StudentAttributes updatedStudent = studentsLogic
                .getStudentForEmail(course1.id, updatedAttributes.email);
        LogicTestHelper.verifyPresentInDatastore(updatedStudent);

        //Verify no tasks sent to task queue 
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0);
        
        //Verify that no response exists for old email
        verifyResponsesDoNotExistForEmailInCourse(oldEmail, course1.id);
        verifySubmissionsDoNotExistForEmailInCourse(oldEmail, course1.id);
        
        ______TS("change team of existing student and verify deletion of all his responses");
        StudentAttributes studentInTeam1 = dataBundle.students.get("student1InCourse1");
        
        //verify he has existing team feedback responses in the system
        List<FeedbackResponseAttributes> student1responses = getAllTeamResponsesForStudent(studentInTeam1);
        assertTrue(student1responses.size() != 0);
        
        studentInTeam1.team = "Team 1.2";
        String student1enrollString = studentInTeam1.toEnrollmentString();
        studentsInfo = studentsLogic.enrollStudents(student1enrollString, studentInTeam1.course);
        
        //Verify scheduling of adjustment of responses
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(
                fsLogic.getFeedbackSessionsForCourse(studentInTeam1.course).size() +
                evaluationsLogic.getEvaluationsForCourse(course1.id).size());
        
        ______TS("error during enrollment");
        //Reset task count in TaskQueue callback
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        
        String invalidStudentId = "t1|n6|e6@g@";
        String invalidEnrollLine = invalidStudentId + Const.EOL;
        try {
            studentsInfo = studentsLogic
                    .enrollStudents(invalidEnrollLine, course1.id);
            assertTrue(false);
        } catch (EnrollException e) {
            String actualErrorMessage = e.getLocalizedMessage();

            String errorReason = String.format(FieldValidator.EMAIL_ERROR_MESSAGE, "e6@g@",
                    FieldValidator.REASON_INCORRECT_FORMAT);
            String expectedMessage = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM,
                    invalidStudentId, errorReason);
            
            assertEquals(expectedMessage, actualErrorMessage);
        }

        //Verify no task sent to the task queue
        SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0);
    }
    
    private void testAdjustmentOfResponses() throws Exception {
        restoreTypicalDataInDatastore();

        ______TS("typical case: add new student to existing team");
        String evaluationName = "evaluation1 In Course1";
        StudentAttributes newStudent = new StudentAttributes();
        newStudent.team = "Team 1.1";
        newStudent.course = "idOfTypicalCourse1";
        newStudent.email = "random@g";
        newStudent.name = "someName";
        newStudent.comments = "comments";
        
        /*
         * Old number of submissions = (4 * 4 + 1 * 1) = 17
         */
        int oldNumberOfSubmissionsForEvaluation = submissionsLogic
                .getSubmissionsForEvaluation(newStudent.course, evaluationName).size();
        assertEquals(17, oldNumberOfSubmissionsForEvaluation);
        
        studentsLogic.createStudentCascadeWithSubmissionAdjustmentScheduled(newStudent);
        
        StudentEnrollDetails enrollDetails = new StudentEnrollDetails
                (UpdateStatus.NEW, newStudent.course, newStudent.email, "", newStudent.team);
        
        ArrayList<StudentEnrollDetails> enrollList = new ArrayList<StudentEnrollDetails>();
        enrollList.add(enrollDetails);
        Gson gsonBuilder = Utils.getTeammatesGson();
        String enrollString = gsonBuilder.toJson(enrollList);
        
        //Prepare parameter map
        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put(ParamsNames.COURSE_ID, newStudent.course);
        paramMap.put(ParamsNames.EVALUATION_NAME, evaluationName);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollString);
        
        EvaluationSubmissionAdjustmentAction newSubmissionAdjustmentAction = new EvaluationSubmissionAdjustmentAction(paramMap);
        assertTrue(newSubmissionAdjustmentAction.execute());
        LogicTestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(evaluationName, 
                studentsLogic.getStudentsForCourse(newStudent.course), 
                submissionsLogic.getSubmissionsForCourse(newStudent.course));
        
        /*
         * New number of submissions = (5 * 5 + 1 * 1) = 26
         */
        int newNumberOfSubmissionsForEvaluation = submissionsLogic
                .getSubmissionsForEvaluation(newStudent.course, evaluationName).size();
        assertEquals(26, newNumberOfSubmissionsForEvaluation);
        
        ______TS("typical case : existing student changes team");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        
        //Verify pre-existing submissions and responses
        int oldNumberOfResponsesForSession = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertTrue(oldNumberOfResponsesForSession != 0);
        
        String oldTeam = student.team;
        String newTeam = "Team 1.2";
        student.team = newTeam;
        
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, oldTeam, newTeam);
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollList.add(enrollDetails);
        enrollString = gsonBuilder.toJson(enrollList);

        //Prepare parameter map
        paramMap = new HashMap<String,String>();
        paramMap.put(ParamsNames.COURSE_ID, student.course);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollString);
        
        studentsLogic.updateStudentCascadeWithSubmissionAdjustmentScheduled(student.email, student);
        FeedbackSubmissionAdjustmentAction responseAdjustmentAction = new FeedbackSubmissionAdjustmentAction(paramMap);
        assertTrue(responseAdjustmentAction.execute());
        
        paramMap.remove(ParamsNames.FEEDBACK_SESSION_NAME);
        paramMap.put(ParamsNames.EVALUATION_NAME, evaluationName);
        
        EvaluationSubmissionAdjustmentAction submissionAdjustmentAction = new EvaluationSubmissionAdjustmentAction(paramMap);
        assertTrue(submissionAdjustmentAction.execute());
        LogicTestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(evaluationName, 
                studentsLogic.getStudentsForCourse(student.course), 
                submissionsLogic.getSubmissionsForCourse(student.course));
        
        int numberOfNewResponses = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertEquals(0, numberOfNewResponses);
        
        /*
         * New number of submissions = (4 * 4 + 2 * 2) = 20
         */
        newNumberOfSubmissionsForEvaluation = submissionsLogic
                .getSubmissionsForEvaluation(newStudent.course, evaluationName).size();
        assertEquals(20, newNumberOfSubmissionsForEvaluation);
    }

    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> studentReceiverResponses = FeedbackResponsesLogic.inst()
                .getFeedbackResponsesForReceiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        List<FeedbackResponseAttributes> studentGiverResponses = FeedbackResponsesLogic.inst()
                .getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS || 
                question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        return returnList;
    }
    
    private List<FeedbackResponseAttributes> getAllResponsesForStudentForSession(StudentAttributes student,
            String feedbackSessionName) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> allResponseOfStudent = getAllTeamResponsesForStudent(student);
        
        for (FeedbackResponseAttributes responseAttributes : allResponseOfStudent) {
            if (responseAttributes.feedbackSessionName.equals(feedbackSessionName)) {
                returnList.add(responseAttributes);
            }
        }
        
        return returnList;
    }
    
    private void verifyResponsesDoNotExistForEmailInCourse(String email,
            String courseId) {
        List<FeedbackSessionAttributes> allSessions = fsLogic
                .getFeedbackSessionsForCourse(courseId); 
        
        for (FeedbackSessionAttributes eachSession : allSessions) {
            List<FeedbackResponseAttributes> allResponses = frLogic
                    .getFeedbackResponsesForSession(eachSession.feedbackSessionName, courseId);
            
            for (FeedbackResponseAttributes eachResponse : allResponses) {
                if (eachResponse.recipientEmail.equals(email) ||
                    eachResponse.giverEmail.equals(email)) {
                    fail("Cause : Feedback response for "
                         + email + " found on system");
                }
            }
        }
    }
    
    private void verifySubmissionsDoNotExistForEmailInCourse(String email,
            String courseId) {
        List<SubmissionAttributes> allSubmissions = submissionsLogic.getSubmissionsForCourse(courseId);
        
        for (SubmissionAttributes currentSubmission : allSubmissions) {
            if (currentSubmission.reviewee.equals(email) ||
                currentSubmission.reviewer.equals(email)) {
                fail("Cause : Submission for " + email +
                        " found on system");
            }
        }
    }
}
