package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionStats;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionPublishedMailAction;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.automated.FeedbackSessionPublishedReminderTest.FeedbackSessionPublishedCallback;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class FeedbackSessionsLogicTest extends BaseComponentUsingTaskQueueTestCase {
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private DataBundle dataBundle = getTypicalDataBundle();
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackSessionsLogic.class);
        gaeSimulation.setupWithTaskQueueCallbackClass(PublishUnpublishSessionCallback.class);
        gaeSimulation.resetDatastore();
        
    }
    
    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        //TODO: add restoreTypicalDataInDatastore() here and remove it from the rest of the file
    }
    
    @SuppressWarnings("serial")
    public static class PublishUnpublishSessionCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            EmailAction action = new FeedbackSessionPublishedMailAction(paramMap);
            action.getPreparedEmailsAndPerformSuccessOperations();
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @Test
    public void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("init : 0 non private sessions closing within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        
        assertEquals(0, sessionList.size());
        
        ______TS("typical case : 1 non private session closing within time limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.timeZone = 0;
        session.feedbackSessionType = FeedbackSessionType.STANDARD;
        session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        fsLogic.createFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        
        assertEquals(1, sessionList.size());
        assertEquals(session.feedbackSessionName, 
                sessionList.get(0).feedbackSessionName);
        
        ______TS("case : 1 private session closing within time limit");
        session.feedbackSessionType = FeedbackSessionType.PRIVATE;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        assertEquals(0, sessionList.size());
        
        //delete the newly added session as restoreTypicalDataInDatastore()
                //wont do it
        fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
                session.courseId);
    }
    
    @Test
    public void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {
        restoreTypicalDataInDatastore();
        ______TS("init : 0 open sessions");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 open session with mail unsent");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.timeZone = 0;
        session.feedbackSessionType = FeedbackSessionType.STANDARD;
        session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        session.sentOpenEmail = false;
        fsLogic.createFeedbackSession(session);        
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).feedbackSessionName,
                session.feedbackSessionName);
        
        ______TS("typical case : 1 open session with mail sent");
        session.sentOpenEmail = true;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 closed session with mail unsent");
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(0, sessionList.size());
        
        //delete the newly added session as restoreTypicalDataInDatastore()
        //wont do it
        fsLogic.deleteFeedbackSessionCascade(session.feedbackSessionName,
                session.courseId);
    }
    
    @Test
    public void testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent() throws Exception {
        
        restoreTypicalDataInDatastore();
        ______TS("init : no published sessions");
        unpublishAllSessions();
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
        
        assertEquals(0, sessionList.size());
        
        ______TS("case : 1 published session with mail unsent");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        session.timeZone = 0;
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        
        session.sentPublishedEmail = false;
        fsLogic.updateFeedbackSession(session);
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).feedbackSessionName,
                session.feedbackSessionName);
        
        ______TS("case : 1 published session with mail sent");
        session.sentPublishedEmail = true;
        fsLogic.updateFeedbackSession(session);        
        
        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
        assertEquals(0, sessionList.size());
    }
    
    @Test
    public void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {        
        ______TS("test create");
        
        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsLogic.createFeedbackSession(fs);
        LogicTest.verifyPresentInDatastore(fs);

        ______TS("test delete");
        // Create a question under the session to test for cascading during delete.
        FeedbackQuestionAttributes fq = new FeedbackQuestionAttributes();
        fq.feedbackSessionName = fs.feedbackSessionName;
        fq.courseId = fs.courseId;
        fq.questionNumber = 1;
        fq.creatorEmail = fs.creatorEmail;
        fq.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        fq.giverType = FeedbackParticipantType.STUDENTS;
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.questionMetaData = new Text("question to be deleted through cascade");
        fq.questionType = FeedbackQuestionType.TEXT;
        fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        
        fqLogic.createFeedbackQuestion(fq);
        
        fsLogic.deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
        LogicTest.verifyAbsentInDatastore(fs);
        LogicTest.verifyAbsentInDatastore(fq);
    }
    
    @Test
    public void testGetFeedbackSessionDetailsForInstructor() throws Exception {
        
        // This file contains a session with a private session + a standard
        // session + a special session with all questions without recipients.
        DataBundle dataBundle = loadDataBundle("/FeedbackSessionDetailsTest.json");
        new BackDoorLogic().persistDataBundle(dataBundle);
        
        Map<String,FeedbackSessionDetailsBundle> detailsMap =
                new HashMap<String,FeedbackSessionDetailsBundle>();
        
        ______TS("standard success case");
        
        List<FeedbackSessionDetailsBundle> detailsList = 
                fsLogic.getFeedbackSessionDetailsForInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        
        List<String> expectedSessions = new ArrayList<String>();
        expectedSessions.add(dataBundle.feedbackSessions.get("standard.session").toString());
        expectedSessions.add(dataBundle.feedbackSessions.get("no.responses.session").toString());
        expectedSessions.add(dataBundle.feedbackSessions.get("no.recipients.session").toString());
        expectedSessions.add(dataBundle.feedbackSessions.get("private.session").toString());
        
        String actualSessions = "";
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessions += details.feedbackSession.toString();
            detailsMap.put(
                    details.feedbackSession.feedbackSessionName + "%" +
                    details.feedbackSession.courseId,
                    details);
        }
        
        assertEquals(4, detailsList.size());
        AssertHelper.assertContains(expectedSessions, actualSessions);
        
        /** Standard session **/
        FeedbackSessionStats stats =
                detailsMap.get(dataBundle.feedbackSessions.get("standard.session").feedbackSessionName + "%" +
                                dataBundle.feedbackSessions.get("standard.session").courseId).stats;
        
        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // 1 instructor, 1 student, did not respond => 8-2=6
        assertEquals(6, stats.submittedTotal);
        
        
        /** No recipients session **/
        stats = detailsMap.get(dataBundle.feedbackSessions.get("no.recipients.session").feedbackSessionName + "%" +
                                dataBundle.feedbackSessions.get("no.recipients.session").courseId).stats;
        
        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // only 1 student responded
        assertEquals(1, stats.submittedTotal);
        
        /** No responses session **/
        stats = detailsMap.get(dataBundle.feedbackSessions.get("no.responses.session").feedbackSessionName + "%" +
                                dataBundle.feedbackSessions.get("no.responses.session").courseId).stats;
        
        // 1 instructors, 1 students = 2
        assertEquals(2, stats.expectedTotal);
        // no responses
        assertEquals(0, stats.submittedTotal);
        
        /** Private session **/
        stats = detailsMap.get(dataBundle.feedbackSessions.get("private.session").feedbackSessionName + "%" +
                dataBundle.feedbackSessions.get("private.session").courseId).stats;
        assertEquals(1, stats.expectedTotal);
        // For private sessions, we mark as completed only when creator has finished all questions.
        assertEquals(0, stats.submittedTotal);
        
        // Make session non-private
        FeedbackSessionAttributes privateSession = 
                dataBundle.feedbackSessions.get("private.session");
        privateSession.sessionVisibleFromTime = privateSession.startTime;
        privateSession.endTime = TimeHelper.convertToDate("2015-04-01 10:00 PM UTC");
        privateSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        fsLogic.updateFeedbackSession(privateSession);
        
        // Re-read details
        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                dataBundle.instructors.get("instructor1OfCourse1").googleId);
        for (FeedbackSessionDetailsBundle details : detailsList) {
            if(details.feedbackSession.feedbackSessionName.equals(
                    dataBundle.feedbackSessions.get("private.session").feedbackSessionName)){
                stats = details.stats;
                break;
            }
        }
        // 1 instructor (creator only), 6 students = 8
        assertEquals(7, stats.expectedTotal);
        // 1 instructor, 1 student responded
        assertEquals(2, stats.submittedTotal);
        
        ______TS("instructor does not exist");
            
        assertTrue(fsLogic.getFeedbackSessionDetailsForInstructor("non-existent.google.id").isEmpty());
                    
    }
    
    @Test
    public void testGetFeedbackSessionsForCourse() throws Exception {
        
        restoreTypicalDataInDatastore();
        dataBundle = getTypicalDataBundle();
        
        List<FeedbackSessionAttributes> actualSessions = null;
        
        ______TS("non-existent course");
        
        try {
            fsLogic.getFeedbackSessionsForUserInCourse("NonExistentCourseId", "randomUserId");
            signalFailureToDetectException("Did not detect that course does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to get feedback sessions for a course that does not exist.", edne.getMessage());
        }
        
       ______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");
        
        // 2 valid sessions in course 1, 0 in course 2.
        
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.com");
        
        // Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
        
        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertTrue(actualSessions.size() == 3);
        
        // Course 2 only has an instructor session and a private session.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "student1InCourse2@gmail.com");        
        assertTrue(actualSessions.isEmpty());
                
        ______TS("Instructor viewing");
        
        // 3 valid sessions in course 1, 1 in course 2.
        
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "instructor1@course1.com");
        
        // Instructors should be able to see all sessions for the course
        expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("empty.session").toString() + Const.EOL + 
                dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("closedSession").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
        
        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertTrue(actualSessions.size() == 6);
        
        // We should only have one session here as session 2 is private and this instructor is not the creator.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor2@course2.com");
        
        assertEquals(actualSessions.get(0).toString(),
                dataBundle.feedbackSessions.get("session2InCourse2").toString());
        assertTrue(actualSessions.size() == 1);

        
        ______TS("Private session viewing");
        
        // This is the creator for the private session.
        // We have already tested above that other instructors cannot see it.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor1@course2.com");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
                actualSessions.toString());

    }
    
    @Test
    public void testGetFeedbackSessionQuestionsForStudent() throws Exception {
        
        ______TS("standard test");

        restoreTypicalDataInDatastore();
        
        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForStudent(
                        "First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.com");
        
        // We just test this once.
        assertEquals(actual.feedbackSession.toString(), 
                dataBundle.feedbackSessions.get("session1InCourse1").toString());
        
        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        FeedbackQuestionAttributes expectedQuestion = 
                getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C1", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        expectedResponsesString = getResponseFromDatastore("response2ForQ2S1C1",dataBundle).toString();    
        actualResponses.clear();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        ______TS("team feedback test");

        // Check that student3 get team member's (student4) feedback response as well (for team question).
        actual = fsLogic.getFeedbackSessionQuestionsForStudent(
                        "Second feedback session", "idOfTypicalCourse1", "student3InCourse1@gmail.com");

        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        expectedQuestion = getQuestionFromDatastore("team.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore(
                "response1ForQ1S2C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle
                .get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2, no responses from this student yet
        expectedQuestion = getQuestionFromDatastore("team.members.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
        
        ______TS("failure: invalid session");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "invalid session", "idOfTypicalCourse1", "student3InCourse1@gmail.com");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a feedback session that does not exist.", e.getMessage());
        }
        
        ______TS("failure: non-existent student");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "Second feedback session", "idOfTypicalCourse1", "randomUserId");
            signalFailureToDetectException("Did not detect that student does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to get a feedback session for student that does not exist.", edne.getMessage());
        }
        
        
        
    }
    
    @Test
    public void testGetFeedbackSessionQuestionsForInstructor() throws Exception {
        ______TS("standard test");

        restoreTypicalDataInDatastore();
        
        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.com");
        
        // We just test this once.
        assertEquals(dataBundle.feedbackSessions.get("session2InCourse2").toString(), 
                actual.feedbackSession.toString());
        
        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());
        
        // Question 1
        FeedbackQuestionAttributes expectedQuestion = 
                getQuestionFromDatastore("qn1InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S2C2", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();        
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);
        
        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());
        
        ______TS("private test: not creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor2@course2.com");
        assertEquals(0, actual.questionResponseBundle.size());
        
        ______TS("private test: is creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor1@course2.com");
        assertEquals(1, actual.questionResponseBundle.size());
        expectedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        
        ______TS("failure: invalid session");
        
        try {
            fsLogic.getFeedbackSessionQuestionsForInstructor(
                    "invalid session", "idOfTypicalCourse1", "instructor1@course1.com");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a feedback session that does not exist.", e.getMessage());
        }
    }
    
    @Test
    public void testGetFeedbackSessionResultsForUser() throws Exception {
        
        // This file contains a session with a private session + a standard
        // session which needs to have enough qn/response combinations to cover as much
        // of the SUT as possible
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        new BackDoorLogic().persistDataBundle(responseBundle);
        
        ______TS("standard session with varied visibilities");        
        
        FeedbackSessionAttributes session =
                responseBundle.feedbackSessions.get("standard.session");
        
        /*** Test result bundle for student1 ***/
        StudentAttributes student = 
                responseBundle.students.get("student1InCourse1");        
        FeedbackSessionResultsBundle results =
                fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
                        session.courseId, student.email);
    
        // We just check for correct session once
        assertEquals(session.toString(), results.feedbackSession.toString());    
        
        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        assertEquals(11, results.responses.size());
        assertEquals(7, results.questions.size());
        
        // Test the user email-name maps used for display purposes
        String mapString = results.emailNameTable.toString();
        List<String> expectedStrings = new ArrayList<String>();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.com=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.com=student2 In Course1",
                "FSRTest.student4InCourse1@gmail.com=student4 In Course1",
                "Team 1.1=Team 1.1",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.com=Instructor1 Course1",
                "FSRTest.student1InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student2InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student4InCourse1@gmail.com" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.2");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student4InCourse1@gmail.com=Team 1.2",
                "FSRTest.student1InCourse1@gmail.com=Team 1.1",
                "FSRTest.student1InCourse1@gmail.com's Team=",
                "FSRTest.student2InCourse1@gmail.com's Team=",
                "FSRTest.student4InCourse1@gmail.com's Team=",
                "FSRTest.student2InCourse1@gmail.com=Team 1.1",
                "Team 1.1=",
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=",
                "FSRTest.instr1@course1.com=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.emailTeamNameTable.size());
        
        // Test 'Append TeamName to Name' for display purposes with Typical Cases
        expectedStrings.clear();
        List<String> actualStrings = new ArrayList<String>();
        for(FeedbackResponseAttributes response: results.responses) {
            String giverName = results.getNameForEmail(response.giverEmail);
            String giverTeamName = results.getTeamNameForEmail(response.giverEmail);
            giverName = results.appendTeamNameToName(giverName, giverTeamName);
            String recipientName = results.getNameForEmail(response.recipientEmail);
            String recipientTeamName = results.getTeamNameForEmail(response.recipientEmail);
            recipientName = results.appendTeamNameToName(recipientName, recipientTeamName);
            actualStrings.add(giverName);
            actualStrings.add(recipientName);
        }
        Collections.addAll(expectedStrings,
                "student1 In Course1 (Team 1.1)",
                "student1 In Course1 (Team 1.1)",
                "student1 In Course1 (Team 1.1)",
                "student2 In Course1 (Team 1.1)",
                "student2 In Course1 (Team 1.1)",
                "student1 In Course1 (Team 1.1)",
                "student1 In Course1 (Team 1.1)",
                "Instructor1 Course1 (Instructors)",
                "student1 In Course1 (Team 1.1)",
                "Team 1.3",
                "student2 In Course1 (Team 1.1)",
                "Team 1.4",
                "student2 In Course1 (Team 1.1)",
                "student4 In Course1 (Team 1.2)",
                "Team 1.1",
                "student2 In Course1 (Team 1.1)",
                "Team 1.1",
                "student4 In Course1 (Team 1.2)",
                "Team 1.1",
                "Team 1.2",
                "Team 1.2",
                "Team 1.1");
        assertEquals(expectedStrings.toString(), actualStrings.toString());
        
        // Test 'Append TeamName to Name' for display purposes with Special Cases
        expectedStrings.clear();
        actualStrings.clear();
        
        // case: Unknown User
        String UnknownUserName = Const.USER_UNKNOWN_TEXT;
        String someTeamName = "Some Team Name";
        UnknownUserName = results.appendTeamNameToName(UnknownUserName, someTeamName);
        actualStrings.add(UnknownUserName);
        
        // case: Nobody
        String NobodyUserName = Const.USER_NOBODY_TEXT;
        NobodyUserName = results.appendTeamNameToName(NobodyUserName, someTeamName);
        actualStrings.add(NobodyUserName);
        
        // case: Anonymous User
        String AnonymousUserName = "Anonymous " + System.currentTimeMillis();
        AnonymousUserName = results.appendTeamNameToName(AnonymousUserName, someTeamName);
        actualStrings.add(AnonymousUserName);
        Collections.addAll(expectedStrings,
                Const.USER_UNKNOWN_TEXT,
                Const.USER_NOBODY_TEXT,
                AnonymousUserName);
        assertEquals(expectedStrings.toString(), actualStrings.toString());
        
        // Test the generated response visibilityTable for userNames.        
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn1.resp1",responseBundle)+"={true,true}",
                getResponseId("qn2.resp1",responseBundle)+"={false,false}",
                getResponseId("qn2.resp3",responseBundle)+"={true,true}",
                getResponseId("qn3.resp1",responseBundle)+"={false,false}",
                getResponseId("qn4.resp2",responseBundle)+"={false,true}",
                getResponseId("qn4.resp3",responseBundle)+"={false,true}",
                getResponseId("qn5.resp1",responseBundle)+"={true,false}",
                getResponseId("qn7.resp1",responseBundle)+"={true,true}",
                getResponseId("qn7.resp2",responseBundle)+"={true,true}",
                getResponseId("qn8.resp1",responseBundle)+"={true,true}",
                getResponseId("qn8.resp2",responseBundle)+"={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.visibilityTable.size());
        
        
        /*** Test result bundle for instructor1 ***/
        InstructorAttributes instructor =
                responseBundle.instructors.get("instructor1OfCourse1");        
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.feedbackSessionName, 
                session.courseId, instructor.email);
        
        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(10, results.responses.size());
        assertEquals(5, results.questions.size());
        
        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "%GENERAL%=%NOBODY%",
                "FSRTest.student1InCourse1@gmail.com=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.com=student2 In Course1",
                "FSRTest.student3InCourse1@gmail.com=student3 In Course1",
                "FSRTest.student4InCourse1@gmail.com=student4 In Course1",
                "FSRTest.student5InCourse1@gmail.com=student5 In Course1",
                "FSRTest.student6InCourse1@gmail.com=student6 In Course1",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.com=Instructor1 Course1",
                "FSRTest.instr2@course1.com=Instructor2 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(12, results.emailNameTable.size());
        
        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student4InCourse1@gmail.com=Team 1.2",
                "FSRTest.student1InCourse1@gmail.com=Team 1.1",
                "FSRTest.student5InCourse1@gmail.com=Team 1.3",
                "FSRTest.student6InCourse1@gmail.com=Team 1.4",
                "%GENERAL%=",
                "FSRTest.student2InCourse1@gmail.com=Team 1.1",
                "FSRTest.student3InCourse1@gmail.com=Team 1.2",
                "Team 1.3=",
                "Team 1.2=",
                "FSRTest.instr2@course1.com=Instructors",
                "Team 1.4=",
                "FSRTest.instr1@course1.com=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(12, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.        
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn2.resp1",responseBundle)+"={false,false}",
                getResponseId("qn2.resp2",responseBundle)+"={false,false}",
                getResponseId("qn2.resp3",responseBundle)+"={false,false}",
                getResponseId("qn3.resp1",responseBundle)+"={true,false}",
                getResponseId("qn3.resp2",responseBundle)+"={false,false}",
                getResponseId("qn4.resp1",responseBundle)+"={true,true}",
                getResponseId("qn4.resp2",responseBundle)+"={true,true}",
                getResponseId("qn4.resp3",responseBundle)+"={true,true}",
                getResponseId("qn5.resp1",responseBundle)+"={false,true}",
                getResponseId("qn6.resp1",responseBundle)+"={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(10, results.visibilityTable.size());
        
        // TODO: test student2 too.
        
        ______TS("private session");

        session = responseBundle.feedbackSessions.get("private.session");
        
        /*** Test result bundle for student1 ***/
        student =  responseBundle.students.get("student1InCourse1");        
        results = fsLogic.getFeedbackSessionResultsForStudent(session.feedbackSessionName, 
                        session.courseId, student.email);
        
        assertEquals(0, results.questions.size());
        assertEquals(0, results.responses.size());
        assertEquals(0, results.emailNameTable.size());
        assertEquals(0, results.emailTeamNameTable.size());
        assertEquals(0, results.visibilityTable.size());
        
        /*** Test result bundle for instructor1 ***/
        
        instructor =
                responseBundle.instructors.get("instructor1OfCourse1");        
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.feedbackSessionName, 
                session.courseId, instructor.email);
        
        // Can see all responses regardless of visibility settings.
        assertEquals(2, results.questions.size());
        assertEquals(2, results.responses.size());
        
        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.com=student1 In Course1",
                "FSRTest.instr1@course1.com=Instructor1 Course1",
                "Team 1.2=Team 1.2");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(3, results.emailNameTable.size());
        
        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.com=Team 1.1",
                "Team 1.2=",
                "FSRTest.instr1@course1.com=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(3, results.emailTeamNameTable.size());

        // Test that name visibility is adhered to even when
        // it is a private session. (to protect anonymity during session type conversion)"
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("p.qn1.resp1",responseBundle)+"={false,false}",
                getResponseId("p.qn2.resp1",responseBundle)+"={true,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(2, results.visibilityTable.size());
        
        ______TS("failure: no session");
                
        try {
            fsLogic.getFeedbackSessionResultsForInstructor("invalid session", 
                session.courseId, instructor.email);
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to view non-existent feedback session.", e.getMessage());
        }
        //TODO: check for cases where a person is both a student and an instructor
    }
    
    
    @Test
    public void testGetFeedbackSessionResultsSummaryAsCsv() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("typical case");
    
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        String export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        /* This is what export should look like:
        ==================================
        Course,idOfTypicalCourse1
        Session Name,First feedback session
        
        
        Question 1,"What is the best selling point of your product?"
        
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Team 1.1","student1 In Course1","Team 1.1","student1 In Course1","Student 1 self feedback."
        "Team 1.1","student2 In Course1","Team 1.1","student2 In Course1","I'm cool'"
        
        
        Question 2,"Rate 5 other students' products",
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Team 1.1","student1 In Course1","Team 1.1","student1 In Course1","Response from student 1 to student 2."
        "Team 1.1","student2 In Course1","Team 1.1","student1 In Course1","Response from student 2 to student 1."
        "Team 1.1","student3 In Course1","Team 1.1","student2 In Course1","Response from student 3 ""to"" student 2.
        Multiline test."
        
        
        Question 3,"My comments on the class",
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Instructors","Instructor1 Course1","","-","Good work, keep it up!"
        */
        
        String[] exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What is the best selling point of your product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[7], "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student1 In Course1\",\"Student 1 self feedback.\"");
        // checking single quotes inside cell
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"I'm cool'\"");
        assertEquals(exportLines[9], "");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Question 2,\"Rate 1 other student's product\"");
        assertEquals(exportLines[12], "");
        assertEquals(exportLines[13], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[14], "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student1 In Course1\",\"Response from student 2 to student 1.\"");
        assertEquals(exportLines[15], "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"Response from student 1 to student 2.\"");
        // checking double quotes inside cell + multiline cell
        assertEquals(exportLines[16].trim(), "\"Team 1.1\",\"student3 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"Response from student 3 \"\"to\"\" student 2.");
        assertEquals(exportLines[17], "Multiline test.\"");
        assertEquals(exportLines[18], "");
        assertEquals(exportLines[19], "");
        assertEquals(exportLines[20], "Question 3,\"My comments on the class\"");
        assertEquals(exportLines[21], "");
        assertEquals(exportLines[22], "Team,Giver,Recipient's Team,Recipient,Feedback");
        // checking comma inside cell
        assertEquals(exportLines[23], "\"Instructors\",\"Instructor1 Course1\",\"\",\"-\",\"Good work, keep it up!\"");
        
        ______TS("MCQ results");
        
        restoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");
        dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        session = dataBundle.feedbackSessions.get("mcqSession");
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"MCQ Session"
         
         
        Question 1,"What do you like best about our product?"
         
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Team 1.1","student1 In Course1","Team 1.1","student1 In Course1","It's good"
        "Team 1.1","student2 In Course1","Team 1.1","student2 In Course1","It's perfect"
        
        
        Question 2,"What do you like best about the class' product?"
        
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Instructors","Instructor1 Course1","Instructors","Instructor1 Course1","It's good"
        "Instructors"."Instructor2 Course1","Instructors","Instructor2 Course1","It's perfect"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What do you like best about our product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[7], "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student1 In Course1\",\"It's good\"");
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"It's perfect\"");
        assertEquals(exportLines[9], "");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Question 2,\"What do you like best about the class' product?\"");
        assertEquals(exportLines[12], "");
        assertEquals(exportLines[13], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[14], "\"Instructors\",\"Instructor1 Course1\",\"Instructors\",\"Instructor1 Course1\",\"It's good\"");
        assertEquals(exportLines[15], "\"Instructors\",\"Instructor2 Course1\",\"Instructors\",\"Instructor2 Course1\",\"It's perfect\"");
        
        ______TS("MSQ results");
        
        session = dataBundle.feedbackSessions.get("msqSession");
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"MCQ Session"
         
         
        Question 1,"What do you like best about our product?"
         
        Team,Giver,Recipient's Team,Recipient,Feedbacks:,"It's good","It's perfect"
        "Team 1.1","student1 In Course1","Team 1.1","student1 In Course1",,"It's good",
        "Team 1.1","student2 In Course1","Team 1.1","student2 In Course1",,,"It's perfect"
        
        Question 2,"What do you like best about the class' product?"
        
        Team,Giver,Recipient's Team,Recipient,Feedbacks:,"It's good","It's perfect"
        "Instructors","Instructor1 Course1","Instructors","Instructor1 Course1",,"It's good","It's perfect"
        "Instructors","Instructor2 Course1","Instructors","Instructor2 Course1",,,"It's perfect"
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"What do you like best about our product?\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Team,Giver,Recipient's Team,Recipient,Feedbacks:,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[7], "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student1 In Course1\",,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student2 In Course1\",,\"It's good\",");
        assertEquals(exportLines[9], "");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Question 2,\"What do you like best about the class' product?\"");
        assertEquals(exportLines[12], "");
        assertEquals(exportLines[13], "Team,Giver,Recipient's Team,Recipient,Feedbacks:,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[14], "\"Instructors\",\"Instructor1 Course1\",\"Instructors\",\"Instructor1 Course1\",,\"It's good\",\"It's perfect\"");
        assertEquals(exportLines[15], "\"Instructors\",\"Instructor2 Course1\",\"Instructors\",\"Instructor2 Course1\",,,\"It's perfect\"");
        
        ______TS("NUMSCALE results");
        
        session = dataBundle.feedbackSessions.get("numscaleSession");
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.feedbackSessionName, session.courseId, instructor.email);
        
        System.out.println(export);
        
        /*This is how the export should look like
        =======================================
        Course,"FSQTT.idOfTypicalCourse1"
        Session Name,"NUMSCALE Session"
        
        
        Question 1,"Rate our product."
        
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Team 1.1","student1 In Course1","Team 1.1","student1 In Course1",3.5
        "Team 1.1","student2 In Course1","Team 1.1","student2 In Course1",2
        
        
        Question 2,"Rate our product."
        
        Team,Giver,Recipient's Team,Recipient,Feedback
        "Instructors","Instructor1 Course1","Instructors","Instructor1 Course1",4.5
        "Instructors","Instructor2 Course1","Instructors","Instructor2 Course1",1
        */
        
        exportLines = export.split(Const.EOL);
        assertEquals(exportLines[0], "Course,\"" + session.courseId + "\"");
        assertEquals(exportLines[1], "Session Name,\"" + session.feedbackSessionName + "\"");
        assertEquals(exportLines[2], "");
        assertEquals(exportLines[3], "");
        assertEquals(exportLines[4], "Question 1,\"Rate our product.\"");
        assertEquals(exportLines[5], "");
        assertEquals(exportLines[6], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[7], "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student1 In Course1\",3.5");
        assertEquals(exportLines[8], "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student2 In Course1\",2");
        assertEquals(exportLines[9], "");
        assertEquals(exportLines[10], "");
        assertEquals(exportLines[11], "Question 2,\"Rate our product.\"");
        assertEquals(exportLines[12], "");
        assertEquals(exportLines[13], "Team,Giver,Recipient's Team,Recipient,Feedback");
        assertEquals(exportLines[14], "\"Instructors\",\"Instructor1 Course1\",\"Instructors\",\"Instructor1 Course1\",4.5");
        assertEquals(exportLines[15], "\"Instructors\",\"Instructor2 Course1\",\"Instructors\",\"Instructor2 Course1\",1");
        
        ______TS("Non-existent Course/Session");
        
        try {
            fsLogic.getFeedbackSessionResultsSummaryAsCsv("non.existent", "no course", instructor.email);
            signalFailureToDetectException("Failed to detect non-existent feedback session.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to view non-existent feedback session.");
        }    
    }

    @Test
    public void testUpdateFeedbackSession() throws Exception {
        restoreTypicalDataInDatastore();
        
        FeedbackSessionAttributes fsa = null;
        
        ______TS("failure 1: null object");
        try {
            fsLogic.updateFeedbackSession(fsa);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains(Const.StatusCodes.NULL_PARAMETER, ae.getMessage());
        }
        
        ______TS("failure 2: non-existent session name");
        fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionName = "asdf_randomName1423";
        fsa.courseId = "idOfTypicalCourse1";
        
        try {
            fsLogic.updateFeedbackSession(fsa);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to update a feedback session that does not exist.", edne.getMessage());
        }
        
        ______TS("success 1: all changeable values sent are null");
        fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        fsa.instructions = null;
        fsa.startTime = null;
        fsa.endTime = null;
        fsa.feedbackSessionType = null;
        fsa.sessionVisibleFromTime = null;
        fsa.resultsVisibleFromTime = null;
        
        fsLogic.updateFeedbackSession(fsa);
        
        assertEquals(fsa.toString(), fsLogic.getFeedbackSession(fsa.feedbackSessionName, fsa.courseId).toString());
    }
    
    @Test
    public void testPublishUnpublishFeedbackSession() throws Exception {
        restoreTypicalDataInDatastore();
        PublishUnpublishSessionCallback.resetTaskCount();
        
        ______TS("success: publish");
        FeedbackSessionAttributes
            sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");
        
        // set as manual publish
        
        sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        fsLogic.updateFeedbackSession(sessionUnderTest);
        
        fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
        FeedbackSessionPublishedCallback.waitForTaskQueueExecution(1);
        sessionUnderTest.sentPublishedEmail = true;
        sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
        
        assertEquals(
                sessionUnderTest.toString(),
                fsLogic.getFeedbackSession(
                        sessionUnderTest.feedbackSessionName,
                        sessionUnderTest.courseId).toString());

        ______TS("failure: already published");
        
        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is already published.");
        } catch (InvalidParametersException e) {
            assertEquals("Session is already published.", e.getMessage());
        }
        
        ______TS("success: publish");
        
        fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
        
        sessionUnderTest.sentPublishedEmail = false;
        sessionUnderTest.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        
        assertEquals(
                sessionUnderTest.toString(),
                fsLogic.getFeedbackSession(
                        sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId).toString());
        
        ______TS("failure: not published");
        
        try{
            fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is not published.");
        } catch (InvalidParametersException e) {
            assertEquals("Session is already unpublished.", e.getMessage());
        }
        
        ______TS("failure: private session");
        
        sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse2");

        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session can't " +
                    "be published.");
        } catch (InvalidParametersException e) {
            assertEquals("Private session can't be published.", e.getMessage());
        }
        
        try{
            fsLogic.unpublishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session should " +
                    "not be published");
        } catch (InvalidParametersException e) {
            assertEquals("Private session can't be unpublished.", e.getMessage());
        }
                
        ______TS("failure: session does not exist");

        sessionUnderTest.feedbackSessionName = "non-existant session";
        
        try{
            fsLogic.publishFeedbackSession(
                sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to publish a non-existant session.", e.getMessage());
        }
        
        try{
            fsLogic.unpublishFeedbackSession(
                    sessionUnderTest.feedbackSessionName, sessionUnderTest.courseId);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to unpublish a non-existant session.", e.getMessage());
        }
    }
    
    @Test
    public void testIsFeedbackSessionCompletedByInstructor() throws Exception {
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");
        
        ______TS("failure: non-existent feedback session for instructor");
        
        try {
            fsLogic.isFeedbackSessionCompletedByInstructor(fs.courseId, "nonExistentFSName","random.instructor@email");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a feedback session that does not exist.",
                         edne.getMessage());
        }
        
        ______TS("success: empty session");
        
        fs = dataBundle.feedbackSessions.get("empty.session");
        
        assertTrue(fsLogic.isFeedbackSessionCompletedByInstructor(fs.feedbackSessionName, fs.courseId, instructor.email));
        
    }
    
    @Test
    public void testIsFeedbackSessionCompletedByStudent() throws Exception {
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        
        ______TS("failure: non-existent feedback session for student");
        
        try {
            fsLogic.isFeedbackSessionCompletedByStudent(fs.courseId, "nonExistentFSName","random.student@email");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a feedback session that does not exist.",
                         edne.getMessage());
        }
        
        ______TS("success: empty session");
        
        fs = dataBundle.feedbackSessions.get("empty.session");
        
        assertTrue(fsLogic.isFeedbackSessionCompletedByInstructor(fs.feedbackSessionName, fs.courseId, student.email));
        
    }
    
    @Test
    public void testSendReminderForFeedbackSession() throws Exception {
        // private method. no need to check for authentication.
        Logic logic = new Logic();
        
        restoreTypicalDataInDatastore();
        DataBundle dataBundle = getTypicalDataBundle();
        
        ______TS("typical success case");
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");

        List<MimeMessage> emailsSent = 
                fsLogic.sendReminderForFeedbackSession(fs.courseId, fs.feedbackSessionName);
        assertEquals(7, emailsSent.size());

        List<StudentAttributes> studentList = logic.getStudentsForCourse(fs.courseId);
        for (StudentAttributes s : studentList) {
            MimeMessage emailToStudent = LogicTest.getEmailToStudent(s, emailsSent);
            
            if(fsLogic.isFeedbackSessionCompletedByStudent(fs.feedbackSessionName, fs.courseId, s.email)) {
                String errorMessage = "Email sent to " + s.email + " when he already completed the session.";
                assertNull(errorMessage, emailToStudent);
            } else {
                String errorMessage = "No email sent to " + s.email + " when he hasn't completed the session.";
                assertNotNull(errorMessage, emailToStudent);
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailToStudent.getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailToStudent.getSubject());
            }
        }
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(fs.courseId);
        String notificationHeader = "The email below has been sent to students of course: " + fs.courseId;
        for (InstructorAttributes i : instructorList) {
            List<MimeMessage> emailsToInstructor = LogicTest.getEmailsToInstructor(i, emailsSent);
            
            if(fsLogic.isFeedbackSessionCompletedByInstructor(fs.feedbackSessionName, fs.courseId, i.email)) {
                // Only send notification (no reminder) if instructor already completed the session
                assertEquals(1, emailsToInstructor.size());
                AssertHelper.assertContains(notificationHeader, emailsToInstructor.get(0).getContent().toString());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
            } else {
                // Send both notification and reminder if the instructor hasn't completed the session
                assertEquals(2, emailsToInstructor.size());
                
                assertTrue(emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                assertTrue(!emailsToInstructor.get(0).getContent().toString().contains(notificationHeader) 
                            || !emailsToInstructor.get(1).getContent().toString().contains(notificationHeader));
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(0).getSubject());
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER,
                        emailsToInstructor.get(1).getSubject());
                AssertHelper.assertContains(fs.feedbackSessionName, emailsToInstructor.get(1).getSubject());
            }
            
            
        }
        
        ______TS("failure: non-existent Feedback session");
        
        String nonExistentFSName = "non-ExIsTENT FsnaMe123";
        
        try {
            fsLogic.sendReminderForFeedbackSession(fs.courseId, nonExistentFSName);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to remind non-existent feedback session " 
                            + fs.courseId + "/" + nonExistentFSName,
                         edne.getMessage());
        }
        
    }
    
    private FeedbackSessionAttributes getNewFeedbackSession() {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionType = FeedbackSessionType.STANDARD;
        fsa.feedbackSessionName = "fsTest1";
        fsa.courseId = "testCourse";
        fsa.creatorEmail = "valid@email.com";
        fsa.createdTime = new Date();
        fsa.startTime = new Date();
        fsa.endTime = new Date();
        fsa.sessionVisibleFromTime = new Date();
        fsa.resultsVisibleFromTime = new Date();
        fsa.gracePeriod = 5;
        fsa.sentOpenEmail = true;
        fsa.instructions = new Text("Give feedback.");
        return fsa;
    }
    
    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(
                questionToGet.feedbackSessionName, 
                questionToGet.courseId,
                questionToGet.questionNumber);
        
        return questionToGet;
    }

    // Extract response id from datastore based on json key.
    private String getResponseId(String jsonId, DataBundle bundle) {
        return getResponseFromDatastore(jsonId, bundle).getId();
    }
    
    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId, DataBundle bundle) {
        FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);
        
        String questionId = null;        
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);        
            questionId = fqLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            questionId = response.feedbackQuestionId;
        }
        
        return frLogic.getFeedbackResponse(questionId, 
                response.giverEmail, response.recipientEmail);
    }
    
    private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if(fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs.feedbackSessionName, fs.courseId);                
            }
        }
    }
    
    // Stringifies the visibility table for easy testing/comparison.
    private String tableToString(Map<String, boolean[]> table){
        String tableString = "";
        for(Map.Entry<String, boolean[]> entry : table.entrySet()) {
            tableString += "{";
            tableString += entry.getKey().toString();
            tableString += "={";
            tableString += String.valueOf(entry.getValue()[0]);
            tableString += ",";
            tableString += String.valueOf(entry.getValue()[1]);
            tableString += "}},";
        }
        if(!tableString.isEmpty()) {
            tableString = tableString.substring(0, tableString.length()-1);
        }
        return tableString;
    }
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(FeedbackSessionsLogic.class);
    }
}
