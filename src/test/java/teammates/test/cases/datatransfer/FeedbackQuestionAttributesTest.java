package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionAttributes}.
 */
public class FeedbackQuestionAttributesTest extends BaseTestCase {

    private DataBundle typicalBundle = getTypicalDataBundle();

    private static class FeedbackQuestionAttributesWithModifiableTimestamp extends FeedbackQuestionAttributes {

        void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @Test
    public void testBuilderWithDefaultValues() {
        FeedbackQuestionAttributes observedFeedbackQuestionAttributes =
                FeedbackQuestionAttributes.builder().buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(0, observedFeedbackQuestionAttributes.questionNumber);
        assertNull(observedFeedbackQuestionAttributes.recipientType);
        assertNull(observedFeedbackQuestionAttributes.giverType);
        assertNull(observedFeedbackQuestionAttributes.courseId);
        assertNull(observedFeedbackQuestionAttributes.feedbackSessionName);
        assertNull(observedFeedbackQuestionAttributes.showResponsesTo);
        assertNull(observedFeedbackQuestionAttributes.showGiverNameTo);
        assertNull(observedFeedbackQuestionAttributes.showRecipientNameTo);
        assertNull(observedFeedbackQuestionAttributes.questionDescription);
        assertNull(observedFeedbackQuestionAttributes.questionType);
        assertNull(observedFeedbackQuestionAttributes.questionMetaData);
        assertNull(observedFeedbackQuestionAttributes.creatorEmail);
        assertEquals(0, observedFeedbackQuestionAttributes.numberOfEntitiesToGiveFeedbackTo);
    }

    @Test
    public void testBuilderCopy() {
        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        List<FeedbackParticipantType> participantsForShowResponseTo = new ArrayList<>(participants);
        participantsForShowResponseTo.add(FeedbackParticipantType.STUDENTS);

        FeedbackQuestionAttributes original = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("test session")
                .withCourseId("some course")
                .withCreatorEmail("test@case.com")
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionNumber(1)
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponseTo(new ArrayList<>(participantsForShowResponseTo))
                .build();

        FeedbackQuestionAttributes copy = original.getCopy();

        assertNotSame(copy, original);
        assertEquals(copy.getFeedbackSessionName(), original.getFeedbackSessionName());
        assertEquals(copy.getCourseId(), original.getCourseId());
        assertEquals(copy.getCreatorEmail(), original.getCreatorEmail());
        assertEquals(copy.getQuestionMetaData(), original.getQuestionMetaData());
        assertEquals(copy.getQuestionNumber(), original.getQuestionNumber());
        assertEquals(copy.getQuestionType(), original.getQuestionType());
        assertEquals(copy.getGiverType(), original.getGiverType());
        assertEquals(copy.getRecipientType(), original.getRecipientType());
        assertEquals(copy.getShowGiverNameTo(), original.getShowGiverNameTo());
        assertEquals(copy.getShowRecipientNameTo(), original.getShowRecipientNameTo());
        assertEquals(copy.getShowResponsesTo(), copy.getShowResponsesTo());
    }

    @Test
    public void testDefaultTimestamp() {

        FeedbackQuestionAttributesWithModifiableTimestamp fq =
                new FeedbackQuestionAttributesWithModifiableTimestamp();

        fq.setCreatedAt(null);
        fq.setUpdatedAt(null);

        Instant defaultTimeStamp = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;

        ______TS("success : defaultTimeStamp for createdAt date");

        assertEquals(defaultTimeStamp, fq.getCreatedAt());

        ______TS("success : defaultTimeStamp for updatedAt date");

        assertEquals(defaultTimeStamp, fq.getUpdatedAt());
    }

    @Test
    public void testValidate() throws Exception {

        List<FeedbackParticipantType> showGiverNameToList = new ArrayList<>();
        showGiverNameToList.add(FeedbackParticipantType.SELF);
        showGiverNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showRecipientNameToList = new ArrayList<>();
        showRecipientNameToList.add(FeedbackParticipantType.SELF);
        showRecipientNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showResponseToList = new ArrayList<>();
        showResponseToList.add(FeedbackParticipantType.NONE);
        showResponseToList.add(FeedbackParticipantType.SELF);

        FeedbackQuestionAttributes fq = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("")
                .withCourseId("")
                .withCreatorEmail("")
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.NONE)
                .withRecipientType(FeedbackParticipantType.RECEIVER)
                .withShowGiverNameTo(new ArrayList<>(showGiverNameToList))
                .withShowRecipientNameTo(new ArrayList<>(showRecipientNameToList))
                .withShowResponseTo(new ArrayList<>(showResponseToList))
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertFalse(fq.isValid());

        String errorMessage = getPopulatedEmptyStringErrorMessage(
                                  FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                                  FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                                  FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH) + System.lineSeparator()
                              + getPopulatedEmptyStringErrorMessage(
                                    FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH)
                              + System.lineSeparator()
                              + "Invalid creator's email: "
                              + getPopulatedEmptyStringErrorMessage(
                                    FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH)
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.giverType.toString(),
                                              FieldValidator.GIVER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.recipientType.toString(),
                                              FieldValidator.RECIPIENT_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showGiverNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show giver name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showRecipientNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show recipient name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(1).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.feedbackSessionName = "First Feedback Session";
        fq.courseId = "CS1101";
        fq.creatorEmail = "instructor1@course1.com";
        fq.giverType = FeedbackParticipantType.TEAMS;
        fq.recipientType = FeedbackParticipantType.OWN_TEAM;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     fq.recipientType.toDisplayRecipientName(),
                                     fq.giverType.toDisplayGiverName()) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     fq.recipientType.toDisplayRecipientName(),
                                     fq.giverType.toDisplayGiverName()) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.TEAMS;

        fq.showGiverNameTo = new ArrayList<>();
        fq.showGiverNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showRecipientNameTo = new ArrayList<>();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showResponsesTo = new ArrayList<>();
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);

        assertTrue(fq.isValid());
    }

    @Test
    public void testGetQuestionDetails() {

        ______TS("Text question: new Json format");

        FeedbackQuestionAttributes fq = typicalBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("New format text question");
        fq.setQuestionDetails(questionDetails);

        assertTrue(fq.isValid());
        assertEquals(fq.getQuestionDetails().getQuestionText(), "New format text question");

        ______TS("Text question: old string format");

        fq = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertEquals(fq.getQuestionDetails().getQuestionText(), "Rate 1 other student's product");
    }

    @Test
    public void testRemoveIrrelevantVisibilityOptions() {

        ______TS("test teams->none");

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        List<FeedbackParticipantType> participantsForShowResponseTo = new ArrayList<>(participants);
        participantsForShowResponseTo.add(FeedbackParticipantType.STUDENTS);

        FeedbackQuestionAttributes question = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("test session")
                .withCourseId("some course")
                .withCreatorEmail("test@case.com")
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionNumber(1)
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponseTo(new ArrayList<>(participantsForShowResponseTo))
                .build();

        assertTrue(question.showGiverNameTo.isEmpty());
        assertTrue(question.showRecipientNameTo.isEmpty());
        // check that other types are not removed
        assertTrue(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS));
        assertEquals(question.showResponsesTo.size(), 1);

        ______TS("test students->teams");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.TEAMS;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 2);
        assertEquals(question.showRecipientNameTo.size(), 3);
        assertEquals(question.showResponsesTo.size(), 3);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->team members including giver");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 3);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));

        ______TS("test students->instructors");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.INSTRUCTORS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team members");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
    }

}
