package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class InstructorCourseStudentDetailsEditPageData extends InstructorCourseStudentDetailsPageData {

    private String newEmail;
    private boolean isOpenOrPublishedEmailSentForTheCourse;

    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, String sessionToken, StudentAttributes student, boolean hasSection,
            boolean isOpenOrPublishedEmailSentForTheCourse) {
        this(account, sessionToken, student, student.email, "Unknown", hasSection, isOpenOrPublishedEmailSentForTheCourse);
    }

    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, String sessionToken, StudentAttributes student, String newEmail, String courseName,
            boolean hasSection, boolean isOpenOrPublishedEmailSentForTheCourse) {
        super(account, sessionToken, student, null, courseName, hasSection);
        this.newEmail = newEmail;
        this.isOpenOrPublishedEmailSentForTheCourse = isOpenOrPublishedEmailSentForTheCourse;
    }

    public boolean isOpenOrPublishedEmailSentForTheCourse() {
        return isOpenOrPublishedEmailSentForTheCourse;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
