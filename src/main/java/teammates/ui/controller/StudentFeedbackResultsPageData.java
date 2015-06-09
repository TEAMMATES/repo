package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;

public class StudentFeedbackResultsPageData extends PageData {
    public FeedbackSessionResultsBundle bundle = null;
    
    public StudentFeedbackResultsPageData(AccountAttributes account, StudentAttributes student) {
        super(account, student);
    }
}
