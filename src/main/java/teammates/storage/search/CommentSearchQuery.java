package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Document;

/**
 * The SearchQuery object that defines how we query {@link Document} for student comments
 */
public class CommentSearchQuery extends BaseCommentSearchQuery {
    
    public CommentSearchQuery(String googleId, String queryString, String cursorString) {
        super(googleId, queryString, cursorString);
    }

    protected StringBuilder prepareVisibilityQueryString(String googleId) {
        StringBuilder courseIdLimit = super.prepareVisibilityQueryString(googleId);
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        StringBuilder giverEmailLimit = new StringBuilder("(");
        String delim = "";
        for(InstructorAttributes ins: instructorRoles){
            giverEmailLimit.append(delim).append(ins.email);
            delim = OR;
        }
        giverEmailLimit.append(")");

        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString()
                + AND + "(" + Const.SearchDocumentField.GIVER_EMAIL + ":" + giverEmailLimit.toString() 
                        + OR + Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR + ":true)";
        return null;
    }

}
