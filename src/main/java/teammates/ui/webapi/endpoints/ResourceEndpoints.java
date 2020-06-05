package teammates.ui.webapi.endpoints;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.ResourceURIs;

/**
 * API endpoints for resources.
 */
public enum ResourceEndpoints {
    //CHECKSTYLE.OFF:JavadocVariable
    URI_PREFIX(ResourceURIs.URI_PREFIX),
    LOGOUT(ResourceURIs.LOGOUT),
    DATABUNDLE(ResourceURIs.DATABUNDLE),
    EXCEPTION(ResourceURIs.EXCEPTION),
    ERROR_REPORT(ResourceURIs.ERROR_REPORT),
    AUTH(ResourceURIs.AUTH),
    ACCOUNT(ResourceURIs.ACCOUNT),
    ACCOUNT_RESET(ResourceURIs.ACCOUNT_RESET),
    ACCOUNT_DOWNGRADE(ResourceURIs.ACCOUNT_DOWNGRADE),
    RESPONSE_COMMENT(ResourceURIs.RESPONSE_COMMENT),
    COURSE(ResourceURIs.COURSE),
    COURSE_ARCHIVE(ResourceURIs.COURSE_ARCHIVE),
    BIN_COURSE(ResourceURIs.BIN_COURSE),
    COURSE_SECTIONS(ResourceURIs.COURSE_SECTIONS),
    COURSES(ResourceURIs.COURSES),
    INSTRUCTORS(ResourceURIs.INSTRUCTORS),
    INSTRUCTOR(ResourceURIs.INSTRUCTOR),
    INSTRUCTOR_PRIVILEGE(ResourceURIs.INSTRUCTOR_PRIVILEGE),
    RESULT(ResourceURIs.RESULT),
    RESULT_CSV(ResourceURIs.RESULT_CSV),
    STUDENTS(ResourceURIs.STUDENTS),
    STUDENT(ResourceURIs.STUDENT),
    SESSIONS_ONGOING(ResourceURIs.SESSIONS_ONGOING),
    SESSION(ResourceURIs.SESSION),
    SESSION_PUBLISH(ResourceURIs.SESSION_PUBLISH),
    SESSION_REMIND_SUBMISSION(ResourceURIs.SESSION_REMIND_SUBMISSION),
    SESSION_REMIND_RESULT(ResourceURIs.SESSION_REMIND_RESULT),
    SESSION_STATS(ResourceURIs.SESSION_STATS),
    SESSION_SUBMITTED_GIVER_SET(ResourceURIs.SESSION_SUBMITTED_GIVER_SET),
    SESSIONS(ResourceURIs.SESSIONS),
    BIN_SESSION(ResourceURIs.BIN_SESSION),
    QUESTIONS(ResourceURIs.QUESTIONS),
    QUESTION(ResourceURIs.QUESTION),
    QUESTION_RECIPIENTS(ResourceURIs.QUESTION_RECIPIENTS),
    RESPONSE(ResourceURIs.RESPONSE),
    RESPONSES(ResourceURIs.RESPONSES),
    HAS_RESPONSES(ResourceURIs.HAS_RESPONSES),
    SUBMISSION_CONFIRMATION(ResourceURIs.SUBMISSION_CONFIRMATION),
    JOIN(ResourceURIs.JOIN),
    JOIN_REMIND(ResourceURIs.JOIN_REMIND),
    TIMEZONE(ResourceURIs.TIMEZONE),
    LOCAL_DATE_TIME(ResourceURIs.LOCAL_DATE_TIME),
    SESSION_LINKS_RECOVERY(ResourceURIs.SESSION_LINKS_RECOVERY),
    NATIONALITIES(ResourceURIs.NATIONALITIES),
    STUDENT_PROFILE_PICTURE(ResourceURIs.STUDENT_PROFILE_PICTURE),
    STUDENT_PROFILE(ResourceURIs.STUDENT_PROFILE),
    STUDENT_COURSE_LINKS_REGENERATION(ResourceURIs.STUDENT_COURSE_LINKS_REGENERATION),
    STUDENTS_CSV(ResourceURIs.STUDENTS_CSV),
    SEARCH_COMMENTS(ResourceURIs.SEARCH_COMMENTS),
    SEARCH_INSTRUCTORS(ResourceURIs.SEARCH_INSTRUCTORS),
    SEARCH_STUDENTS(ResourceURIs.SEARCH_STUDENTS);
    //CHECKSTYLE.ON:JavadocVariable

    private final String url;

    ResourceEndpoints(String s) {
        this.url = s;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}
