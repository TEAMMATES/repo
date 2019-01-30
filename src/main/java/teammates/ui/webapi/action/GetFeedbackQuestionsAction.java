package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.FeedbackQuestions;

/**
 * Get a list of feedback questions for a feedback session.
 */
public class GetFeedbackQuestionsAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()), feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        List<FeedbackQuestionAttributes> questions;
        try {
            switch (intent) {
            case STUDENT_SUBMISSION:
                questions = logic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
                break;
            case INSTRUCTOR_SUBMISSION:
                InstructorAttributes instructor = getInstructorOfCourseFromRequest(courseId);
                questions = logic.getFeedbackQuestionsForInstructors(feedbackSessionName, courseId, instructor.getEmail());
                break;
            case FULL_DETAIL:
                questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        if (!StringHelper.isEmpty(moderatedPerson)) {
            // filter out unmodifiable questions
            questions.removeIf(question -> !canInstructorSeeQuestion(question));
        }

        FeedbackQuestions response = new FeedbackQuestions(questions);
        response.normalizeQuestionNumber();
        return new JsonResult(response);
    }

}
