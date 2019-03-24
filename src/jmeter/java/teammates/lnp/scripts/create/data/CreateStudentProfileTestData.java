package teammates.lnp.scripts.create.data;

import org.json.JSONObject;

/**
 * Script to create data for Student Profile endpoint.
 */
public final class CreateStudentProfileTestData extends CreateTestData {

    private static final int NUMBER_OF_USER_ACCOUNTS = 100;
    private static final String USER_NAME = "DummyUser";
    private static final String USER_EMAIL = "personalEmail";

    public CreateStudentProfileTestData() {
        pathToOutputJson = "/studentProfile.json";
    }

    @Override
    public JSONObject createJsonData() {
        JSONObject studentData = new JSONObject();

        // course data
        JSONObject courseData = new JSONObject();
        courseData.put("id", "TestData.CS101");
        courseData.put("name", "Intro To Programming");
        courseData.put("timeZone", "UTC");
        studentData.put("courses", new JSONObject().put("course", courseData));

        // add user accounts
        JSONObject user = new JSONObject();
        for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
            JSONObject userAccountData = new JSONObject();
            userAccountData.put("googleId", USER_NAME + i + ".tmms");
            userAccountData.put("name", USER_NAME + i);
            userAccountData.put("isInstructor", false);
            userAccountData.put("email", USER_EMAIL + i + "@gmail.tmt");
            userAccountData.put("institute", "TEAMMATES Test Institute 1");
            user.put(USER_NAME + i, userAccountData);
        }

        JSONObject instructorAccount = new JSONObject();
        instructorAccount.put("googleId", "TestData.instructor");
        instructorAccount.put("name", "TEAMMATES Test Instructor");
        instructorAccount.put("isInstructor", true);
        instructorAccount.put("email", "tmms.test@gmail.tmt");
        instructorAccount.put("institute", "TEAMMATES Test Institute 1");
        user.put("instructor", instructorAccount);
        studentData.put("accounts", user);

        // feedbackQuestions
        studentData.put("feedbackQuestions", new JSONObject());

        // feedbackResponses
        studentData.put("feedbackResponses", new JSONObject());

        // feedbackResponseComments
        studentData.put("feedbackResponseComments", new JSONObject());

        // open feedbackSessions
        JSONObject session = new JSONObject();
        session.put("feedbackSessionName", "First Session");
        session.put("courseId", "TestData.CS101");
        session.put("creatorEmail", "tmms.test@gmail.tmt");
        session.put("instructions", "Instructions for first session");
        session.put("createdTime", "2018-04-01T23:59:00Z");
        session.put("startTime", "2018-04-01T21:59:00Z");
        session.put("endTime", "2026-04-30T21:59:00Z");
        session.put("sessionVisibleFromTime", "2018-04-01T21:59:00Z");
        session.put("resultsVisibleFromTime", "2026-05-01T21:59:00Z");
        session.put("timeZone", "Africa/Johannesburg");
        session.put("gracePeriod", 10);
        session.put("sentOpenEmail", true);
        session.put("sentClosingEmail", false);
        session.put("sentPublishedEmail", false);
        session.put("isOpeningEmailEnabled", false);
        session.put("isClosingEmailEnabled", false);
        session.put("isPublishedEmailEnabled", false);

        studentData.put("feedbackSessions", new JSONObject().put("openSession", session));

        // students
        JSONObject students = new JSONObject();
        for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
            JSONObject studentAccountData = new JSONObject();
            studentAccountData.put("googleId", USER_NAME + i + ".tmms");
            studentAccountData.put("email", USER_EMAIL + i + "@gmail.tmt");
            studentAccountData.put("course", "TestData.CS101");
            studentAccountData.put("name", USER_NAME + i);
            studentAccountData.put("comments", "This student's name is " + USER_NAME + i);
            studentAccountData.put("team", "Team 1");
            studentAccountData.put("section", "None");
            students.put(USER_NAME + i, studentAccountData);
        }
        studentData.put("students", students);

        // student profiles
        JSONObject profiles = new JSONObject();
        for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
            JSONObject profileData = new JSONObject();
            profileData.put("googleId", USER_NAME + i + ".tmms");
            profileData.put("email", USER_EMAIL + i + "@gmail.tmt");
            profileData.put("shortName", i);
            profileData.put("institute", "TEAMMATES Test Institute 222");
            profileData.put("moreInfo", "I am " + i);
            profileData.put("pictureKey", "");
            profileData.put("gender", "MALE");
            profileData.put("nationality", "American");

            profiles.put(USER_NAME + i, profileData);
        }
        studentData.put("profiles", profiles);

        // instructors
        JSONObject instructor = new JSONObject();
        JSONObject instructorDetails = new JSONObject();
        instructorDetails.put("googleId", "TestData.instructor");
        instructorDetails.put("courseId", "TestData.CS101");
        instructorDetails.put("name", "Teammates Test");
        instructorDetails.put("email", "tmms.test@gmail.tmt");
        instructorDetails.put("role", "Co-owner");
        instructorDetails.put("isDisplayedToStudents", true);
        instructorDetails.put("displayedName", "Co-owner");
        instructorDetails.put("sectionLevel", new JSONObject());
        instructorDetails.put("sessionLevel", new JSONObject());

        JSONObject privileges = new JSONObject();
        JSONObject courseLevel = new JSONObject();
        courseLevel.put("canviewstudentinsection", true);
        courseLevel.put("cansubmitsessioninsection", true);
        courseLevel.put("canmodifysessioncommentinsection", true);
        courseLevel.put("canmodifycourse", true);
        courseLevel.put("canviewsessioninsection", true);
        courseLevel.put("canmodifysession", true);
        courseLevel.put("canmodifystudent", true);
        courseLevel.put("canmodifyinstructor", true);
        privileges.put("courseLevel", courseLevel);

        instructorDetails.put("privileges", privileges);
        instructor.put("teammates.test.instructor", instructorDetails);
        studentData.put("instructors", instructor);

        return studentData;
    }

}
