package teammates.ui.webapi.endpoints;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.ActionURIs;

/**
 * API endpoints for resources.
 */
public enum ActionEndpoints {
    //CHECKSTYLE.OFF:JavadocVariable
    INSTRUCTOR_COURSE_STUDENT_DELETE(ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE),
    INSTRUCTOR_COURSE_REMIND(ActionURIs.INSTRUCTOR_COURSE_REMIND),
    INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE(ActionURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE),
    INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE(ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE),
    INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS(ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS),
    INSTRUCTOR_FEEDBACK_PUBLISH(ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH),
    INSTRUCTOR_FEEDBACK_UNPUBLISH(ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH),
    INSTRUCTOR_FEEDBACK_RESULTS_PAGE(ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE),
    INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD(ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD),
    STUDENT_PROFILE_PICTURE(ActionURIs.STUDENT_PROFILE_PICTURE);
    //CHECKSTYLE.ON:JavadocVariable

    private final String url;

    ActionEndpoints(String s) {
        this.url = s;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}
