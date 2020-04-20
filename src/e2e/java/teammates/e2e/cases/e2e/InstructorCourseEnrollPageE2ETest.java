package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.storage.entity.CourseStudent;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("ICEnrollE2eT.teammates.test").googleId)
                .withCourseId(testData.courses.get("ICEnrollE2eT.CS2104").getId());
        loginAdminToPage(url, InstructorHomePage.class);
        InstructorCourseEnrollPage enrollPage =
                AppPage.getNewPageInstance(browser, url, InstructorCourseEnrollPage.class);

        ______TS("Add rows to enroll spreadsheet");
        int numRowsToAdd = 80;
        enrollPage.addEnrollSpreadsheetRows(numRowsToAdd);
        enrollPage.verifyNumAddedEnrollSpreadsheetRows(numRowsToAdd);

        ______TS("Enroll students to empty course");
        CourseStudent student1 = createCourseStudent("Section 1", "Team 1", "Alice Betsy",
                "alice.b.tmms@gmail.tmt", "Comment for Alice");
        CourseStudent student2 = createCourseStudent("Section 1", "Team 1", "Benny Charles",
                "benny.c.tmms@gmail.tmt", "Comment for Benny");
        CourseStudent student3 = createCourseStudent("Section 2", "Team 2", "Charlie Davis",
                "charlie.d.tmms@gmail.tmt", "Comment for Charlie");

        CourseStudent[] studentsEnrollingToEmptyCourse = {student1, student2, student3};

        enrollPage.enroll(studentsEnrollingToEmptyCourse);
        // verifyStatusMessage('Enrollment successful. Summary given below.');
        enrollPage.verifyResultsPanelContains(studentsEnrollingToEmptyCourse, null, null, null, null);

        // refresh page to confirm enrollment
        enrollPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEnrollPage.class);
        enrollPage.verifyExistingStudentsTableContains(studentsEnrollingToEmptyCourse);

        ______TS("Enroll and modify students in existing course");
        // modify team details of existing student
        student3.setTeamName("Team 3");
        // add valid new student
        CourseStudent student4 = createCourseStudent("Section 2", "Team 2", "Danny Engrid",
                "danny.e.tmms@gmail.tmt", "Comment for Danny");
        // add new student with invalid email
        CourseStudent student5 = createCourseStudent("Section 2", "Team 2", "Invalid Student",
                "invalid.email", "Comment for Invalid");

        // student2 included to test modified without change table
        CourseStudent[] studentsEnrollingToExistingCourse = {student2, student3, student4, student5};
        enrollPage.enroll(studentsEnrollingToExistingCourse);
//        verifyStatusMessage("Some students failed to be enrolled, see the summary below." +
//            'You may check that: ' +
//            '"Section" and "Comment" are optional while "Team", "Name", and "Email" must be filled. ' +
//            '"Section", "Team", "Name", and "Comment" should start with an alphabetical character, ' +
//            'unless wrapped by curly brackets "{}", and should not contain vertical bar "|" and percentage sign"%". ' +
//            '"Email" should contain some text followed by one \'@\' sign followed by some more text. ' +
//            '"Team" should not have same format of email to avoid mis-interpretation. '
//        );

        CourseStudent[] newStudentsData = {student4};
        CourseStudent[] modifiedStudentsData = {student3};
        CourseStudent[] modifiedWithoutChangeStudentsData = {student2};
        CourseStudent[] errorStudentsData = {student5};
        CourseStudent[] unmodifiedStudentsData = {student1};

        enrollPage.verifyResultsPanelContains(newStudentsData, modifiedStudentsData, modifiedWithoutChangeStudentsData,
                errorStudentsData, unmodifiedStudentsData);

        // refresh page to confirm enrollment
        enrollPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEnrollPage.class);
        CourseStudent[] expectedExistingData = {student1, student2, student3, student4};
        enrollPage.verifyExistingStudentsTableContains(expectedExistingData);
    }

    private CourseStudent createCourseStudent(String section, String team, String name, String email, String comments) {
        return new CourseStudent(email, name, null, comments, null, team, section);
    }
}
