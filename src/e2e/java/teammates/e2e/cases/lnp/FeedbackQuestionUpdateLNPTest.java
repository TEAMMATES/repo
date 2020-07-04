package teammates.e2e.cases.lnp;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.e2e.util.JMeterElements;
import teammates.e2e.util.LNPSpecification;
import teammates.e2e.util.LNPTestData;
import teammates.ui.webapi.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.webapi.request.FeedbackQuestionUpdateRequest;

/**
* L&P Test Case for feedback question update cascade API.
*/
public class FeedbackQuestionUpdateLNPTest extends BaseLNPTestCase {
    private static final int NUM_INSTRUCTORS = 1;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUMBER_OF_FEEDBACK_QUESTIONS = 10;
    private static final int NUMBER_OF_FEEDBACK_RESPONSES = 500;

    private static final String COURSE_ID = "TestData.CS101";
    private static final String COURSE_NAME = "LnPCourse";
    private static final String COURSE_TIME_ZONE = "UTC";

    private static final String INSTRUCTOR_ID = "LnPInstructor_id";
    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String HAS_ADMIN_PRIVILEGE = "no";

    private static final String STUDENT_ID = "LnPStudent.tmms";
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String TEAM_NAME = "Team 1";
    private static final String GIVER_SECTION_NAME = "Section 1";
    private static final String RECEIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";
    private static final String FEEDBACK_QUESTION_TEXT = "Test Question";

    private static final String UPDATE_FEEDBACK_QUESTION_BRIEF = "update the new question brief";
    private static final String UPDATE_FEEDBACK_QUESTION_TEXT = "update the new question text";

    private static final String FEEDBACK_RESPONSE_ID = "ResponseForQ";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 1;

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new HashMap<>();

                courses.put(COURSE_NAME, CourseAttributes.builder(COURSE_ID)
                        .withName(COURSE_NAME)
                        .withTimezone(ZoneId.of(COURSE_TIME_ZONE))
                        .build());

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new HashMap<>();

                instructors.put(INSTRUCTOR_NAME,
                        InstructorAttributes.builder(COURSE_ID, INSTRUCTOR_EMAIL)
                            .withGoogleId(INSTRUCTOR_ID)
                            .withName(INSTRUCTOR_NAME)
                            .withRole("Co-owner")
                            .withIsDisplayedToStudents(true)
                            .withDisplayedName("Co-owner")
                            .withPrivileges(new InstructorPrivileges(
                                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER))
                            .build()
                );

