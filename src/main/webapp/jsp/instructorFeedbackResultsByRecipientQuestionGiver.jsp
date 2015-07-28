<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">    
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByRQG.js"></script>
</c:set>

<results:resultsPage pageTitle="TEAMMATES - Feedback Session Results" bodyTitle="Session Results" jsIncludes="${jsIncludes}" data="${data}">
    <results:byParticipantQuestionParticipant isGroupedByQuestion="${true}" showAll="${data.bundle.complete}" shouldCollapsed="${data.shouldCollapsed}" isGroupedByTeam="${data.groupedByTeam}" />    
</results:resultsPage>
