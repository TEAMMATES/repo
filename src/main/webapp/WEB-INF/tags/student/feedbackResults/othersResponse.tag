<%@ tag description="othersResponseTable.tag - Others-responses given to a particular recipient" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<%@ attribute name="othersResponse" type="teammates.ui.template.FeedbackResultsResponse" required="true" %>

<tr class="resultSubheader">
  <td>
    <span class="bold"><b>From:</b></span> ${fn:escapeXml(othersResponse.giverName)}
  </td>
</tr>

<tr>
  <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
  <td class="text-preserve-space">${othersResponse.answer}</td>
</tr>

<c:if test="${not empty othersResponse.comments}">
  <tr>
    <td>
      <ul class="list-group comment-list">
        <c:forEach items="${othersResponse.comments}" var="comment">
          <shared:feedbackResponseCommentRow frc="${comment}" />
        </c:forEach>
      </ul>
    </td>
  </tr>
</c:if>
