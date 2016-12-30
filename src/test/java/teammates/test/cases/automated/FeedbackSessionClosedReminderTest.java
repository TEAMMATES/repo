package teammates.test.cases.automated;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class FeedbackSessionClosedReminderTest extends BaseComponentUsingTaskQueueTestCase {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @SuppressWarnings("serial")
    public static class FeedbackSessionClosedCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            
            EmailType typeOfMail = EmailType.valueOf(paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.FEEDBACK_CLOSED, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));
            
            FeedbackSessionClosedCallback.taskCount++;
            return TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionClosedCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
    @Test(enabled = false)
    public void testAdditionOfTaskToTaskQueue() throws Exception {
        FeedbackSessionClosedCallback.resetTaskCount();
        
        ______TS("typical case, 0 sessions closed");
        //fsLogic.scheduleFeedbackSessionClosedEmails();
        if (!FeedbackSessionClosedCallback.verifyTaskCount(0)) {
            assertEquals(FeedbackSessionClosedCallback.taskCount, 0);
        }
        
        ______TS("typical case, two sessions closed, 1 session closed with disabled closing reminder.");
        // Modify session to closed.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");

        session1.setTimeZone(0);
        session1.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session1.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);
       
        // Reuse an existing session to create a new one that is closed.
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse2");
        
        session2.setTimeZone(0);
        session2.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session2.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);
        
        // Reuse an existing session to create a new one that is closed and closing reminder disabled.
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("session2InCourse1");
        
        session3.setTimeZone(0);
        session3.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session3.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        session3.setClosingEmailEnabled(false);
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);
        
        int counter = 0;
        while (counter != 10) {
            FeedbackSessionClosedCallback.resetTaskCount();
            //fsLogic.scheduleFeedbackSessionClosedEmails();
            //There are only 1 sessions closed reminder to be sent
            if (FeedbackSessionClosedCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }
        if (counter == 10) {
            assertEquals(FeedbackSessionClosedCallback.taskCount, 1);
        }
    }

    @Test(enabled = false)
    public void testFeedbackSessionClosedMailAction() throws Exception {
        
        ______TS("typical case, testing mime messages");
        // Modify session to closed.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setTimeZone(0);
        session1.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session1.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        fsLogic.updateFeedbackSession(session1);
        
        // Reuse an existing session to create a new one that is closed
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse2");
        session2.setTimeZone(0);
        session2.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session2.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);
        
        // Reuse an existing session to create a new one that is closed and closing reminder disabled.
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("session2InCourse1");
        
        session3.setTimeZone(0);
        session3.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session3.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        session3.setClosingEmailEnabled(false);
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);
        
    }
    
}
