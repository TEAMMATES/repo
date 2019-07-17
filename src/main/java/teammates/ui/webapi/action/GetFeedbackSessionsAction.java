package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions.
 */
public class GetFeedbackSessionsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(entityType.equals(Const.EntityType.STUDENT) || entityType.equals(Const.EntityType.INSTRUCTOR))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (entityType.equals(Const.EntityType.STUDENT)) {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSessionAttributes> feedbackSessionAttributes;

        if (courseId == null) {
            if (entityType.equals(Const.EntityType.STUDENT)) {
                List<StudentAttributes> students = logic.getStudentsForGoogleId(userInfo.getId());
                feedbackSessionAttributes = new ArrayList<>();
                for (StudentAttributes student : students) {
                    try {
                        feedbackSessionAttributes.addAll(
                                logic.getFeedbackSessionsForUserInCourse(student.getCourse(), student.email));
                    } catch (EntityDoesNotExistException e) {
                        return new JsonResult("Course does not exist.", HttpStatus.SC_NOT_FOUND);
                    }
                }
            } else {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.getId(), true);

                if (isInRecycleBin) {
                    feedbackSessionAttributes = logic.getSoftDeletedFeedbackSessionsListForInstructors(instructors);
                } else {
                    feedbackSessionAttributes = logic.getFeedbackSessionsListForInstructor(instructors);
                }
            }
        } else {
            if (entityType.equals(Const.EntityType.STUDENT)) {
                StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
                try {
                    feedbackSessionAttributes = logic.getFeedbackSessionsForUserInCourse(courseId, student.email);
                } catch (EntityDoesNotExistException e) {
                    return new JsonResult("Course does not exist.", HttpStatus.SC_NOT_FOUND);
                }
            } else {
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                try {
                    feedbackSessionAttributes = logic.getFeedbackSessionsForUserInCourse(courseId, instructor.email);
                } catch (EntityDoesNotExistException e) {
                    return new JsonResult("Course does not exist.", HttpStatus.SC_NOT_FOUND);
                }
            }
        }

        if (entityType.equals(Const.EntityType.STUDENT)) {
            // hide session not visible to student
            feedbackSessionAttributes = feedbackSessionAttributes.stream()
                    .filter(FeedbackSessionAttributes::isVisible).collect(Collectors.toList());
        }

        FeedbackSessionsData responseData = new FeedbackSessionsData(feedbackSessionAttributes);
        if (entityType.equals(Const.EntityType.STUDENT)) {
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformationForStudent);
        }
        return new JsonResult(responseData);
    }

}
