package teammates.ui.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;

public class FeedbackResultsTable {

    private String studentName;
    private List<FeedbackResponsePersonRow> receivedResponses;
    private List<FeedbackResponsePersonRow> givenResponses;

    public FeedbackResultsTable(int fbIndex, String studentName, String studentEmail, FeedbackSessionResultsBundle result) {
        this.studentName = studentName;

        this.receivedResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> received =
                                        result.getResponsesSortedByRecipient().get(studentName);
        int giverIndex = 0;
        if (received != null) {
            FeedbackResponseAttributes contriFeedbackResponse = findContriFeedbackResponse(received);
            if (contriFeedbackResponse != null && contriFeedbackResponse.recipient.equals(studentEmail)) {
                received.computeIfAbsent(studentName, k -> new ArrayList<>());
            }
            received = sortByKey(received);
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : received.entrySet()) {
                giverIndex++;
                if (contriFeedbackResponse != null && entry.getKey().equals(this.studentName)) {
                    List<FeedbackResponseAttributes> newResponseReceived = entry.getValue();
                    newResponseReceived.add(contriFeedbackResponse);
                    FeedbackResponseAttributes.sortFeedbackResponses(newResponseReceived);

                    this.receivedResponses.add(new FeedbackResponsePersonRow(fbIndex, giverIndex, entry.getKey(),
                                    "giver", newResponseReceived, result, true));
                } else {
                    this.receivedResponses.add(new FeedbackResponsePersonRow(fbIndex, giverIndex, entry.getKey(),
                                    "giver", entry.getValue(), result, false));
                }
            }
        }

        this.givenResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> given = result.getResponsesSortedByGiver().get(studentName);
        int recipientIndex = 0;
        if (given != null) {
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : given.entrySet()) {
                recipientIndex++;
                this.givenResponses.add(new FeedbackResponsePersonRow(fbIndex, recipientIndex, entry.getKey(), "recipient",
                                                                      entry.getValue(), result, false));
            }
        }
    }

    private FeedbackResponseAttributes findContriFeedbackResponse(
            Map<String, List<FeedbackResponseAttributes>> rcvedFeedbacks) {
        FeedbackResponseAttributes[] newResponseDetails = new FeedbackResponseAttributes[1];
        boolean[] hasContriFeedbackResponseRow = {false};

        rcvedFeedbacks.entrySet().forEach(entry -> {
            for (FeedbackResponseAttributes questionResponses : entry.getValue()) {
                if (questionResponses.feedbackQuestionType.equals(FeedbackQuestionType.CONTRIB)) {
                    newResponseDetails[0] = new FeedbackResponseAttributes(questionResponses);
                    if (entry.getKey().equals(this.studentName)) {
                        hasContriFeedbackResponseRow[0] = true;
                    }
                }
            }
        });
        if (newResponseDetails[0] != null && !hasContriFeedbackResponseRow[0]) {
            return newResponseDetails[0];
        }
        return null;
    }

    private Map<String, List<FeedbackResponseAttributes>> sortByKey(Map<String, List<FeedbackResponseAttributes>> received) {
        LinkedHashMap<String, List<FeedbackResponseAttributes>> sortedMap = new LinkedHashMap<>();
        received.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }

    public String getStudentName() {
        return studentName;
    }

    public List<FeedbackResponsePersonRow> getReceivedResponses() {
        return receivedResponses;
    }

    public List<FeedbackResponsePersonRow> getGivenResponses() {
        return givenResponses;
    }
}
