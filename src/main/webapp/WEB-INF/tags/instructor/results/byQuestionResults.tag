<%@ tag description="instructorFeedbackResults - by question" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="questionPanels" type="java.util.List" required="true" %>
<%@ attribute name="isShowingResponses" type="java.lang.Boolean" required="true" %>

<br>
<input type="hidden" id="is-showing-responses" value="${isShowingResponses}">
<c:forEach items="${questionPanels}" var="questionPanel" varStatus="i">
    <results:questionPanel questionIndex="${i.index}" isShowingResponses="${isShowingResponses}" 
                           questionPanel="${questionPanel}"/>
</c:forEach>
