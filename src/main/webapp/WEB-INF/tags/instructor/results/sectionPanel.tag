<%@ tag description="instructorFeedbackResults - by question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionPanel" type="teammates.ui.template.InstructorFeedbackResultsSectionPanel" required="true" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="feedbackSessionName" required="true" %>

<c:set var="groupByTeamEnabled" value="${data.groupByTeam != null && data.groupByTeam == 'on'}"/>

<div class="panel ${sectionPanel.panelClass}">
    <c:choose>
        <c:when test="${!sectionPanel.loadSectionResponsesByAjax}">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-sm-9 panel-heading-text">
                        <strong>${sectionPanel.sectionName}</strong>                        
                    </div>
                    <div class="col-sm-3">
                        <div class="pull-right">
                            <a class="btn btn-success btn-xs" id="collapse-panels-button-section-${sectionIndex}" data-toggle="tooltip" title='Collapse or expand all ${groupByTeamEnabled? "team" : "student"} panels. You can also click on the panel heading to toggle each one individually.'>
                                ${shouldCollapsed ? "Expand " : "Collapse "}${groupByTeamEnabled ? "Teams" : "Students"}
                            </a>
                            &nbsp;
                            <span class="glyphicon glyphicon-chevron-up"></span>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="panel-heading ajax_submit">
                <div class="row">
                    <div class="col-sm-9 panel-heading-text">
                        <strong>${sectionPanel.sectionName}</strong>
                    </div>
                    <div class="col-sm-3">
                        <div class="pull-right">
                            <div class="display-icon" style="display:inline;">
                                <span class="glyphicon glyphicon-chevron-down"></span>
                            </div>
                        </div>
                     </div>
                </div>
    
                <form style="display:none;" id="seeMore-${sectionIndex}" class="seeMoreForm-${sectionIndex}" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${feedbackSessionName}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="${sectionPanel.sectionName}">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="${data.groupByTeam}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="${data.sortType}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-${sectionIndex}">
                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX%>" value="on" id="mainIndex-${sectionIndex}">
                </form>
            </div>
        </c:otherwise>
    </c:choose>
    <div class="panel-collapse collapse <c:if test="${!shouldCollapsed}">in</c:if>">
        <div class="panel-body" id="sectionBody-${sectionIndex}">
            <c:choose>
                <c:when test="${groupByTeamEnabled}">
                    <c:forEach var="teamPanel" items="${sectionPanel.participantPanels}" varStatus="i">
                           <results:teamPanel teamName="${teamPanel.key}" teamIndex="${i.index}" 
                                              showAll="${showAll}" shouldCollapsed="${shouldCollapsed}" 
                                              statsTables="${sectionPanel.teamStatisticsTable[teamPanel.key]}"
                                              detailedResponsesHeaderText="${sectionPanel.detailedResponsesHeaderText}" 
                                              statisticsHeaderText="${sectionPanel.statisticsHeaderText}"
                                              isTeamHasResponses="${sectionPanel.isTeamWithResponses[teamPanel.key]}"
                                              isDisplayingTeamStatistics="${sectionPanel.displayingTeamStatistics}"
                                              isDisplayingMissingParticipants="${sectionPanel.displayingMissingParticipants}"
                                              participantPanels="${teamPanel.value}"/>  
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <c:forEach var="participantPanels" items="${sectionPanel.participantPanels}" varStatus="i">
                        <c:forEach var="participantPanel" items="${participantPanels.value}">
                            <results:participantGroupByQuestionPanel showAll="${showAll}" groupByQuestionPanel="${participantPanel}" shouldCollapsed="${shouldCollapsed}"/>
                        </c:forEach>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
