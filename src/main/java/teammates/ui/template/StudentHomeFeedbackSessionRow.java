package teammates.ui.template;

public class StudentHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String endTime;
    private StudentFeedbackSessionActions actions;
    private int index;

    public StudentHomeFeedbackSessionRow(String name, String tooltip, String submissionStatus,
            String publishedStatus, String endTime, StudentFeedbackSessionActions actions, int index) {
        super(name, tooltip, submissionStatus, publishedStatus);
        this.endTime = endTime;
        this.actions = actions;
        this.index = index;
    }

    public String getEndTime() {
        return endTime;
    }

    public StudentFeedbackSessionActions getActions() {
        return actions;
    }

    public int getIndex() {
        return index;
    }
}
