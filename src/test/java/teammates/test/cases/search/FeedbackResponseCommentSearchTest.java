package teammates.test.cases.search;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.FeedbackResponseCommentsDb;

/**
 * SUT: {@link FeedbackResponseCommentsDb},
 *      {@link teammates.storage.search.FeedbackResponseCommentDocument},
 *      {@link teammates.storage.search.FeedbackResponseCommentSearchQuery}.
 */
public class FeedbackResponseCommentSearchTest extends BaseSearchTest {

    @Test
    public void allTests() {
        FeedbackResponseCommentsDb commentsDb = new FeedbackResponseCommentsDb();

        FeedbackResponseCommentAttributes frc1I1Q1S1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes frc1I1Q2S1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q2S1C1");
        ArrayList<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();
        instructors.add(dataBundle.instructors.get("instructor1OfCourse1"));

        ______TS("success: search for comments in data bundle; query string does not match any comment");

        FeedbackResponseCommentSearchResultBundle bundle = commentsDb.search("non-existent", instructors);
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.comments.isEmpty());

        ______TS("success: search for comments in data bundle; query string matches single comment");

        bundle = commentsDb.search("\"Instructor 1 comment to student 1 self feedback Question 2\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1);

        ______TS("success: search for comments in instructor's course; query string matches some comments");

        bundle = commentsDb.search("\"self feedback\"", instructors);
        verifySearchResults(bundle, frc1I1Q1S1, frc1I1Q2S1);

        ______TS("success: search for comments in instructor's course; confirms query string is case insensitive");

        bundle = commentsDb.search("\"Instructor 1 COMMENT to student 1 self feedback Question 2\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1);

        ______TS("success: search for comments in data bundle using feedbackSessionName");

        bundle = commentsDb.search("\"First feedback session\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1, frc1I1Q1S1);

        ______TS("success: search for comments in data bundle using Instructor's email");

        bundle = commentsDb.search("instructor1@course1.tmt", instructors);
        verifySearchResults(bundle, frc1I1Q2S1, frc1I1Q1S1);

        ______TS("success: search for comments in data bunndle using Student name");

        bundle = commentsDb.search("Student in two courses", instructors);
        verifySearchResults(bundle, frc1I1Q1S1, frc1I1Q2S1);

        ______TS("success: search for comments in data bundle; confirms deleted comments are not included in results");

        commentsDb.deleteDocument(frc1I1Q2S1);
        bundle = commentsDb.search("\"Instructor 1 comment to student 1 self feedback Question 2\"", instructors);
        verifySearchResults(bundle);

    }

    /*
     * Verifies that search results match with expected output.
     * Compares the text for each comment as it is unique.
     *
     * @param actual the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(FeedbackResponseCommentSearchResultBundle actual,
                FeedbackResponseCommentAttributes... expected) {
        assertEquals(expected.length, actual.numberOfResults);
        assertEquals(expected.length, actual.comments.size());
        FeedbackResponseCommentAttributes.sortFeedbackResponseCommentsByCreationTime(Arrays.asList(expected));
        FeedbackResponseCommentAttributes[] sortedComments = Arrays.asList(expected)
                .toArray(new FeedbackResponseCommentAttributes[2]);
        int i = 0;
        for (String key : actual.comments.keySet()) {
            for (FeedbackResponseCommentAttributes comment : actual.comments.get(key)) {
                assertEquals(sortedComments[i].commentText, comment.commentText);
                i++;
            }
        }
    }
}
