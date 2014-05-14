package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResponseCommentAjaxPageData;
import teammates.ui.controller.InstructorFeedbackResponseCommentDeleteAction;

public class InstructorFeedbackResponseCommentDeleteActionTest extends
        BaseActionTest {

    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "1",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
    
    @Test
    public void testExcecuteAndPostProcess() throws Exception {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);
        
        String giverEmail = "student1InCourse1@gmail.com";
        String receiverEmail = "student1InCourse1@gmail.com";
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);
        
        FeedbackResponseCommentAttributes feedbackResponseComment = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");
        
        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("Unsuccessful case: not enough parameters");
        
        verifyAssumptionFailure();
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.USER_ID, instructor.googleId
        };
        
        verifyAssumptionFailure(submissionParams);
        
        ______TS("Typical successful case");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        
        InstructorFeedbackResponseCommentDeleteAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();
        
        InstructorFeedbackResponseCommentAjaxPageData data = 
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertNull(feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseComment.feedbackResponseId,
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt));
    }
    
    private InstructorFeedbackResponseCommentDeleteAction getAction(String... params) throws Exception {
        return (InstructorFeedbackResponseCommentDeleteAction) (gaeSimulation.getActionObject(uri, params));
    }
}