                return instructors;
            }

            @Override
            protected Map<String, StudentAttributes> generateStudents() {
                Map<String, StudentAttributes> students = new LinkedHashMap<>();
                StudentAttributes studentAttribute;

                studentAttribute = StudentAttributes.builder(COURSE_ID, STUDENT_EMAIL)
                        .withGoogleId(STUDENT_ID)
                        .withName(STUDENT_NAME)
                        .withComment("This student's name is " + STUDENT_NAME)
                        .withSectionName(GIVER_SECTION_NAME)
                        .withTeamName(TEAM_NAME)
                        .build();

                students.put(STUDENT_NAME, studentAttribute);

                return students;
            }

            @Override
            protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
                Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();

                FeedbackSessionAttributes session = FeedbackSessionAttributes
                        .builder(FEEDBACK_SESSION_NAME, COURSE_ID)
                        .withCreatorEmail(INSTRUCTOR_EMAIL)
                        .withStartTime(Instant.now())
                        .withEndTime(Instant.now().plusSeconds(500))
                        .withSessionVisibleFromTime(Instant.now())
                        .withResultsVisibleFromTime(Instant.now())
                        .build();

                feedbackSessions.put(FEEDBACK_SESSION_NAME, session);

                return feedbackSessions;
            }

            @Override
            protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
                ArrayList<FeedbackParticipantType> showResponses = new ArrayList<>();
                showResponses.add(FeedbackParticipantType.RECEIVER);
                showResponses.add(FeedbackParticipantType.INSTRUCTORS);

                ArrayList<FeedbackParticipantType> showGiverName = new ArrayList<>();
                showGiverName.add(FeedbackParticipantType.INSTRUCTORS);

                ArrayList<FeedbackParticipantType> showRecepientName = new ArrayList<>();
                showRecepientName.add(FeedbackParticipantType.INSTRUCTORS);

                Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
                FeedbackQuestionDetails details = new FeedbackTextQuestionDetails(FEEDBACK_QUESTION_TEXT);

                for (int i = 1; i <= NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
                    feedbackQuestions.put(FEEDBACK_QUESTION_ID + " " + i,
                            FeedbackQuestionAttributes.builder()
                                    .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                    .withQuestionDescription(FEEDBACK_QUESTION_TEXT)
                                    .withCourseId(COURSE_ID)
                                    .withQuestionDetails(details)
                                    .withQuestionNumber(1)
                                    .withGiverType(FeedbackParticipantType.STUDENTS)
                                    .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                                    .withShowResponsesTo(showResponses)
                                    .withShowGiverNameTo(showGiverName)
                                    .withShowRecipientNameTo(showRecepientName)
                                    .build()
                    );
                }

                return feedbackQuestions;
            }

            @Override
            protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
                Map<String, FeedbackResponseAttributes> feedbackResponses = new HashMap<>();

                for (int i = 1; i <= NUMBER_OF_FEEDBACK_RESPONSES; i++) {
                    String responseText = FEEDBACK_RESPONSE_ID + " " + i;
                    FeedbackTextResponseDetails details =
                            new FeedbackTextResponseDetails(responseText);

                    feedbackResponses.put(responseText,
                            FeedbackResponseAttributes.builder("1",
                                STUDENT_EMAIL,
                                STUDENT_EMAIL)
                                .withCourseId(COURSE_ID)
                                .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                .withGiverSection(GIVER_SECTION_NAME)
                                .withRecipientSection(RECEIVER_SECTION_NAME)
                                .withResponseDetails(details)
                                .build());
                }

                return feedbackResponses;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("isAdmin");
                headers.add("courseId");
                headers.add("studentId");
                headers.add("studentEmail");
                headers.add("fsname");

                for (int i = 1; i <= NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
                    headers.add("fqname_" + i);
                }

                for (int i = 1; i <= NUMBER_OF_FEEDBACK_RESPONSES; i++) {
                    headers.add("frname_" + i);
                }

                headers.add("updateData");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(INSTRUCTOR_ID);
                    csvRow.add(HAS_ADMIN_PRIVILEGE);
                    csvRow.add(COURSE_ID);
                    csvRow.add(STUDENT_ID);
                    csvRow.add(STUDENT_EMAIL);
                    csvRow.add(FEEDBACK_SESSION_NAME);

                    for (int i = 1; i <= NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
                        csvRow.add(FEEDBACK_QUESTION_ID + " " + i);
                    }

                    for (int i = 1; i <= NUMBER_OF_FEEDBACK_RESPONSES; i++) {
                        csvRow.add(FEEDBACK_RESPONSE_ID + " " + i);
                    }

                    FeedbackQuestionUpdateRequest feedbackQuestionUpdateRequest =
                            getTypicalTextQuestionUpdateRequest();

                    String updateData = sanitizeForCsv(JsonUtils.toJson(feedbackQuestionUpdateRequest));
                    csvRow.add(updateData);

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    private FeedbackQuestionUpdateRequest getTypicalTextQuestionUpdateRequest() {
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);

        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(1);
        updateRequest.setQuestionBrief(UPDATE_FEEDBACK_QUESTION_BRIEF);
        updateRequest.setQuestionDescription(UPDATE_FEEDBACK_QUESTION_TEXT);

        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.TEXT);
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        updateRequest.setShowResponsesTo(new ArrayList<>());
        updateRequest.setShowGiverNameTo(new ArrayList<>());
        updateRequest.setShowRecipientNameTo(new ArrayList<>());

        return updateRequest;
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("X-CSRF-TOKEN", "${csrfToken}");
        headers.put("Content-Type", "application/json");

        return headers;
    }

    private String getTestEndpoint() {
        return Const.ResourceURIs.URI_PREFIX + Const.ResourceURIs.QUESTION
            + "?courseid=${courseId}&fsname=${fsname}&questionid=${fqname_1}";
    }

    @Override
    protected ListedHashTree getLnpTestPlan() {
        ListedHashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(NUM_INSTRUCTORS, RAMP_UP_PERIOD, 1));

        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());

        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler())
                .add(JMeterElements.csrfExtractor("csrfToken"));

        // Add HTTP sampler for test endpoint
        HeaderManager headerManager = JMeterElements.headerManager(getRequestHeaders());
        threadGroup.add(JMeterElements.httpSampler(getTestEndpoint(), POST, "${updateData}"))
                .add(headerManager);

        return testPlan;
    }

    @Override
    protected void setupSpecification() {
        this.specification = LNPSpecification.builder()
                .withErrorRateLimit(ERROR_RATE_LIMIT)
                .withMeanRespTimeLimit(MEAN_RESP_TIME_LIMIT)
                .build();
    }

    @BeforeClass
    public void classSetup() {
        generateTimeStamp();
        createTestData();
        setupSpecification();
    }

    @Test
    public void runLnpTest() throws IOException {
        runJmeter(false);
        displayLnpResults();
    }

    @AfterClass
    public void classTearDown() throws IOException {
        deleteTestData();
        deleteDataFiles();
        cleanupResults();
    }
}
