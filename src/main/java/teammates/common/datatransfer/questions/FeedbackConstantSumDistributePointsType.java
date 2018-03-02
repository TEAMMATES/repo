package teammates.common.datatransfer.questions;

/**
 * Enum that defines different distribute points options for constant sum questions.
 */
public enum FeedbackConstantSumDistributePointsType {
    DISTRIBUTE_ALL_UNEVENLY("All options"),
    DISTRIBUTE_SOME_UNEVENLY("At least some options"),
    NONE("None");

    private String displayedOption;

    FeedbackConstantSumDistributePointsType(String displayedOption) {
        this.displayedOption = displayedOption;
    }

    /**
     * Gets {@code displayedOption} that is associated with a particular distribute points option.
     */
    public String getDisplayedOption() {
        return displayedOption;
    }
}
