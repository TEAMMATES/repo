package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicTest extends BaseLogicTest {

    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Test
    public void allTests() throws Exception {
        testGetRecipientsForQuestion();
        testGetFeedbackQuestionsForInstructor();
        testGetFeedbackQuestionsForStudents();
        testGetFeedbackQuestionsForStudent();
        testGetFeedbackQuestionsWithCustomFeedbackPaths();
        testIsQuestionHasResponses();
        testIsQuestionAnswered();
        testUpdateQuestionNumber();
        testAddQuestion();
        testCopyQuestion();
        testUpdateQuestion();
        testDeleteQuestion();
        testAddQuestionNoIntegrityCheck();
        testDeleteQuestionsForCourse();
    }

    private void testGetRecipientsForQuestion() throws Exception {
        FeedbackQuestionAttributes question;
        String email;
        Map<String, String> recipients;

        ______TS("response to students, total 6");

        question = getQuestionFromDatastore("qn2InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 5); // 6 students minus giver himself

        email = dataBundle.instructors.get("instructor1OfCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 6); // instructor is not student so he can respond to all 6.

        ______TS("response to instructors, total 3");

        question = getQuestionFromDatastore("qn2InSession2InCourse2");
        email = dataBundle.instructors.get("instructor1OfCourse2").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 2); // 3 - giver = 2

        ______TS("empty case: response to team members, but alone");

        question = getQuestionFromDatastore("team.members.feedback");
        email = dataBundle.students.get("student5InCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 0);

        ______TS("response from team to itself");

        question = getQuestionFromDatastore("graceperiod.session.feedbackFromTeamToSelf");
        email = dataBundle.students.get("student1InCourse1").email;
        String teamName = dataBundle.students.get("student1InCourse1").team;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 1);
        assertTrue(recipients.containsKey(teamName));
        assertEquals(recipients.get(teamName), teamName);

        ______TS("special case: response to other teams, instructor is also student");
        question = getQuestionFromDatastore("team.feedback");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);

        assertEquals(recipients.size(), 2);

        ______TS("to nobody (general feedback)");
        question = getQuestionFromDatastore("qn3InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.get(Const.GENERAL_QUESTION), Const.GENERAL_QUESTION);
        assertEquals(recipients.size(), 1);

        ______TS("to self");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.get(email), Const.USER_NAME_FOR_SELF);
        assertEquals(recipients.size(), 1);

    }

    private void testUpdateQuestionNumber() throws Exception {
        ______TS("shift question up");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<FeedbackQuestionAttributes>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 2;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 3;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 1;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        FeedbackQuestionAttributes q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;
        FeedbackQuestionAttributes q6 = getQuestionFromDatastore("custom.feedback.paths.student.question");
        q6.questionNumber = 6;
        FeedbackQuestionAttributes q7 = getQuestionFromDatastore("custom.feedback.paths.student1tostudent5.question");
        q7.questionNumber = 7;
        FeedbackQuestionAttributes q8 = getQuestionFromDatastore("custom.feedback.paths.student5tostudent1.question");
        q8.questionNumber = 8;
        FeedbackQuestionAttributes q9 = getQuestionFromDatastore("custom.feedback.paths.instructor.question");
        q9.questionNumber = 9;
        FeedbackQuestionAttributes q10 = getQuestionFromDatastore("custom.feedback.paths.instructor2toinstructor3.question");
        q10.questionNumber = 10;
        FeedbackQuestionAttributes q11 = getQuestionFromDatastore("custom.feedback.paths.team.question");
        q11.questionNumber = 11;
        FeedbackQuestionAttributes q12 = getQuestionFromDatastore("custom.feedback.paths.team1.1toteam1.2.question");
        q12.questionNumber = 12;

        expectedList.add(q3);
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q4);
        expectedList.add(q5);
        expectedList.add(q6);
        expectedList.add(q7);
        expectedList.add(q8);
        expectedList.add(q9);
        expectedList.add(q10);
        expectedList.add(q11);
        expectedList.add(q12);

        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.questionNumber = 1;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("shift question down");
        expectedList = new ArrayList<FeedbackQuestionAttributes>();
        q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;
        q6 = getQuestionFromDatastore("custom.feedback.paths.student.question");
        q6.questionNumber = 6;
        q7 = getQuestionFromDatastore("custom.feedback.paths.student1tostudent5.question");
        q7.questionNumber = 7;
        q8 = getQuestionFromDatastore("custom.feedback.paths.student5tostudent1.question");
        q8.questionNumber = 8;
        q9 = getQuestionFromDatastore("custom.feedback.paths.instructor.question");
        q9.questionNumber = 9;
        q10 = getQuestionFromDatastore("custom.feedback.paths.instructor2toinstructor3.question");
        q10.questionNumber = 10;
        q11 = getQuestionFromDatastore("custom.feedback.paths.team.question");
        q11.questionNumber = 11;
        q12 = getQuestionFromDatastore("custom.feedback.paths.team1.1toteam1.2.question");
        q12.questionNumber = 12;

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        expectedList.add(q6);
        expectedList.add(q7);
        expectedList.add(q8);
        expectedList.add(q9);
        expectedList.add(q10);
        expectedList.add(q11);
        expectedList.add(q12);

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.questionNumber = 3;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);

        actualList = fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }

    private void testAddQuestion() throws Exception {

        ______TS("Add question for feedback session that does not exist");
        FeedbackQuestionAttributes question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.feedbackSessionName = "non-existent Feedback Session";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), "Session disappeared.");
        }

        ______TS("Add question for course that does not exist");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.courseId = "non-existent course id";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), "Session disappeared.");
        }

        ______TS("Add questions sequentially");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<FeedbackQuestionAttributes>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        FeedbackQuestionAttributes q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;
        FeedbackQuestionAttributes q6 = getQuestionFromDatastore("custom.feedback.paths.student.question");
        q6.questionNumber = 6;
        FeedbackQuestionAttributes q7 = getQuestionFromDatastore("custom.feedback.paths.student1tostudent5.question");
        q7.questionNumber = 7;
        FeedbackQuestionAttributes q8 = getQuestionFromDatastore("custom.feedback.paths.student5tostudent1.question");
        q8.questionNumber = 8;
        FeedbackQuestionAttributes q9 = getQuestionFromDatastore("custom.feedback.paths.instructor.question");
        q9.questionNumber = 9;
        FeedbackQuestionAttributes q10 = getQuestionFromDatastore("custom.feedback.paths.instructor2toinstructor3.question");
        q10.questionNumber = 10;
        FeedbackQuestionAttributes q11 = getQuestionFromDatastore("custom.feedback.paths.team.question");
        q11.questionNumber = 11;
        FeedbackQuestionAttributes q12 = getQuestionFromDatastore("custom.feedback.paths.team1.1toteam1.2.question");
        q12.questionNumber = 12;
        FeedbackQuestionAttributes q13 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q13.questionNumber = 13;

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        expectedList.add(q6);
        expectedList.add(q7);
        expectedList.add(q8);
        expectedList.add(q9);
        expectedList.add(q10);
        expectedList.add(q11);
        expectedList.add(q12);
        expectedList.add(q13);

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatastore("qn1InSession1InCourse1");
        newQuestion.questionNumber = 13;

        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question to the front of the list");

        FeedbackQuestionAttributes q14 = getQuestionFromDatastore("qn4InSession1InCourse1");

        q14.questionNumber = 1;
        q1.questionNumber = 2;
        q2.questionNumber = 3;
        q3.questionNumber = 4;
        q4.questionNumber = 5;
        q5.questionNumber = 6;
        q6.questionNumber = 7;
        q7.questionNumber = 8;
        q8.questionNumber = 9;
        q9.questionNumber = 10;
        q10.questionNumber = 11;
        q11.questionNumber = 12;
        q12.questionNumber = 13;
        q13.questionNumber = 14;

        expectedList.add(0, q14);

        //Add a question to session1course1 and sets its number to 1
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 1;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question inbetween 2 existing questions");

        FeedbackQuestionAttributes q15 = getQuestionFromDatastore("qn4InSession1InCourse1");

        q15.questionNumber = 3;
        q2.questionNumber = 4;
        q3.questionNumber = 5;
        q4.questionNumber = 6;
        q5.questionNumber = 7;
        q6.questionNumber = 8;
        q7.questionNumber = 9;
        q8.questionNumber = 10;
        q9.questionNumber = 11;
        q10.questionNumber = 12;
        q11.questionNumber = 13;
        q12.questionNumber = 14;
        q13.questionNumber = 15;

        expectedList.add(2, q15);

        //Add a question to session1course1 and place it between existing question 2 and 3
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 3;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }
    }

    private void testCopyQuestion() throws Exception {

        InstructorAttributes instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        ______TS("Typical case: copy question successfully");

        FeedbackQuestionAttributes question1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        question1 = fqLogic.getFeedbackQuestion(question1.feedbackSessionName, question1.courseId, question1.questionNumber);

        FeedbackQuestionAttributes copiedQuestion =
                fqLogic.copyFeedbackQuestion(question1.getId(), question1.feedbackSessionName, question1.courseId,
                                             instructor2OfCourse1.email);

        FeedbackQuestionDetails question1Details = question1.getQuestionDetails();
        FeedbackQuestionDetails copiedQuestionDetails = copiedQuestion.getQuestionDetails();

        assertEquals(question1.numberOfEntitiesToGiveFeedbackTo, copiedQuestion.numberOfEntitiesToGiveFeedbackTo);
        assertEquals(question1.questionType, copiedQuestion.questionType);
        assertEquals(question1.giverType, copiedQuestion.giverType);
        assertEquals(question1.recipientType, copiedQuestion.recipientType);
        assertEquals(question1Details.getQuestionText(), copiedQuestionDetails.getQuestionText());

    }

    private void testUpdateQuestion() throws Exception {
        ______TS("standard update, no existing responses, with 'keep existing' policy");
        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn2InSession2InCourse2");
        questionToUpdate.questionMetaData = new Text("new question text");
        questionToUpdate.questionNumber = 3;
        List<FeedbackParticipantType> newVisibility =
                new LinkedList<FeedbackParticipantType>();
        newVisibility.add(FeedbackParticipantType.INSTRUCTORS);
        questionToUpdate.showResponsesTo = newVisibility;
        // Check keep existing policy.
        String originalCourseId = questionToUpdate.courseId;
        questionToUpdate.courseId = null;

        fqLogic.updateFeedbackQuestion(questionToUpdate);

        questionToUpdate.courseId = originalCourseId;

        FeedbackQuestionAttributes updatedQuestion =
                fqLogic.getFeedbackQuestion(questionToUpdate.getId());
        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());

        ______TS("cascading update, non-destructive changes, existing responses are preserved");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 2");
        questionToUpdate.numberOfEntitiesToGiveFeedbackTo = 2;

        int numberOfResponses =
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size();

        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size(), numberOfResponses);

        ______TS("cascading update, destructive changes, delete all existing responses");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 3");
        questionToUpdate.recipientType = FeedbackParticipantType.INSTRUCTORS;

        assertFalse(frLogic.getFeedbackResponsesForQuestion(questionToUpdate.getId()).isEmpty());

        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(frLogic.getFeedbackResponsesForQuestion(
                questionToUpdate.getId()).size(), 0);

        ______TS("cascading update for changed student email, "
                 + "custom feedback paths containing updated student should be updated");

        List<FeedbackQuestionAttributes> questionsToUpdate =
                Arrays.asList(fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 8),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 9),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 10),
                              fqLogic.getFeedbackQuestion("Session With Custom Participants", "idOfTypicalCourse1", 1));

        assertEquals(1, questionsToUpdate.get(0).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(1).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(2).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(3).feedbackPaths.size());

        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getRecipient());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(2).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(2).feedbackPaths.get(0).getRecipient());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(3).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(3).feedbackPaths.get(0).getRecipient());

        StudentAttributes updatedStudent = dataBundle.students.get("student5InCourse1");
        updatedStudent.email = "updatedStudentEmail@gmail.tmt";

        fqLogic.updateFeedbackQuestionsForChangingStudentEmail("student5InCourse1@gmail.tmt", updatedStudent);

        List<FeedbackQuestionAttributes> updatedQuestions =
                Arrays.asList(fqLogic.getFeedbackQuestion(questionsToUpdate.get(0).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(1).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(2).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(3).getId()));

        assertEquals(1, updatedQuestions.get(0).feedbackPaths.size());
        assertEquals(1, updatedQuestions.get(1).feedbackPaths.size());
        assertEquals(1, updatedQuestions.get(2).feedbackPaths.size());
        assertEquals(1, updatedQuestions.get(3).feedbackPaths.size());

        assertEquals("updatedStudentEmail@gmail.tmt (Student)",
                     updatedQuestions.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("updatedStudentEmail@gmail.tmt (Student)",
                     updatedQuestions.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     updatedQuestions.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("updatedStudentEmail@gmail.tmt (Student)",
                     updatedQuestions.get(1).feedbackPaths.get(0).getRecipient());
        assertEquals("updatedStudentEmail@gmail.tmt (Student)",
                     updatedQuestions.get(2).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     updatedQuestions.get(2).feedbackPaths.get(0).getRecipient());
        assertEquals("updatedStudentEmail@gmail.tmt (Student)",
                     updatedQuestions.get(3).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     updatedQuestions.get(3).feedbackPaths.get(0).getRecipient());

        // Revert change to questions
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(0));
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(1));
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(2));
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(3));

        ______TS("cascading update for changed instructor email, "
                 + "custom feedback paths containing updated instructor should be updated");

        questionsToUpdate =
                Arrays.asList(fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 11),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 12));

        assertEquals(1, questionsToUpdate.get(0).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(1).feedbackPaths.size());

        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("instructor3@course1.tmt (Instructor)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getRecipient());

        InstructorAttributes updatedInstructor = dataBundle.instructors.get("instructor2OfCourse1");
        updatedInstructor.email = "updatedInstructorEmail@gmail.tmt";

        fqLogic.updateFeedbackQuestionsForChangingInstructorEmail("instructor2@course1.tmt", updatedInstructor);

        updatedQuestions = Arrays.asList(fqLogic.getFeedbackQuestion(questionsToUpdate.get(0).getId()),
                                         fqLogic.getFeedbackQuestion(questionsToUpdate.get(1).getId()));

        assertEquals(1, updatedQuestions.get(0).feedbackPaths.size());
        assertEquals(1, updatedQuestions.get(1).feedbackPaths.size());

        assertEquals("updatedInstructorEmail@gmail.tmt (Instructor)",
                     updatedQuestions.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("updatedInstructorEmail@gmail.tmt (Instructor)",
                     updatedQuestions.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("updatedInstructorEmail@gmail.tmt (Instructor)",
                     updatedQuestions.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("instructor3@course1.tmt (Instructor)",
                     updatedQuestions.get(1).feedbackPaths.get(0).getRecipient());

        // Revert change to questions
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(0));
        fqLogic.updateFeedbackQuestion(questionsToUpdate.get(1));

        ______TS("cascading update for deleted student, custom feedback paths and "
                 + "responses for custom feedback paths containing deleted student should be deleted");

        questionsToUpdate =
                Arrays.asList(fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 8),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 9),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 10),
                              fqLogic.getFeedbackQuestion("Session With Custom Participants", "idOfTypicalCourse1", 1));

        assertEquals(1, questionsToUpdate.get(0).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(1).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(2).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(3).feedbackPaths.size());

        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getRecipient());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(2).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(2).feedbackPaths.get(0).getRecipient());
        assertEquals("student5InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(3).feedbackPaths.get(0).getGiver());
        assertEquals("student1InCourse1@gmail.tmt (Student)",
                     questionsToUpdate.get(3).feedbackPaths.get(0).getRecipient());

        List<FeedbackResponseAttributes> responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(questionsToUpdate.get(0).getFeedbackQuestionId(),
                                      "student5InCourse1@gmail.tmt", "student5InCourse1@gmail.tmt"),
                              frLogic.getFeedbackResponse(questionsToUpdate.get(1).getFeedbackQuestionId(),
                                      "student1InCourse1@gmail.tmt", "student5InCourse1@gmail.tmt"),
                              frLogic.getFeedbackResponse(questionsToUpdate.get(2).getFeedbackQuestionId(),
                                      "student5InCourse1@gmail.tmt", "student1InCourse1@gmail.tmt"));

        assertNotNull(responsesForQuestions.get(0));
        assertNotNull(responsesForQuestions.get(1));
        assertNotNull(responsesForQuestions.get(2));

        fqLogic.updateFeedbackQuestionsForDeletedStudent("idOfTypicalCourse1", "student5InCourse1@gmail.tmt");

        updatedQuestions =
                Arrays.asList(fqLogic.getFeedbackQuestion(questionsToUpdate.get(0).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(1).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(2).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(3).getId()));

        assertTrue(updatedQuestions.get(0).feedbackPaths.isEmpty());
        assertTrue(updatedQuestions.get(1).feedbackPaths.isEmpty());
        assertTrue(updatedQuestions.get(2).feedbackPaths.isEmpty());
        assertTrue(updatedQuestions.get(3).feedbackPaths.isEmpty());

        responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(updatedQuestions.get(0).getFeedbackQuestionId(),
                                      "student5InCourse1@gmail.tmt", "student5InCourse1@gmail.tmt"),
                              frLogic.getFeedbackResponse(updatedQuestions.get(1).getFeedbackQuestionId(),
                                      "student1InCourse1@gmail.tmt", "student5InCourse1@gmail.tmt"),
                              frLogic.getFeedbackResponse(updatedQuestions.get(2).getFeedbackQuestionId(),
                                      "student5InCourse1@gmail.tmt", "student1InCourse1@gmail.tmt"));

        assertNull(responsesForQuestions.get(0));
        assertNull(responsesForQuestions.get(1));
        assertNull(responsesForQuestions.get(2));

        ______TS("cascading update for deleted instructor, custom feedback paths and "
                 + "responses for custom feedback paths containing deleted instructor should be deleted");

        questionsToUpdate =
                Arrays.asList(fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 11),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 12));
        assertEquals(1, questionsToUpdate.get(0).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(1).feedbackPaths.size());

        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("instructor2@course1.tmt (Instructor)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("instructor3@course1.tmt (Instructor)",
                     questionsToUpdate.get(1).feedbackPaths.get(0).getRecipient());

        InstructorAttributes deletedInstructor = dataBundle.instructors.get("instructor2OfCourse1");
        deletedInstructor.email = "instructor2@course1.tmt";

        responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(questionsToUpdate.get(0).getFeedbackQuestionId(),
                                      "instructor2@course1.tmt", "instructor2@course1.tmt"),
                              frLogic.getFeedbackResponse(questionsToUpdate.get(1).getFeedbackQuestionId(),
                                      "instructor2@course1.tmt", "instructor3@course1.tmt"));

        assertNotNull(responsesForQuestions.get(0));
        assertNotNull(responsesForQuestions.get(1));

        fqLogic.updateFeedbackQuestionsForDeletedInstructor(deletedInstructor);

        updatedQuestions =
                Arrays.asList(fqLogic.getFeedbackQuestion(questionsToUpdate.get(0).getId()),
                              fqLogic.getFeedbackQuestion(questionsToUpdate.get(1).getId()));

        assertTrue(updatedQuestions.get(0).feedbackPaths.isEmpty());
        assertTrue(updatedQuestions.get(1).feedbackPaths.isEmpty());

        responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(updatedQuestions.get(0).getFeedbackQuestionId(),
                                      "instructor2@course1.tmt", "instructor2@course1.tmt"),
                              frLogic.getFeedbackResponse(updatedQuestions.get(1).getFeedbackQuestionId(),
                                      "instructor2@course1.tmt", "instructor3@course1.tmt"));

        assertNull(responsesForQuestions.get(0));
        assertNull(responsesForQuestions.get(1));

        ______TS("cascading update for deleted team, custom feedback paths and "
                 + "responses for custom feedback paths containing deleted team should be deleted");

        questionsToUpdate =
                Arrays.asList(fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 13),
                              fqLogic.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 14));

        assertEquals(1, questionsToUpdate.get(0).feedbackPaths.size());
        assertEquals(1, questionsToUpdate.get(1).feedbackPaths.size());

        assertEquals("Team 1.2 (Team)", questionsToUpdate.get(0).feedbackPaths.get(0).getGiver());
        assertEquals("Team 1.2 (Team)", questionsToUpdate.get(0).feedbackPaths.get(0).getRecipient());
        assertEquals("Team 1.1</td></div>'\" (Team)", questionsToUpdate.get(1).feedbackPaths.get(0).getGiver());
        assertEquals("Team 1.2 (Team)", questionsToUpdate.get(1).feedbackPaths.get(0).getRecipient());

        responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(
                                      questionsToUpdate.get(0).getFeedbackQuestionId(), "Team 1.2", "Team 1.2"),
                              frLogic.getFeedbackResponse(
                                      questionsToUpdate.get(1).getFeedbackQuestionId(),
                                      "Team 1.1</td></div>'\"", "Team 1.2"));

        assertNotNull(responsesForQuestions.get(0));
        assertNotNull(responsesForQuestions.get(1));

        fqLogic.updateFeedbackQuestionsForDeletedTeam("idOfTypicalCourse1", "Team 1.2");

        updatedQuestions =
                Arrays.asList(fqLogic.getFeedbackQuestion(updatedQuestions.get(0).getId()),
                              fqLogic.getFeedbackQuestion(updatedQuestions.get(1).getId()));

        assertTrue(updatedQuestions.get(0).feedbackPaths.isEmpty());
        assertTrue(updatedQuestions.get(1).feedbackPaths.isEmpty());

        responsesForQuestions =
                Arrays.asList(frLogic.getFeedbackResponse(
                                      questionToUpdate.getFeedbackQuestionId(), "Team 1.2", "Team 1.2"),
                              frLogic.getFeedbackResponse(
                                      questionToUpdate.getFeedbackQuestionId(), "Team 1.1</td></div>'\"", "Team 1.2"));

        assertNull(responsesForQuestions.get(0));
        assertNull(responsesForQuestions.get(1));

        ______TS("failure: question does not exist");

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        fqLogic.deleteFeedbackQuestionCascade(questionToUpdate.getId());

        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected EntityDoesNotExistException not caught.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to update a feedback question that does not exist.");
        }

        ______TS("failure: invalid parameters");

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.giverType = FeedbackParticipantType.TEAMS;
        questionToUpdate.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;
        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected InvalidParametersException not caught.");
        } catch (InvalidParametersException e) {
            assertEquals(e.getMessage(), String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                                       questionToUpdate.recipientType.toDisplayRecipientName(),
                                                       questionToUpdate.giverType.toDisplayGiverName()));
        }
    }

    private void testDeleteQuestion() {
        //Success case already tested in update
        ______TS("question already does not exist, silently fail");

        fqLogic.deleteFeedbackQuestionCascade("non-existent-question-id");
        //No error should be thrown.

    }

    private void testDeleteQuestionsForCourse() throws EntityDoesNotExistException {
        ______TS("standard case");

        // test that questions are deleted
        String courseId = "idOfTypicalCourse2";
        FeedbackQuestionAttributes deletedQuestion = getQuestionFromDatastore("qn1InSession2InCourse2");
        assertNotNull(deletedQuestion);

        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertFalse(questions.isEmpty());
        questions = fqLogic.getFeedbackQuestionsForSession("Private feedback session", courseId);
        assertFalse(questions.isEmpty());

        fqLogic.deleteFeedbackQuestionsForCourse(courseId);
        deletedQuestion = getQuestionFromDatastore("qn1InSession2InCourse2");
        assertNull(deletedQuestion);

        questions = fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertEquals(0, questions.size());
        questions = fqLogic.getFeedbackQuestionsForSession("Private feedback session", courseId);
        assertEquals(0, questions.size());

        // test that questions in other courses are unaffected
        assertNotNull(getQuestionFromDatastore("qn1InSessionInArchivedCourse"));
        assertNotNull(getQuestionFromDatastore("qn1InSession4InCourse1"));
    }

    private void testGetFeedbackQuestionsForInstructor() throws Exception {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;

        ______TS("Get questions created for instructors and self");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn5InSession1InCourse1"));
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1",
                                                          "instructor1@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors and self by another instructor");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor2toinstructor3.question"));
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1",
                                                          "instructor2@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors by the creating instructor");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession2InCourse2"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession2InCourse2"));

        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "instructor1@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions for creator instructor, verifies that the two methods return the same questions");
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>(actualQuestions);
        actualQuestions =
                fqLogic.getFeedbackQuestionsForCreatorInstructor("Instructor feedback session", "idOfTypicalCourse2");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors not by the creating instructor");

        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "instructor2@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors by non-instructor of the course");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "iwc@yahoo.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Failure: Getting questions for a non-existent session");

        try {
            fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse1",
                                                      "instructor1@course1.tmt");
            signalFailureToDetectException("Allowed to get questions for a feedback session that does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to get questions for a feedback session that does not exist.");
        }

        ______TS("Get questions created for self from list of all questions");

        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructor(allQuestions, true, "instructor1@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for an instructor from list of all questions");

        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor.question"));

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor.question"));

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructor(allQuestions, false, "instructor2@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);
    }

    private void testGetFeedbackQuestionsForStudents() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;

        ______TS("Get questions created for students");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("First feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students and teams");

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("Second feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

    }

    private void testGetFeedbackQuestionsForStudent() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;
        StudentAttributes student = dataBundle.students.get("student5InCourse1");

        ______TS("Get questions created for single student");
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student5tostudent1.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.team.question"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudent(
                "First feedback session", "idOfTypicalCourse1", student);

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for a student from list of all questions");

        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student.question"));

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student.question"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudent(allQuestions, student);

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for a student and his/her team from list of all questions");

        allQuestions = new ArrayList<FeedbackQuestionAttributes>();
        allQuestions.add(getQuestionFromDatastore("team.feedback"));
        allQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        allQuestions.add(getQuestionFromDatastore("custom.feedback.paths.team.question"));

        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.team.question"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudent(allQuestions, student);

        assertEquals(actualQuestions, expectedQuestions);
    }

    private void testGetFeedbackQuestionsWithCustomFeedbackPaths() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;

        ______TS("Get questions with custom feedback paths from session");
        expectedQuestions = new ArrayList<FeedbackQuestionAttributes>();
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student1tostudent5.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.student5tostudent1.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.instructor2toinstructor3.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.team.question"));
        expectedQuestions.add(getQuestionFromDatastore("custom.feedback.paths.team1.1toteam1.2.question"));

        actualQuestions = fqLogic.getFeedbackQuestionsWithCustomFeedbackPaths(
                "First feedback session", "idOfTypicalCourse1");

        AssertHelper.assertSameContentIgnoreOrder(expectedQuestions, actualQuestions);
    }

    private void testIsQuestionHasResponses() {
        FeedbackQuestionAttributes questionWithResponse;
        FeedbackQuestionAttributes questionWithoutResponse;

        ______TS("Check that a question has some responses");

        questionWithResponse = getQuestionFromDatastore("qn1InSession2InCourse2");
        assertTrue(fqLogic.areThereResponsesForQuestion(questionWithResponse.getId()));

        ______TS("Check that a question has no responses");

        questionWithoutResponse = getQuestionFromDatastore("qn2InSession2InCourse2");
        assertFalse(fqLogic.areThereResponsesForQuestion(questionWithoutResponse.getId()));
    }

    private void testIsQuestionAnswered() throws Exception {
        FeedbackQuestionAttributes question;

        ______TS("test question is fully answered by user");

        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(fqLogic.isQuestionFullyAnsweredByUser(question, "student1InCourse1@gmail.tmt"));

        assertFalse(fqLogic.isQuestionFullyAnsweredByUser(question, "studentWithNoResponses@gmail.tmt"));

        ______TS("test question is fully answered by team");

        assertFalse(fqLogic.isQuestionFullyAnsweredByTeam(question, "Team 1.1"));

    }

    private void testAddQuestionNoIntegrityCheck() throws InvalidParametersException, EntityDoesNotExistException {

        ______TS("Add questions sequentially - test for initial template question");
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;

        int initialNumQuestions = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId).size();

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatastore("qn1InSession1InCourse1");
        newQuestion.questionNumber = initialNumQuestions + 1;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestionNoIntegrityCheck(newQuestion, newQuestion.questionNumber);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), initialNumQuestions + 1);

        //The list starts from 0, so no need to + 1 here.
        assertEquals(actualList.get(initialNumQuestions), newQuestion);

    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(String questionKey) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionKey);
        question = fqLogic.getFeedbackQuestion(
                question.feedbackSessionName, question.courseId, question.questionNumber);
        return question;
    }

}
