package teammates.test.cases.webapi;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.StudentsLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SendReminderEmailAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link SendReminderEmailActionTest}.
 */
public class SendReminderEmailActionTest extends BaseActionTest<SendReminderEmailAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN_REMIND;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.email);

        ______TS("Typical case: Send email to remind an instructor to register for the course");

        loginAsInstructor(instructorId);
        InstructorAttributes anotherInstructorOfCourse1 = typicalBundle.instructors.get("instructorNotYetJoinCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email,
        };

        SendReminderEmailAction sendReminderEmailAction = getAction(submissionParams);
        JsonResult result = getJsonResult(sendReminderEmailAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        MessageOutput msg = (MessageOutput) result.getOutput();
        assertEquals("An email has been sent to " + anotherInstructorOfCourse1.email, msg.getMessage());

        verifySpecifiedTasksAdded(sendReminderEmailAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = sendReminderEmailAction.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(anotherInstructorOfCourse1.email, paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL)[0]);

        ______TS("Typical case: Send email to remind a student to register for the course from course details page");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        sendReminderEmailAction = getAction(submissionParams);
        result = getJsonResult(sendReminderEmailAction);

        msg = (MessageOutput) result.getOutput();
        assertEquals("An email has been sent to " + student1InCourse1.email, msg.getMessage());

        ______TS("Typical case: Send email to remind a student to register for the course from student list page");

        sendReminderEmailAction = getAction(submissionParams);
        result = getJsonResult(sendReminderEmailAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        msg = (MessageOutput) result.getOutput();
        assertEquals("An email has been sent to " + student1InCourse1.email, msg.getMessage());

        verifySpecifiedTasksAdded(sendReminderEmailAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = sendReminderEmailAction.getTaskQueuer().getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(student1InCourse1.email, paramMap.get(Const.ParamsNames.STUDENT_EMAIL)[0]);

        ______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");

        loginAsAdmin();
        StudentAttributes unregisteredStudent1 = StudentAttributes
                .builder(courseId, "Unregistered student 1", "unregistered1@email.com")
                .withSection("Section 1")
                .withTeam("Team Unregistered")
                .withComments("")
                .build();
        StudentAttributes unregisteredStudent2 = StudentAttributes
                .builder(courseId, "Unregistered student 2", "unregistered2@email.com")
                .withSection("Section 1")
                .withTeam("Team Unregistered")
                .withComments("")
                .build();
        StudentsLogic.inst().createStudentCascade(unregisteredStudent1);
        StudentsLogic.inst().createStudentCascade(unregisteredStudent2);

        /* Reassign the attributes to retrieve their keys */
        unregisteredStudent1 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent1.email);
        unregisteredStudent2 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent2.email);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        sendReminderEmailAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(sendReminderEmailAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        msg = (MessageOutput) result.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // 2 unregistered students, thus 2 emails queued to be sent
        verifySpecifiedTasksAdded(sendReminderEmailAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = sendReminderEmailAction.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        }

        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent1.email);
        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent2.email);

        ______TS("Typical case: no unregistered students in course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        sendReminderEmailAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(sendReminderEmailAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        msg = (MessageOutput) result.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // no unregistered students, thus no emails sent
        verifyNoTasksAdded(sendReminderEmailAction);

        ______TS("Failure case: Invalid email parameter");

        String invalidEmail = "invalidEmail.com";
        String[] invalidInstructorEmailSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
        };

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                getAction(addUserIdToParams(instructorId, invalidInstructorEmailSubmissionParams)).execute());
        assertEquals("Instructor with email " + invalidEmail + " does not exist "
                + "in course " + courseId + "!", entityNotFoundException.getMessage());

        String[] invalidStudentEmailSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail,
        };

        entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                getAction(instructorId, invalidStudentEmailSubmissionParams).execute());
        assertEquals("Student with email " + invalidEmail + " does not exist "
                + "in course " + courseId + "!", entityNotFoundException.getMessage());

        ______TS("Failure case: Invalid course id parameter");

        String[] invalidCourseIdSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidCourseId",
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email,
        };

        entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                getAction(instructorId, invalidCourseIdSubmissionParams).execute());
        assertEquals("Course with ID invalidCourseId does not exist!", entityNotFoundException.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
