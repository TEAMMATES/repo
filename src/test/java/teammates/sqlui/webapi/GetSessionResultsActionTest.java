package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static teammates.ui.request.Intent.FULL_DETAIL;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetSessionResultsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {
    String googleId = "user-googleId";
    Course course;

    FeedbackSession session;
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "session-name",
                course,
                "creater_email@tm.tmt",
                null,
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-10-01T00:00:00.000Z"),
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-11-01T00:00:00.000Z"),
                null,
                false,
                false,
                false);
        loginAsInstructor(googleId);
    }

    SqlSessionResultsBundle prepareMocks(Intent intent) {
        Instructor instructorStub = getTypicalInstructor();
        List<FeedbackQuestion> questionsStub = new ArrayList<>();
        questionsStub.add(new FeedbackMcqQuestion(session,1, "description",
                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails()));
        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));

        // Common mocked methods
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);

        // Specific mocked methods
        switch(intent) {
        case FULL_DETAIL:
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForCourse(any(), any(), any(), any(), any(), any())).thenReturn(resultsStub);
        case INSTRUCTOR_RESULT:
            when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForUser(any(FeedbackSession.class), any(String.class), any(String.class),
                    any(Boolean.class), nullable(UUID.class), anyBoolean())).thenReturn(resultsStub);
        case STUDENT_RESULT:
            Student studentStub = getTypicalStudent();
            when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
            when(mockLogic.getSessionResultsForUser(any(FeedbackSession.class), any(String.class), any(String.class),
                    any(Boolean.class), nullable(UUID.class), anyBoolean())).thenReturn(resultsStub);
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:

        }
        return resultsStub;

    }

    @Test
    void testExecute_fullDetailIntent_success() {


//        questionsStub.add(new FeedbackMcqQuestion(session,1, "description",
//                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails()));
//        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
//                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
//                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
//                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));

        SqlSessionResultsBundle resultsStub = prepareMocks(FULL_DETAIL);

//        when(mockLogic.getCourse(course.getId())).thenReturn(course);
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
//        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
//        when(mockLogic.getSessionResultsForCourse(any(), any(), any(), any(), any(), any())).thenReturn(resultsStub);

       String[] params = {
               Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
               Const.ParamsNames.COURSE_ID, session.getCourseId(),
               Const.ParamsNames.INTENT, FULL_DETAIL.name(),
       };
         GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();

        assertEquals(resultsStub.getQuestions().size(), output.getQuestions().size());
        assertEquals(resultsStub.getQuestions().get(0).getDescription(),
                output.getQuestions().get(0).getFeedbackQuestion().getQuestionDescription());
    }

    @Test
    void testExecute_instructorResultIntent_success() {
//        Instructor instructorStub = getTypicalInstructor();
//        List<FeedbackQuestion> questionsStub = new ArrayList<>();
//        questionsStub.add(new FeedbackMcqQuestion(session, 1, "description",
//                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails()));
//        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
//                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
//                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
//                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));
//        when(mockLogic.getCourse(course.getId())).thenReturn(course);
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
//        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
//        when(mockLogic.getSessionResultsForUser(any(FeedbackSession.class), any(String.class), any(String.class),
//                any(Boolean.class), nullable(UUID.class), anyBoolean())).thenReturn(resultsStub);
        SqlSessionResultsBundle resultsStub = prepareMocks(Intent.INSTRUCTOR_RESULT);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();

        assertEquals(resultsStub.getQuestions().size(), output.getQuestions().size());
        assertEquals(resultsStub.getQuestions().get(0).getDescription(),
                output.getQuestions().get(0).getFeedbackQuestion().getQuestionDescription());
    }

    @Test
    void testExecute_studentResultIntent_success() {
//        Student studentStub = getTypicalStudent();
//        List<FeedbackQuestion> questionsStub = new ArrayList<>();
//        questionsStub.add(new FeedbackMcqQuestion(session, 1, "description",
//                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails()));
//        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
//                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
//                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
//                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));
//        when(mockLogic.getCourse(course.getId())).thenReturn(course);
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
//        when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
//        when(mockLogic.getSessionResultsForUser(any(FeedbackSession.class), any(String.class), any(String.class),
//                any(Boolean.class), nullable(UUID.class), anyBoolean())).thenReturn(resultsStub);
        SqlSessionResultsBundle resultsStub = prepareMocks(Intent.STUDENT_RESULT);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();

        assertEquals(resultsStub.getQuestions().size(), output.getQuestions().size());
        assertEquals(resultsStub.getQuestions().get(0).getDescription(),
                output.getQuestions().get(0).getFeedbackQuestion().getQuestionDescription());
    }

    @Test
    void testExecute_instructorSubmissionIntent_throwsInvalidHttpParameterException() {
//        when(mockLogic.getCourse(course.getId())).thenReturn(course);
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        prepareMocks(Intent.INSTRUCTOR_SUBMISSION);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_studentSubmissionIntent_throwsInvalidHttpParameterException() {
//        when(mockLogic.getCourse(course.getId())).thenReturn(course);
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        prepareMocks(Intent.STUDENT_SUBMISSION);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nonNullFeedbackQuestionIdFullDetailIntent_success() {
        Instructor instructorStub = getTypicalInstructor();
        List<FeedbackQuestion> questionsStub = new ArrayList<>();
        FeedbackQuestion questionStub = new FeedbackMcqQuestion(session,1, "description",
                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails());
        questionsStub.add(questionStub);
        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));


        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getSessionResultsForCourse(any(), any(), any(), any(), any(), any())).thenReturn(resultsStub);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionStub.getId().toString(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();

        assertEquals(resultsStub.getQuestions().size(), output.getQuestions().size());
        assertEquals(resultsStub.getQuestions().get(0).getDescription(),
                output.getQuestions().get(0).getFeedbackQuestion().getQuestionDescription());

    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_withAllParametersAndFullDetailIntent_success() {
        Instructor instructorStub = getTypicalInstructor();
        List<FeedbackQuestion> questionsStub = new ArrayList<>();
        FeedbackQuestion questionStub = new FeedbackMcqQuestion(session, 1, "description",
                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.OWN_TEAM, 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new FeedbackMcqQuestionDetails());
        questionsStub.add(questionStub);
        SqlSessionResultsBundle resultsStub = new SqlSessionResultsBundle(questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getInstructorForEmail(course.getId(), instructorStub.getEmail())).thenReturn(instructorStub);
        when(mockLogic.getSessionResultsForCourse(any(FeedbackSession.class), any(String.class), any(String.class),
                any(UUID.class), any(String.class), any(FeedbackResultFetchType.class))).thenReturn(resultsStub);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionStub.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "sectionName",
                Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER, FeedbackResultFetchType.BOTH.name(),
                Const.ParamsNames.PREVIEWAS, instructorStub.getEmail(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();

        assertEquals(resultsStub.getQuestions().size(), output.getQuestions().size());
        assertEquals(resultsStub.getQuestions().get(0).getDescription(),
                output.getQuestions().get(0).getFeedbackQuestion().getQuestionDescription());

    }


    @Test
    void testCheckSpecificAccessControlSql() {
        String[] params ={
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(null);
        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_validInstructorAndSessionPublished_canAccess() {
        Instructor instructor = getTypicalInstructor();
        String[] params ={
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, instructor.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(session.getCourseId(), instructor.getEmail())).thenReturn(instructor);
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_validInstructorAndNoPreviewResultsAndSessionNotPublished_canAccess() {
        Instructor instructor = getTypicalInstructor();
        FeedbackSession unpublishedSession = new FeedbackSession(
                "session-name",
                course,
                "creater_email@tm.tmt",
                null,
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-10-01T00:00:00.000Z"),
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.MAX,
                null,
                false,
                false,
                false);
        String[] params ={
                Const.ParamsNames.FEEDBACK_SESSION_NAME, unpublishedSession.getName(),
                Const.ParamsNames.COURSE_ID, unpublishedSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(unpublishedSession.getName(), unpublishedSession.getCourseId())).thenReturn(unpublishedSession);
        when(mockLogic.getInstructorByGoogleId(unpublishedSession.getCourseId(), googleId)).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(unpublishedSession.getCourseId(), instructor.getEmail())).thenReturn(instructor);
        verifyCannotAccess(params);
    }

//    @Test
//    void testCheckSpecificAccessControl_validStudentAndSessionPublished_canAccess() {
//        Student student = getTypicalStudent();
//        String[] params ={
//                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
//                Const.ParamsNames.COURSE_ID, session.getCourseId(),
//                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
//                Const.ParamsNames.PREVIEWAS, student.getEmail(),
//        };
//        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
//        when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(student);
//        verifyCanAccess(params);
//    }



}
