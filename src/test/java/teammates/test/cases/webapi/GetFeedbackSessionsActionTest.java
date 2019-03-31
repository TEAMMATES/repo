package teammates.test.cases.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackSessionsAction;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.FeedbackSessionPublishStatus;
import teammates.ui.webapi.output.FeedbackSessionSubmissionStatus;
import teammates.ui.webapi.output.FeedbackSessionsData;
import teammates.ui.webapi.output.ResponseVisibleSetting;
import teammates.ui.webapi.output.SessionVisibleSetting;

/**
 * SUT: {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction> {

    private List<FeedbackSessionAttributes> sessionsInCourse1;
    private List<FeedbackSessionAttributes> sessionsInCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        sessionsInCourse1 = new ArrayList<>();
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("session2InCourse1"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("gracePeriodSession"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("closedSession"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("empty.session"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("awaiting.session"));

        sessionsInCourse2 = new ArrayList<>();
        sessionsInCourse2.add(typicalBundle.feedbackSessions.get("session1InCourse2"));
        sessionsInCourse2.add(typicalBundle.feedbackSessions.get("session2InCourse2"));

        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1InCourse1.setDeletedTime(Instant.now());

        // Make student2InCourse2 and instructor1OfCourse1 belong to the same account.
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student2InCourse2.googleId = instructor1OfCourse1.getGoogleId();

        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor2OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(1, fsData.getFeedbackSessions().size());
        FeedbackSessionData fs = fsData.getFeedbackSessions().get(0);
        assertAllInformationMatch(fs, session1InCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_instructorAsStudent_shouldReturnAllSessionsForStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(2, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2);
    }

    @Test
    protected void testExecute_instructorAsStudentWithCourseId_shouldReturnAllSessionsForCourseOfStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2);
    }

    @Test
    protected void testExecute_instructorAsStudentWithInvalidCourseId_shouldReturnEmptyList() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(0, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asStudentWithCourseId_shouldReturnAllSessionsForCourse() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1);

    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_noEntityType_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsStudent(student1InCourse1.googleId);

        ______TS("student can access");
        String[] studentEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(studentEntityParam);

        ______TS("student of the same course can access");
        loginAsStudent(student1InCourse2.googleId);
        String[] courseParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(courseParam);

        ______TS("Student of another course cannot access");
        loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(courseParam);

        ______TS("instructor can access");
        loginAsInstructor(instructor1OfCourse2.googleId);

        String[] instructorParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(instructorParam);

        ______TS("instructor of the same course can access");
        String[] instructorAndCourseIdParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyCanAccess(instructorAndCourseIdParam);

        ______TS("instructor of another course cannot access");
        loginAsInstructor(instructor2OfCourse1.googleId);
        verifyCannotAccess(instructorAndCourseIdParam);

        ______TS("instructor as student can access");
        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(studentEntityParam);

        ______TS("instructor as student can access for course");
        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(courseParam);

        String[] adminEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyInaccessibleForAdmin(adminEntityParam);
        verifyInaccessibleForUnregisteredUsers(studentEntityParam);
        verifyInaccessibleWithoutLogin();
    }

    private void assertInformationHiddenForStudent(FeedbackSessionData data) {
        assertNull(data.getGracePeriod());
        assertNull(data.getSessionVisibleSetting());
        assertNull(data.getCustomSessionVisibleTimestamp());
        assertNull(data.getResponseVisibleSetting());
        assertNull(data.getCustomResponseVisibleTimestamp());
        assertNull(data.getPublishStatus());
        assertNull(data.getIsClosingEmailEnabled());
        assertNull(data.getIsPublishedEmailEnabled());
    }

    private void assertPartialInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(expectedSession.getTimeZone().getId(), data.getTimeZone());
        assertEquals(expectedSession.getFeedbackSessionName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(expectedSession.getStartTime().toEpochMilli(), data.getSubmissionStartTimestamp());
        assertEquals(expectedSession.getEndTime().toEpochMilli(), data.getSubmissionEndTimestamp());

        if (!expectedSession.isVisible()) {
            assertEquals(FeedbackSessionSubmissionStatus.NOT_VISIBLE, data.getSubmissionStatus());
        } else if (expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.OPEN, data.getSubmissionStatus());
        } else if (expectedSession.isClosed()) {
            assertEquals(FeedbackSessionSubmissionStatus.CLOSED, data.getSubmissionStatus());
        } else if (expectedSession.isInGracePeriod()) {
            assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, data.getSubmissionStatus());
        } else if (expectedSession.isVisible() && !expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, data.getSubmissionStatus());
        }

        assertEquals(expectedSession.getCreatedTime().toEpochMilli(), data.getCreatedAtTimestamp());
        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }
    }

    private void assertAllInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(expectedSession.getTimeZone().getId(), data.getTimeZone());
        assertEquals(expectedSession.getFeedbackSessionName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(expectedSession.getStartTime().toEpochMilli(), data.getSubmissionStartTimestamp());
        assertEquals(expectedSession.getEndTime().toEpochMilli(), data.getSubmissionEndTimestamp());
        assertEquals(expectedSession.getGracePeriodMinutes(), data.getGracePeriod().longValue());

        Instant sessionVisibleTime = expectedSession.getSessionVisibleFromTime();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.AT_OPEN);
        } else {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.CUSTOM);
            assertEquals(sessionVisibleTime.toEpochMilli(), data.getCustomSessionVisibleTimestamp().longValue());
        }

        Instant responseVisibleTime = expectedSession.getResultsVisibleFromTime();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            assertEquals(ResponseVisibleSetting.AT_VISIBLE, data.getResponseVisibleSetting());
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertEquals(ResponseVisibleSetting.LATER, data.getResponseVisibleSetting());
        } else {
            assertEquals(ResponseVisibleSetting.CUSTOM, data.getResponseVisibleSetting());
            assertEquals(responseVisibleTime.toEpochMilli(), data.getCustomResponseVisibleTimestamp().longValue());
        }

        if (!expectedSession.isVisible()) {
            assertEquals(FeedbackSessionSubmissionStatus.NOT_VISIBLE, data.getSubmissionStatus());
        } else if (expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.OPEN, data.getSubmissionStatus());
        } else if (expectedSession.isClosed()) {
            assertEquals(FeedbackSessionSubmissionStatus.CLOSED, data.getSubmissionStatus());
        } else if (expectedSession.isInGracePeriod()) {
            assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, data.getSubmissionStatus());
        } else if (expectedSession.isVisible() && !expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, data.getSubmissionStatus());
        }

        if (expectedSession.isPublished()) {
            assertEquals(FeedbackSessionPublishStatus.PUBLISHED, data.getPublishStatus());
        } else {
            assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, data.getPublishStatus());
        }

        assertEquals(expectedSession.isClosingEmailEnabled(), data.getIsClosingEmailEnabled());
        assertEquals(expectedSession.isPublishedEmailEnabled(), data.getIsPublishedEmailEnabled());

        assertEquals(expectedSession.getCreatedTime().toEpochMilli(), data.getCreatedAtTimestamp());
        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }
    }

    private void assertAllInstructorSessionsMatch(FeedbackSessionsData sessionsData,
                                                  List<FeedbackSessionAttributes> expectedSessions) {

        assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSessionAttributes> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getFeedbackSessionName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            assertAllInformationMatch(sessionData, matchedSessions.get(0));
        }
    }

    private void assertAllStudentSessionsMatch(FeedbackSessionsData sessionsData,
                                               List<FeedbackSessionAttributes> expectedSessions) {

        assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSessionAttributes> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getFeedbackSessionName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            assertPartialInformationMatch(sessionData, matchedSessions.get(0));
            assertInformationHiddenForStudent(sessionData);
        }
    }
}
