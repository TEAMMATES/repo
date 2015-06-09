package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResponseCommentsLoadAction extends Action {

    private static final Boolean IS_INCLUDE_RESPONSE_STATUS = true;
    private InstructorAttributes instructor = null;
    private InstructorFeedbackResponseCommentsLoadPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String fsname = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String fsindexString = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INDEX);
        Assumption.assertNotNull(fsindexString);
        int fsindex = 0;
        try {
            fsindex = Integer.parseInt(fsindexString);
        } catch (NumberFormatException e) {
            Assumption.fail("Invalid request parameter value for feedback session index: " + fsindexString);
        }
        Assumption.assertNotNull(fsname);
        
        instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        
        CourseRoster roster = new CourseRoster(logic.getStudentsForCourse(courseId),
                                               logic.getInstructorsForCourse(courseId));
        
        data = new InstructorFeedbackResponseCommentsLoadPageData(account);
        data.feedbackResultBundles = getFeedbackResultBundles(courseId, fsname, roster);
        data.instructorEmail = instructor.email;
        data.currentInstructor = instructor;
        data.roster = roster;
        data.feedbackSessionIndex = fsindex;
        data.numberOfPendingComments = logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size() 
                + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENTS_LOAD, data);
    }

    private Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles(String courseId, String fsname,
            CourseRoster roster) throws EntityDoesNotExistException {
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles =
                new HashMap<String, FeedbackSessionResultsBundle>();
        FeedbackSessionResultsBundle bundle = 
                logic.getFeedbackSessionResultsForInstructor(
                        fsname, courseId, instructor.email, roster, !IS_INCLUDE_RESPONSE_STATUS);
        if (bundle != null) {
            removeQuestionsAndResponsesIfNotAllowed(bundle);
            removeQuestionsAndResponsesWithoutFeedbackResponseComment(bundle);
            if (bundle.questions.size() != 0) {
                feedbackResultBundles.put(fsname, bundle);
            }
        }
        return feedbackResultBundles;
    }

    private void removeQuestionsAndResponsesIfNotAllowed(FeedbackSessionResultsBundle bundle) {
        Iterator<FeedbackResponseAttributes> iter = bundle.responses.iterator();
        while (iter.hasNext()) {
            FeedbackResponseAttributes fdr = iter.next();
            boolean canInstructorViewSessionInGiverSection = 
                    instructor.isAllowedForPrivilege(fdr.giverSection, fdr.feedbackSessionName,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
            boolean canInstructorViewSessionInRecipientSection =
                    instructor.isAllowedForPrivilege(fdr.recipientSection, fdr.feedbackSessionName,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
            
            boolean instructorHasSessionViewingPrivileges = (canInstructorViewSessionInGiverSection
                                                                    && canInstructorViewSessionInRecipientSection);
            if (!instructorHasSessionViewingPrivileges) {
                iter.remove();
            }
        }
    }

    private void removeQuestionsAndResponsesWithoutFeedbackResponseComment(FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responsesWithFeedbackResponseComment =
                new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes fr : bundle.responses) {
            List<FeedbackResponseCommentAttributes> frComment = bundle.responseComments.get(fr.getId());
            if (frComment != null && frComment.size() != 0) {
                responsesWithFeedbackResponseComment.add(fr);
            }
        }
        Map<String, FeedbackQuestionAttributes> questionsWithFeedbackResponseComment =
                new HashMap<String, FeedbackQuestionAttributes>();
        for (FeedbackResponseAttributes fr: responsesWithFeedbackResponseComment) {
            FeedbackQuestionAttributes qn = bundle.questions.get(fr.feedbackQuestionId);
            if (questionsWithFeedbackResponseComment.get(qn.getId()) == null) {
                questionsWithFeedbackResponseComment.put(qn.getId(), qn);
            }
        }
        bundle.questions = questionsWithFeedbackResponseComment;
        bundle.responses = responsesWithFeedbackResponseComment;
    }
}
