package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.util.Const;

/**
 * Page Object Model for student feedback results page.
 */
public class StudentFeedbackResultsPage extends AppPage {
    private static final String CURRENT_STUDENT_IDENTIFIER = "You";

    @FindBy(id = "course-id")
    private WebElement courseId;

    @FindBy(id = "session-name")
    private WebElement sessionName;

    @FindBy(id = "opening-time")
    private WebElement sessionOpeningTime;

    @FindBy(id = "closing-time")
    private WebElement sessionClosingTime;

    public StudentFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Feedback Session Results");
    }

    public void verifyFeedbackSessionDetails(FeedbackSessionAttributes feedbackSession) {
        assertEquals(getCourseId(), feedbackSession.getCourseId());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertDateEquals(getOpeningTime(), feedbackSession.getStartTime(), feedbackSession.getTimeZone());
        assertDateEquals(getClosingTime(), feedbackSession.getEndTime(), feedbackSession.getTimeZone());
    }

    public void verifyQuestionDetails(int questionNum, FeedbackQuestionAttributes question) {
        assertEquals(question.getQuestionDetails().getQuestionText(), getQuestionText(questionNum));
        if (!question.getQuestionType().equals(FeedbackQuestionType.TEXT)) {
            assertEquals(getAdditionalInfoString(question), getAdditionalInfo(questionNum));
        }
    }

    public void verifyResponseDetails(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> givenResponses,
                                      List<FeedbackResponseAttributes> otherResponses,
                                      Set<String> visibleGivers, Set<String> visibleRecipients) {
        if (!hasDisplayedResponses(question)) {
            return;
        }
        verifyGivenResponses(question, givenResponses);
        verifyOtherResponses(question, otherResponses, visibleGivers, visibleRecipients);
    }

    public void verifyQuestionNotPresent(int questionNum) {
        try {
            getQuestionResponsesSection(questionNum);
            fail("Question " + questionNum + " should not be present.");
        } catch (NoSuchElementException e) {
            // success
        }
    }

    public void verifyNumScaleStatistics(int questionNum, StudentAttributes student,
                                         List<FeedbackResponseAttributes> receivedResponses) {
        verifyTableRowValues(getNumScaleStatistics(questionNum), getExpectedNumScaleStatistics(student, receivedResponses));
    }

    public void verifyRubricStatistics(int questionNum, FeedbackQuestionAttributes question,
                                       List<FeedbackResponseAttributes> receivedResponses,
                                       List<FeedbackResponseAttributes> otherResponses, Set<String> visibleRecipients,
                                       StudentAttributes currentStudent, Collection<StudentAttributes> students) {
        FeedbackRubricQuestionDetails questionDetails = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        String[][] expectedStatistics = getExpectedRubricStatistics(questionDetails, receivedResponses, false);
        markOptionAsUnselected(getRubricExcludeSelfCheckbox(questionNum));
        verifyTableBodyValues(getRubricStatistics(questionNum), expectedStatistics);

        boolean hasSelfEvaluation = receivedResponses.stream()
                .anyMatch(response -> response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER));
        if (hasSelfEvaluation) {
            expectedStatistics = getExpectedRubricStatistics(questionDetails, receivedResponses, true);
        }
        markOptionAsSelected(getRubricExcludeSelfCheckbox(questionNum));
        verifyTableBodyValues(getRubricStatistics(questionNum), expectedStatistics);

        if (questionDetails.hasAssignedWeights()) {
            // sort by recipient name
            sortRubricPerRecipientStats(questionNum, 1);
            replaceRecipientWithAnonymous(otherResponses, visibleRecipients);
            String[][] expectedStatsPerRecipient = getExpectedRubricStatsPerRecipient(questionDetails, otherResponses,
                    currentStudent, students);
            verifyTableBodyValues(getRubricPerRecipientStats(questionNum), expectedStatsPerRecipient);
        }
    }

    public void verifyContributionStatistics(int questionNum, int[] expectedOwnStats, int[] expectedTeamStats) {
        WebElement questionSection = getQuestionResponsesSection(questionNum);
        String[] ownStatsStrings = getExpectedContribStatistics(expectedOwnStats);
        String[] teamStatsStrings = getExpectedContribStatistics(expectedTeamStats);
        assertEquals(questionSection.findElement(By.id("own-view-me")).getText(), ownStatsStrings[0]);
        assertEquals(questionSection.findElement(By.id("own-view-others")).getText().trim(), ownStatsStrings[1]);
        assertEquals(questionSection.findElement(By.id("team-view-me")).getText(), teamStatsStrings[0]);
        assertEquals(questionSection.findElement(By.id("team-view-others")).getText().trim(), teamStatsStrings[1]);
    }

    public void verifyCommentDetails(int questionNum, String commentGiver, String commentEditor, String commentString) {
        WebElement commentField = getCommentField(questionNum, commentString);
        if (commentGiver.isEmpty()) {
            assertTrue(isCommentByResponseGiver(commentField));
        } else {
            assertEquals(commentGiver, getCommentGiver(commentField));
        }
        if (!commentEditor.isEmpty()) {
            assertEquals(commentEditor, getCommentEditor(commentField));
        }
    }

    private boolean hasDisplayedResponses(FeedbackQuestionAttributes question) {
        return !question.getQuestionDetails().getQuestionType().equals(FeedbackQuestionType.CONTRIB);
    }

    private void verifyGivenResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> givenResponses) {
        for (FeedbackResponseAttributes response : givenResponses) {
            WebElement responseField = getGivenResponseField(question.questionNumber, response.getRecipient());
            assertTrue(isResponseEqual(question, responseField, response));
        }
    }

    private void verifyOtherResponses(FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> otherResponses,
                                      Set<String> visibleGivers, Set<String> visibleRecipients) {
        Set<String> recipients = getRecipients(otherResponses);
        for (String recipient : recipients) {
            List<FeedbackResponseAttributes> expectedResponses = otherResponses.stream()
                    .filter(r -> r.getRecipient().equals(recipient))
                    .collect(Collectors.toList());

            verifyResponseForRecipient(question, recipient, expectedResponses, visibleGivers, visibleRecipients);
        }
    }

    private Set<String> getRecipients(List<FeedbackResponseAttributes> responses) {
        return responses.stream().map(FeedbackResponseAttributes::getRecipient).collect(Collectors.toSet());
    }

    private void verifyResponseForRecipient(FeedbackQuestionAttributes question, String recipient,
                                            List<FeedbackResponseAttributes> otherResponses,
                                            Set<String> visibleGivers, Set<String> visibleRecipients) {
        List<WebElement> responseViews = getAllResponseViews(question.questionNumber);
        for (FeedbackResponseAttributes response : otherResponses) {
            boolean isRecipientVisible = visibleRecipients.contains(response.giver)
                    || recipient.equals(CURRENT_STUDENT_IDENTIFIER);
            boolean isGiverVisible = visibleGivers.contains(response.giver)
                    || (visibleGivers.contains("RECEIVER") && response.getRecipient().equals(CURRENT_STUDENT_IDENTIFIER))
                    || response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER);
            if (isRecipientVisible) {
                int recipientIndex = getRecipientIndex(question.questionNumber, recipient);
                WebElement responseView = responseViews.get(recipientIndex);
                List<WebElement> responsesFields = getAllResponseFields(responseView);
                if (isGiverVisible) {
                    int giverIndex = getGiverIndex(responseView, response.getGiver());
                    assertTrue(isResponseEqual(question, responsesFields.get(giverIndex), response));
                } else {
                    assertTrue(isAnyAnonymousResponseEqual(question, responseView, response));
                }
            } else {
                verifyAnonymousResponseView(question, otherResponses, isGiverVisible);
            }
        }
    }

    private void verifyAnonymousResponseView(FeedbackQuestionAttributes question,
                                             List<FeedbackResponseAttributes> expectedResponses,
                                             boolean isGiverVisible) {
        List<WebElement> anonymousViews = getAllResponseViews(question.questionNumber).stream()
                .filter(v -> isAnonymous(v.findElement(By.id("response-recipient")).getText()))
                .collect(Collectors.toList());
        if (anonymousViews.isEmpty()) {
            throw new RuntimeException("No anonymous views found");
        }

        boolean hasCorrectResponses = true;
        for (WebElement responseView : anonymousViews) {
            hasCorrectResponses = true;
            List<WebElement> responseFields = getAllResponseFields(responseView);
            for (FeedbackResponseAttributes response : expectedResponses) {
                if (isGiverVisible) {
                    int giverIndex = getGiverIndex(responseView, response.getGiver());
                    if (!isResponseEqual(question, responseFields.get(giverIndex), response)) {
                        hasCorrectResponses = false;
                        break;
                    }
                } else if (!isAnyAnonymousResponseEqual(question, responseView, response)) {
                    hasCorrectResponses = false;
                    break;
                }
            }
            if (hasCorrectResponses) {
                break;
            }
        }
        assertTrue(hasCorrectResponses);
    }

    private boolean isResponseEqual(FeedbackQuestionAttributes question, WebElement responseField,
                                    FeedbackResponseAttributes response) {
        if (question.getQuestionType().equals(FeedbackQuestionType.RUBRIC)) {
            return isRubricResponseEqual(responseField, response);
        } else {
            return getAnswerString(question, response.getResponseDetails()).equals(responseField.getText());
        }
    }

    private boolean isRubricResponseEqual(WebElement responseField, FeedbackResponseAttributes response) {
        FeedbackRubricResponseDetails responseDetails = (FeedbackRubricResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            WebElement rubricRow = responseField.findElements(By.cssSelector("#rubric-answers tr")).get(i);
            WebElement rubricCell = rubricRow.findElements(By.tagName("td")).get(answers.get(i) + 1);
            if (rubricCell.findElements(By.className("fa-check")).size() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isAnonymous(String identifier) {
        return identifier.contains(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT);
    }

    private boolean isAnyAnonymousResponseEqual(FeedbackQuestionAttributes question, WebElement responseView,
                                                FeedbackResponseAttributes response) {
        List<WebElement> giverNames = responseView.findElements(By.id("response-giver"));
        List<WebElement> responseFields = getAllResponseFields(responseView);
        for (int i = 0; i < giverNames.size(); i++) {
            if (isAnonymous(giverNames.get(i).getText()) && isResponseEqual(question, responseFields.get(i), response)) {
                return true;
            }
        }
        return false;
    }

    private String getCourseId() {
        return courseId.getText();
    }

    private String getFeedbackSessionName() {
        return sessionName.getText();
    }

    private String getOpeningTime() {
        return sessionOpeningTime.getText();
    }

    private String getClosingTime() {
        return sessionClosingTime.getText();
    }

    private void assertDateEquals(String actual, Instant instant, ZoneId timeZone) {
        String dateStrWithAbbr = getDateStringWithAbbr(instant, timeZone);
        String dateStrWithOffset = getDateStringWithOffset(instant, timeZone);

        assertTrue(actual.equals(dateStrWithAbbr) || actual.equals(dateStrWithOffset));
    }

    private String getDateStringWithAbbr(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy, hh:mm a z")
                .format(instant.atZone(timeZone));
    }

    private String getDateStringWithOffset(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy, hh:mm a X")
                .format(instant.atZone(timeZone));
    }

    private String getQuestionText(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElement(By.id("question-text")).getText().trim();
    }

    private String getMcqAddInfo(FeedbackMcqQuestionDetails questionDetails) {
        String additionalInfo = "Multiple-choice (single answer) question options:\n";
        return appendMultiChoiceInfo(additionalInfo, questionDetails.getGenerateOptionsFor(),
                questionDetails.getMcqChoices(), questionDetails.isOtherEnabled());
    }

    private String getMsqAddInfo(FeedbackMsqQuestionDetails questionDetails) {
        String additionalInfo = "Multiple-choice (multiple answers) question options:\n";
        return appendMultiChoiceInfo(additionalInfo, questionDetails.getGenerateOptionsFor(),
                questionDetails.getMsqChoices(), questionDetails.isOtherEnabled());
    }

    private String appendMultiChoiceInfo(String info, FeedbackParticipantType generateOptionsFor, List<String> choices,
                                         boolean isOtherEnabled) {
        StringBuilder additionalInfo = new StringBuilder(info);
        if (generateOptionsFor.equals(FeedbackParticipantType.NONE)) {
            additionalInfo = appendOptions(additionalInfo, choices);
            if (isOtherEnabled) {
                additionalInfo.append("\nOther");
            }
        } else {
            additionalInfo.append("The options for this question is automatically generated from the list of all ")
                    .append(generateOptionsFor.toDisplayGiverName().toLowerCase())
                    .append('.');

        }
        return additionalInfo.toString();
    }

    private String getRubricAddInfo(FeedbackRubricQuestionDetails questionDetails) {
        StringBuilder additionalInfo = new StringBuilder("Rubric question sub-questions:\n");
        return appendOptions(additionalInfo, questionDetails.getRubricSubQuestions()).toString();
    }

    private String getNumScaleAddInfo(FeedbackNumericalScaleQuestionDetails questionDetails) {
        return "Numerical-scale question:\nMinimum value: " + questionDetails.getMinScale() + ". Increment: "
                + questionDetails.getStep() + ". Maximum value: " + questionDetails.getMaxScale() + ".";
    }

    private String getRankOptionsAddInfo(FeedbackRankOptionsQuestionDetails questionDetails) {
        StringBuilder additionalInfo = new StringBuilder("Rank (options) question options:\n");
        return appendOptions(additionalInfo, questionDetails.getOptions()).toString();
    }

    private String getConstSumOptionsAddInfo(FeedbackConstantSumQuestionDetails questionDetails) {
        StringBuilder additionalInfo = new StringBuilder("Distribute points (among options) question options:\n");
        additionalInfo = appendOptions(additionalInfo, questionDetails.getConstSumOptions());
        if (questionDetails.isPointsPerOption()) {
            additionalInfo.append("\nPoints per option: ");
        } else {
            additionalInfo.append("\nTotal points: ");
        }
        additionalInfo.append(questionDetails.getPoints());
        return additionalInfo.toString();
    }

    private String getConstSumRecipientsAddInfo(FeedbackConstantSumQuestionDetails questionDetails) {
        StringBuilder additionalInfo = new StringBuilder("Distribute points (among recipients) question");
        if (questionDetails.isPointsPerOption()) {
            additionalInfo.append("\nPoints per recipient: ");
        } else {
            additionalInfo.append("\nTotal points: ");
        }
        additionalInfo.append(questionDetails.getPoints());
        return additionalInfo.toString();
    }

    private StringBuilder appendOptions(StringBuilder info, List<String> options) {
        StringBuilder additionalInfo = info;
        for (String option : options) {
            additionalInfo.append(option).append('\n');
        }
        return additionalInfo.deleteCharAt(additionalInfo.length() - 1);
    }

    private WebElement getQuestionResponsesSection(int questionNum) {
        return browser.driver.findElement(By.id("question-" + questionNum + "-responses"));
    }

    private void showAdditionalInfo(int qnNumber) {
        WebElement additionalInfoLink = getQuestionResponsesSection(qnNumber).findElement(By.id("additional-info-link"));
        if (additionalInfoLink.getText().equals("[more]")) {
            click(additionalInfoLink);
            waitUntilAnimationFinish();
        }
    }

    private String getAdditionalInfo(int questionNum) {
        showAdditionalInfo(questionNum);
        return getQuestionResponsesSection(questionNum).findElement(By.id("additional-info")).getText();
    }

    private WebElement getGivenResponseField(int questionNum, String receiver) {
        int recipientIndex = getGivenRecipientIndex(questionNum, receiver);
        return getQuestionResponsesSection(questionNum)
                .findElements(By.cssSelector("#given-responses tm-single-response"))
                .get(recipientIndex);
    }

    private int getGivenRecipientIndex(int questionNum, String recipient) {
        List<WebElement> recipients = getQuestionResponsesSection(questionNum)
                .findElements(By.cssSelector("#given-responses #response-recipient"));
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).getText().split("To: ")[1].equals(recipient)) {
                return i;
            }
        }
        throw new RuntimeException("Recipient not found: " + recipient);
    }

    private String getAdditionalInfoString(FeedbackQuestionAttributes question) {
        switch (question.getQuestionType()) {
        case TEXT:
            return "";
        case MCQ:
            return getMcqAddInfo((FeedbackMcqQuestionDetails) question.getQuestionDetails());
        case MSQ:
            return getMsqAddInfo((FeedbackMsqQuestionDetails) question.getQuestionDetails());
        case RUBRIC:
            return getRubricAddInfo((FeedbackRubricQuestionDetails) question.getQuestionDetails());
        case NUMSCALE:
            return getNumScaleAddInfo((FeedbackNumericalScaleQuestionDetails) question.getQuestionDetails());
        case CONTRIB:
            return "Team contribution question";
        case RANK_OPTIONS:
            return getRankOptionsAddInfo((FeedbackRankOptionsQuestionDetails) question.getQuestionDetails());
        case RANK_RECIPIENTS:
            return "Rank (recipients) question";
        case CONSTSUM_OPTIONS:
            return getConstSumOptionsAddInfo((FeedbackConstantSumQuestionDetails) question.getQuestionDetails());
        case CONSTSUM_RECIPIENTS:
            return getConstSumRecipientsAddInfo((FeedbackConstantSumQuestionDetails) question.getQuestionDetails());
        default:
            throw new RuntimeException("Unknown question type: " + question.getQuestionType());
        }
    }

    private String getAnswerString(FeedbackQuestionAttributes question, FeedbackResponseDetails response) {
        switch(response.getQuestionType()) {
        case TEXT:
        case NUMSCALE:
        case RANK_RECIPIENTS:
            return response.getAnswerString();
        case MCQ:
        case MSQ:
            return response.getAnswerString().replace(", ", "\n");
        case RANK_OPTIONS:
            return getRankOptionsAnsString((FeedbackRankOptionsQuestionDetails) question.getQuestionDetails(),
                    (FeedbackRankOptionsResponseDetails) response);
        case CONSTSUM:
            return getConstSumOptionsAnsString((FeedbackConstantSumQuestionDetails) question.getQuestionDetails(),
                    (FeedbackConstantSumResponseDetails) response);
        case RUBRIC:
        case CONTRIB:
            return "";
        default:
            throw new RuntimeException("Unknown question type: " + response.getQuestionType());
        }
    }

    private String getRankOptionsAnsString(FeedbackRankOptionsQuestionDetails question,
                                           FeedbackRankOptionsResponseDetails responseDetails) {
        List<String> options = question.getOptions();
        List<Integer> answers = responseDetails.getAnswers();
        List<String> answerStrings = new ArrayList<>();
        for (int i = 1; i <= options.size(); i++) {
            answerStrings.add(i + ": " + options.get(answers.indexOf(i)));
        }
        return String.join("\n", answerStrings);
    }

    private String getConstSumOptionsAnsString(FeedbackConstantSumQuestionDetails question,
                                               FeedbackConstantSumResponseDetails responseDetails) {
        if (question.isDistributeToRecipients()) {
            return responseDetails.getAnswerString();
        }
        List<String> options = question.getConstSumOptions();
        List<Integer> answers = responseDetails.getAnswers();
        List<String> answerStrings = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            answerStrings.add(options.get(i) + ": " + answers.get(i));
        }
        answerStrings.sort(Comparator.naturalOrder());
        return String.join("\n", answerStrings);
    }

    private List<WebElement> getAllResponseViews(int questionNumber) {
        return getQuestionResponsesSection(questionNumber).findElements(By.tagName("tm-student-view-responses"));
    }

    private List<WebElement> getAllResponseFields(WebElement responseView) {
        return responseView.findElements(By.tagName("tm-single-response"));
    }

    private String getDoubleString(double value) {
        int numDecimalPlaces = 0;
        if (value % 1 != 0) {
            numDecimalPlaces = Double.toString(value).split("\\.")[1].length();
        }
        if (numDecimalPlaces > 2) {
            numDecimalPlaces = 2;
        }
        return String.format("%." + numDecimalPlaces + "f", value);
    }

    private String[] getExpectedNumScaleStatistics(StudentAttributes student,
                                                   List<FeedbackResponseAttributes> receivedResponses) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double average = 0.0;
        double averageExcludingSelf = 0.0;
        int numSelfResponses = 0;
        for (FeedbackResponseAttributes response : receivedResponses) {
            FeedbackNumericalScaleResponseDetails responseDetails =
                    (FeedbackNumericalScaleResponseDetails) response.getResponseDetails();
            double points = responseDetails.getAnswer();
            if (points > max) {
                max = points;
            }
            if (points < min) {
                min = points;
            }
            if (response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER)) {
                numSelfResponses += 1;
            } else {
                averageExcludingSelf += points;
            }
            average += points;
        }
        average /= receivedResponses.size();
        averageExcludingSelf /= receivedResponses.size() - numSelfResponses;

        return new String[] { student.getTeam(), CURRENT_STUDENT_IDENTIFIER, getDoubleString(average),
                getDoubleString(max), getDoubleString(min), getDoubleString(averageExcludingSelf) };
    }

    private WebElement getNumScaleStatistics(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElement(By.cssSelector("#numscale-statistics tbody tr"));
    }

    private int[][] getRubricResponseScores(FeedbackRubricQuestionDetails questionDetails,
                                            List<FeedbackResponseAttributes> receivedResponses,
                                            boolean isExcludingSelf) {
        int numRows = questionDetails.getNumOfRubricSubQuestions();
        int numCols = questionDetails.getNumOfRubricChoices();
        int[][] rubricResponseScores = new int[numRows][numCols];
        for (FeedbackResponseAttributes response : receivedResponses) {
            FeedbackRubricResponseDetails responseDetails = (FeedbackRubricResponseDetails) response.getResponseDetails();
            List<Integer> answers = responseDetails.getAnswer();
            for (int i = 0; i < answers.size(); i++) {
                if (!isExcludingSelf || !response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER)) {
                    rubricResponseScores[i][answers.get(i)] += 1;
                }
            }
        }
        return rubricResponseScores;
    }

    private String[][] getExpectedRubricStatistics(FeedbackRubricQuestionDetails questionDetails,
                                                   List<FeedbackResponseAttributes> responses,
                                                   boolean isExcludingSelf) {
        int numCols = questionDetails.getNumOfRubricChoices() + 1;
        if (questionDetails.hasAssignedWeights()) {
            numCols++;
        }
        String[][] expectedStatistics = new String[questionDetails.getNumOfRubricSubQuestions()][numCols];

        int[][] rubricResponses = getRubricResponseScores(questionDetails, responses, isExcludingSelf);
        int divisor = responses.size();
        if (isExcludingSelf) {
            divisor -= 1;
        }

        for (int i = 0; i < questionDetails.getNumOfRubricSubQuestions(); i++) {
            expectedStatistics[i][0] = getExpectedRubricSubQuestion(i, questionDetails.getRubricSubQuestions());
            double averageWeight = 0;
            for (int j = 0; j < questionDetails.getNumOfRubricChoices(); j++) {
                expectedStatistics[i][j + 1] = getExpectedRubricValue(rubricResponses[i][j], divisor);
                if (questionDetails.hasAssignedWeights()) {
                    double weight = questionDetails.getRubricWeights().get(i).get(j);
                    expectedStatistics[i][j + 1] += " [" + getDoubleString(weight) + "]";
                    averageWeight += questionDetails.getRubricWeights().get(i).get(j) * rubricResponses[i][j];
                }
            }
            if (questionDetails.hasAssignedWeights()) {
                averageWeight /= divisor;
                expectedStatistics[i][numCols - 1] = getDoubleString(averageWeight);
            }
        }
        return expectedStatistics;
    }

    private String[][] getExpectedRubricStatsPerRecipient(FeedbackRubricQuestionDetails questionDetails,
                                                          List<FeedbackResponseAttributes> otherResponses,
                                                          StudentAttributes currentStudent,
                                                          Collection<StudentAttributes> students) {
        List<String> recipients = new ArrayList<>(getRecipients(otherResponses));
        recipients.sort(Comparator.naturalOrder());
        int numCols = questionDetails.getNumOfRubricChoices() + 5;
        int numRows = questionDetails.getNumOfRubricSubQuestions() * recipients.size();
        String[][] expectedStatsPerRecipient = new String[numRows][numCols];

        for (int n = 0; n < recipients.size(); n++) {
            String recipient = recipients.get(n);
            List<FeedbackResponseAttributes> expectedResponses = otherResponses.stream()
                    .filter(r -> r.getRecipient().equals(recipient))
                    .collect(Collectors.toList());

            int[][] rubricResponses = getRubricResponseScores(questionDetails, expectedResponses, false);
            int startingIndex = n * questionDetails.getNumOfRubricSubQuestions();
            for (int i = startingIndex; i < startingIndex + questionDetails.getNumOfRubricSubQuestions(); i++) {
                if (isAnonymous(recipient)) {
                    expectedStatsPerRecipient[i][0] = "";
                } else if (recipient.equals(CURRENT_STUDENT_IDENTIFIER)) {
                    expectedStatsPerRecipient[i][0] = currentStudent.getTeam();
                } else {
                    expectedStatsPerRecipient[i][0] = students.stream().filter(s -> s.getName().equals(recipient))
                            .findFirst().get().getTeam();
                }
                expectedStatsPerRecipient[i][1] = recipient;
                int index = i % questionDetails.getNumOfRubricSubQuestions();
                expectedStatsPerRecipient[i][2] = getExpectedRubricSubQuestion(index,
                        questionDetails.getRubricSubQuestions());
                double total = 0;
                int divisor = expectedResponses.size();
                for (int j = 0; j < questionDetails.getNumOfRubricChoices(); j++) {
                    double weight = questionDetails.getRubricWeights().get(index).get(j);
                    expectedStatsPerRecipient[i][j + 3] = getExpectedRubricValue(rubricResponses[index][j], divisor)
                            + " [" + getDoubleString(weight) + "]";
                    total += weight * rubricResponses[index][j];
                }
                double averageWeight = total / divisor;
                expectedStatsPerRecipient[i][expectedStatsPerRecipient[i].length - 2] = getDoubleString(total);
                expectedStatsPerRecipient[i][expectedStatsPerRecipient[i].length - 1] = getDoubleString(averageWeight);
            }
        }
        return expectedStatsPerRecipient;
    }

    private String getExpectedRubricSubQuestion(int index, List<String> subQuestions) {
        char alphaIndex = (char) ('a' + index);
        return alphaIndex + ") " + subQuestions.get(index);
    }

    private String getExpectedRubricValue(int rubricResponse, int divisor) {
        double percentage = (double) rubricResponse / divisor * 100;
        return getDoubleString(percentage) + "% (" + rubricResponse + ")";

    }

    private WebElement getRubricExcludeSelfCheckbox(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElement(By.id("exclude-self-checkbox"));
    }

    private WebElement getRubricStatistics(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElement(By.id("rubric-statistics"));
    }

    private WebElement getRubricPerRecipientStats(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElement(By.id("rubric-recipient-statistics"));
    }

    private void sortRubricPerRecipientStats(int questionNum, int colNum) {
        click(getRubricPerRecipientStats(questionNum).findElements(By.tagName("th")).get(colNum));
    }

    private void replaceRecipientWithAnonymous(List<FeedbackResponseAttributes> responses,
                                               Set<String> visibleRecipients) {
        for (FeedbackResponseAttributes response : responses) {
            boolean isRecipientVisible = visibleRecipients.contains(response.giver)
                    || response.getRecipient().equals(CURRENT_STUDENT_IDENTIFIER)
                    || response.getGiver().equals(CURRENT_STUDENT_IDENTIFIER);
            if (!isRecipientVisible) {
                response.recipient = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " student";
            }
        }
    }

    private String[] getExpectedContribStatistics(int[] expectedStats) {
        String[] statsStrings = new String[2];
        statsStrings[0] = "of me: " + getContributionString(expectedStats[0]);
        statsStrings[1] = "of others:  ";
        for (int i = 1; i < expectedStats.length; i++) {
            statsStrings[1] += getContributionString(expectedStats[i]);
            if (i != expectedStats.length - 1) {
                statsStrings[1] += ", ";
            }
        }
        return statsStrings;
    }

    private String getContributionString(int value) {
        StringBuilder contributionString = new StringBuilder("E ");
        if (value > 0) {
            contributionString.append('+');
        }
        return contributionString.append(value).append('%').toString();
    }

    private boolean isCommentByResponseGiver(WebElement commentField) {
        return commentField.findElements(By.id("by-response-giver")).size() > 0;
    }

    private String getCommentGiver(WebElement commentField) {
        String commentGiverDescription = commentField.findElement(By.id("comment-giver-name")).getText();
        return commentGiverDescription.split(" commented")[0];
    }

    private String getCommentEditor(WebElement commentField) {
        String editDescription = commentField.findElement(By.id("last-editor-name")).getText();
        return editDescription.split("edited by ")[1];
    }

    private List<WebElement> getCommentFields(int questionNum) {
        return getQuestionResponsesSection(questionNum).findElements(By.tagName("tm-comment-row"));
    }

    private WebElement getCommentField(int questionNum, String commentString) {
        List<WebElement> commentFields = getCommentFields(questionNum);
        for (WebElement comment : commentFields) {
            if (comment.findElement(By.id("comment-text")).getText().equals(commentString)) {
                return comment;
            }
        }
        throw new RuntimeException("Comment field not found");
    }

    private int getGiverIndex(WebElement response, String giver) {
        List<WebElement> givers = response.findElements(By.id("response-giver"));
        for (int i = 0; i < givers.size(); i++) {
            if (givers.get(i).getText().contains(giver)) {
                return i;
            }
        }
        throw new RuntimeException("Giver not found: " + giver);
    }

    private int getRecipientIndex(int questionNum, String recipient) {
        List<WebElement> recipients = getQuestionResponsesSection(questionNum).findElements(By.id("response-recipient"));
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).getText().split("To: ")[1].equals(recipient)) {
                return i;
            }
        }
        throw new RuntimeException("Recipient not found: " + recipient);
    }
}
