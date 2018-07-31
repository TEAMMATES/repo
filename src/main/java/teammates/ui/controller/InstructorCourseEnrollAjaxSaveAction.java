package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;

/**
 * Action: saving the list of enrolled students for a course of an instructor.
 */
public class InstructorCourseEnrollAjaxSaveAction extends Action {

    private static final Logger log = Logger.getLogger();
    private static Map<String, String> enrollErrorLines;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);
        String sanitizedStudentsInfo = SanitizationHelper.sanitizeForHtml(studentsInfo);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, studentsInfo);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        /* Process enrollment list and setup data for page result */
        try {
            enrollErrorLines = new HashMap<>();
            List<StudentAttributes>[] students = enrollAndProcessResultForDisplay(studentsInfo, courseId);

            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);
            pageData.updateEnrollSuccessLines(students);

            statusToUser.add(new StatusMessage("Enroll success.", StatusMessageColor.SUCCESS));

            statusToAdmin = "Students Enrolled in Course <span class=\"bold\">["
                            + courseId + "]:</span><br>" + sanitizedStudentsInfo.replace("\n", "<br>");

            return createAjaxResult(pageData);

        } catch (EnrollException e) {
            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

            List<String> exceptionMessages = new ArrayList<>(Arrays.asList(e.getMessage().split("<br>")));

            for (String exceptionMessage : exceptionMessages) {
                if (exceptionMessage.equals(Const.StatusMessages.ENROLL_LINE_EMPTY)) {
                    statusToUser.add(new StatusMessage(Const.StatusMessages.ENROLL_LINE_EMPTY,
                            StatusMessageColor.DANGER));
                    break;
                } else if (exceptionMessage.equals(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED)) {
                    statusToUser.add(new StatusMessage(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED,
                            StatusMessageColor.DANGER));
                    break;
                }

                if (!"Please use the enroll page to edit multiple students".equals(exceptionMessage)) {
                    enrollErrorLines.put(exceptionMessage.split("##")[0], exceptionMessage.split("##")[1]);
                }
            }
            pageData.setEnrollErrorLines(enrollErrorLines);
            pageData.setStatusMessagesToUser(statusToUser);
            statusToAdmin += "<br>Enrollment string entered by user:<br>" + sanitizedStudentsInfo.replace("\n", "<br>");

            return createAjaxResult(pageData);
        } catch (InvalidParametersException e) {
            setStatusForException(e);

            statusToAdmin += "<br>Enrollment string entered by user:<br>" + sanitizedStudentsInfo.replace("\n", "<br>");

            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

            return createAjaxResult(pageData);
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e);

            statusToUser.add(
                    new StatusMessage("The enrollment failed, possibly because some students were re-enrolled before "
                                      + "the previous enrollment action was still being processed by TEAMMATES database "
                                      + "servers. Please try again after about 10 minutes. If the problem persists, "
                                      + "please contact TEAMMATES support", StatusMessageColor.DANGER));

            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

            log.severe("Entity already exists exception occurred when updating student: " + e.getMessage());
            return createAjaxResult(pageData);
        }
    }

    private List<StudentAttributes>[] enrollAndProcessResultForDisplay(String studentsInfo, String courseId)
            throws EnrollException, EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        CourseEnrollmentResult enrollResult = logic.enrollStudents(studentsInfo, courseId);
        List<StudentAttributes> students = enrollResult.studentList;

        // Adjust submissions for all feedback responses within the course
        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : feedbackSessions) {
            // Schedule adjustment of submissions for feedback session in course
            taskQueuer.scheduleFeedbackResponseAdjustmentForCourse(
                    courseId, session.getFeedbackSessionName(), enrollResult.enrollmentList);
        }

        students.sort(Comparator.comparing(obj -> obj.updateStatus.numericRepresentation));

        return separateStudents(students);
    }

    /**
     * Separate the StudentData objects in the list into different categories based
     * on their updateStatus. Each category is put into a separate list.<br>
     *
     * @return An array of lists of StudentData objects in which each list contains
     *         student with the same updateStatus
     */
    @SuppressWarnings("unchecked")
    private List<StudentAttributes>[] separateStudents(List<StudentAttributes> students) {

        ArrayList<StudentAttributes>[] lists = new ArrayList[StudentUpdateStatus.STATUS_COUNT];
        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            lists[i] = new ArrayList<>();
        }

        for (StudentAttributes student : students) {
            lists[student.updateStatus.numericRepresentation].add(student);
        }

        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            StudentAttributes.sortByNameAndThenByEmail(lists[i]);
        }

        return lists;
    }

}
