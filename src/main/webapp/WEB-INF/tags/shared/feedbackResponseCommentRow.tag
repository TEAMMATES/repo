<%@ tag description="Feedback Response Comment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<%@ attribute name="fourthIndex" %>
<%@ attribute name="frcIndex" %>
<%@ attribute name="viewType" %>
<%@ attribute name="isOnFeedbackSubmissionEditPage" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean"%>
<%@ attribute name="moderatedPersonEmail" %>
<%@ attribute name="giverRole" %>
<c:choose>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty fourthIndex && not empty frcIndex}">
    <c:set var="divId" value="${fourthIndex}-${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex && not empty viewType}">
    <c:set var="divId" value="${viewType}-${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
    <c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:otherwise>
    <c:set var="divId" value="${frc.commentId}" />
  </c:otherwise>
</c:choose>
<c:choose>
  <c:when test="${giverRole eq 'Instructor'}">
    <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD %></c:set>
    <c:set var="deleteLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE %></c:set>
  </c:when>
  <c:otherwise>
    <c:set var="submitLink"><%= Const.ActionURIs.STUDENT_FEEDBACK_RESPONSE_COMMENT_ADD %></c:set>
    <c:set var="deleteLink"><%= Const.ActionURIs.STUDENT_FEEDBACK_RESPONSE_COMMENT_DELETE %></c:set>
  </c:otherwise>
</c:choose>
<li class="list-group-item list-group-item-warning" id="responseCommentRow-${divId}">
  <div id="commentBar-${divId}" class="row">
    <div class="col-xs-10">
      <span class="text-muted">
        From: ${fn:escapeXml(frc.commentGiverName)} [${frc.createdAt}] ${fn:escapeXml(frc.editedAt)}
      </span>
      <c:if test="${frc.withVisibilityIcon}">
        <span class="glyphicon glyphicon-eye-open"
            data-toggle="tooltip"
            data-placement="top"
            style="margin-left: 5px;"
            title="This response comment is visible to ${frc.whoCanSeeComment}"></span>
      </c:if>
    </div>
    <div class="col-xs-2">
      <c:if test="${frc.editDeleteEnabled}">
        <c:choose>
          <c:when test="${isOnFeedbackSubmissionEditPage}">
            <div class="responseCommentDeleteForm pull-right float-right clearfix">
          </c:when>
          <c:otherwise>
            <form class="responseCommentDeleteForm pull-right">
          </c:otherwise>
        </c:choose>
        <a href="${deleteLink}"
            type="button"
            id="commentdelete-${divId}"
            class="btn btn-default btn-xs icon-button"
            data-toggle="tooltip"
            data-placement="top"
            title="<%= Const.Tooltips.COMMENT_DELETE %>"
            <c:if test="${not frc.editDeleteEnabled}">disabled</c:if>
            <c:if test="${not isSessionOpenForSubmission && isOnFeedbackSubmissionEditPage}">disabled</c:if>>
          <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
        </a>
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_INDEX %>" value="${firstIndex}">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${fn:escapeXml(frc.feedbackResponseId)}">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
        <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
        <c:if test="${data.moderation}">
          <input name="moderatedperson" value="${moderatedPersonEmail}" type="hidden">
        </c:if>
        <c:choose>
          <c:when test="${isOnFeedbackSubmissionEditPage}">
            </div>
          </c:when>
          <c:otherwise>
            </form>
          </c:otherwise>
        </c:choose>
        <a type="button"
            id="commentedit-${divId}"
            <c:choose>
              <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
                class="btn btn-default btn-xs icon-button pull-right show-frc-edit-form"
                data-recipientindex="${firstIndex}" data-giverindex="${secondIndex}"
                data-qnindex="${thirdIndex}" data-frcindex="${frcIndex}"
                <c:if test="${not empty fourthIndex}">data-sectionindex="${fourthIndex}"</c:if>
                <c:if test="${not empty viewType}">data-viewtype="${viewType}"</c:if>
              </c:when>
              <c:otherwise>
                class="btn btn-default btn-xs icon-button pull-right"
              </c:otherwise>
            </c:choose>
            data-toggle="tooltip"
            data-placement="top"
            title="<%= Const.Tooltips.COMMENT_EDIT %>"
            <c:if test="${not frc.editDeleteEnabled}">disabled</c:if>>
          <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
        </a>
      </c:if>
    </div>
  </div>
  <%-- Do not add whitespace between the opening and closing tags --%>
  <div id="plainCommentText-${divId}" style="margin-left: 15px;">${frc.commentText}</div>
  <c:if test="${frc.editDeleteEnabled}">
    <c:choose>
      <c:when test="${giverRole eq 'Instructor'}">
        <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT %></c:set>
      </c:when>
      <c:otherwise>
        <c:set var="submitLink"><%= Const.ActionURIs.STUDENT_FEEDBACK_RESPONSE_COMMENT_ADD %></c:set>
      </c:otherwise>
    </c:choose>
    <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %></c:set>
    <shared:feedbackResponseCommentForm fsIndex="${firstIndex}"
        secondIndex="${secondIndex}"
        thirdIndex="${thirdIndex}"
        fourthIndex="${fourthIndex}"
        frcIndex="${frcIndex}"
        frc="${frc}"
        viewType = "${viewType}"
        divId="${divId}"
        formType="Edit"
        textAreaId="${textAreaId}"
        submitLink="${submitLink}"
        buttonText="Save"
        isOnFeedbackSubmissionEditPage="${isOnFeedbackSubmissionEditPage}"
        moderatedPersonEmail="${moderatedPersonEmail}"
        giverRole="${giverRole}"/>
  </c:if>
</li>
