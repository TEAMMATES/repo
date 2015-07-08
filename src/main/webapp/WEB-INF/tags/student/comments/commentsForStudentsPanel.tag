<%@ tag description="StudentComments - Comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="commentRows" type="java.util.Collection" required="true" %>
<%@ attribute name="courseId" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Comments for students</strong>
    </div>
    <div class="panel-body">
        <c:forEach items="${commentRows}" var="commentRow" varStatus="i">
            <c:set var="commentIdx" value="${i.index + 1}" />
            <div class="panel panel-info student-record-comments giver_display-to-${commentRow.recipientDetails == 'you' ? 'you' : 'others'}">
                <div class="panel-heading">
                    From <b>${commentRow.giverDetails} (${courseId})</b>
                </div>
                <ul class="list-group comments">
                    <li class="list-group-item list-group-item-warning"
                        name="form_commentedit"
                        class="form_comment"
                        id="form_commentedit-${commentIdx}">
                        <div id="commentBar-${commentIdx}">
                            <span class="text-muted">
                                 To <b>${commentRow.recipientDetails}</b> [${commentRow.creationTime}] ${commentRow.editedAt}
                            </span>
                        </div>
                        <div id="plainCommentText${commentIdx}">${commentRow.comment.commentText}</div>
                    </li>
                </ul>
            </div>
        </c:forEach>
    </div>
</div>