<%@ tag description="instructorFeedbacks - new feedback session form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>

<%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>

<div class="well well-plain">
    <form class="form-group" method="post"
        action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD %>"
        name="form_addfeedbacksession">
        <div class="row">
            <h4 class="label-control col-md-2 text-md">Create new </h4>
            <div class="col-md-5">
                <div class="col-md-10" title="Select a session type here."
                    data-toggle="tooltip" data-placement="top">
                    <select class="form-control"
                        name="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>"
                        id="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>">
                        <c:forEach items="${fsForm.feedbackSessionTypeOptions}" var="option">
                            <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                ${option.content}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1">
                    <h5>
                        <a href="/instructorHelp.html#fbSetupSession" target="_blank">
                            <span class="glyphicon glyphicon-info-sign"></span>
                        </a>
                    </h5>
                </div>
            </div>
            <h4 class="label-control col-md-1 text-md">Or: </h4>
            <div class="col-md-3">
                <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Loading...</a>
            </div>
        </div>
        <br>

        <div class="panel panel-primary">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-6"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_COURSE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group<c:if test="${empty fsForm.courses}"> has-error</c:if>">
                            <h5 class="col-sm-4">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.COURSE_ID %>">
                                    Course ID
                                </label>
                            </h5>
                            <div class="col-sm-8">
                                <select class="form-control<c:if test="${empty fsForm.courses}"> text-color-red</c:if>"
                                    name="<%= Const.ParamsNames.COURSE_ID %>"
                                    id="<%= Const.ParamsNames.COURSE_ID %>">
                                    <c:forEach items="${fsForm.coursesSelectField}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"
                                        > ${attr.key}="${attr.value}"</c:forEach> >${option.content}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_TIMEZONE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-4">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>">
                                    Timezone
                                </label>
                            </h5>
                            <div class="col-sm-8">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>">
                                    <c:forEach items="${fsForm.timezoneSelectField}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                    
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <div class="row">
                    <div class="col-md-12"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-2">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>">
                                    Session name
                                </label>
                            </h5>
                            <div class="col-sm-10">
                                <input class="form-control" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                                    maxlength=<%= FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH %>
                                    placeholder="e.g. Feedback for Project Presentation 1"
                                    value="${fsForm.fsName}">
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <div class="row" id="instructionsRow">
                    <div class="col-md-12"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="form-group">
                            <h5 class="col-sm-2">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>">
                                    Instructions
                                </label>
                            </h5>
                            <div class="col-sm-10">
                                <textarea class="form-control"
                                    rows="4" cols="100%"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>"
                                    placeholder="e.g. Please answer all the given questions."><c:out value="${fsForm.instructions}"/></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="panel panel-primary" id="timeFramePanel">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-5"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_STARTDATE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-6">
                                <label class="label-control"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>">
                                    Submission opening time
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <input class="form-control col-sm-2" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                                    value="${fsForm.fsStartDate}"
                                    placeholder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>">
                                     <c:forEach items="${fsForm.fsStartTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-5 border-left-gray"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_ENDDATE %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-6">
                                <label class="label-control"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>">
                                    Submission closing time
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <input class="form-control col-sm-2" type="text"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                                    value="${fsForm.fsEndDate}"
                                    placeHolder="Date">
                            </div>
                            <div class="col-md-6">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>">
                                    <c:forEach items="${fsForm.fsEndTimeOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 border-left-gray"
                        title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_GRACEPERIOD %>"
                        data-toggle="tooltip"
                        data-placement="top">
                        <div class="row">
                            <div class="col-md-12">
                                <label class="control-label"
                                    for="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                                    Grace period
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-12">
                                <select class="form-control"
                                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>"
                                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                                    <c:forEach items="${fsForm.gracePeriodOptions}" var="option">
                                        <option <c:forEach items="${option.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach> >
                                            ${option.content}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" id="uncommonSettingsInfo">
            <div class="col-md-12 text-muted">
                <span id="uncommonSettingsInfoText"></span>
                <a id="editUncommonSettingsButton" data-edit="[Edit]" data-done="[Done]">[Edit]</a>
                <br><br>
            </div>
        </div>
        <jsp:doBody/>
        <div class="form-group">
            <div class="row">
                <div class="col-md-offset-5 col-md-3">
                    <button id="button_submit" type="submit" class="btn btn-primary"
                        <c:if test="${fsForm.submitButtonDisabled}">disabled="disabled"</c:if>>
                        Create Feedback Session
                    </button>
                </div>
            </div>
        </div>
        <c:if test="${empty fsForm.courses}"> 
            <div class="row">
                <div class="col-md-12 text-center">
                    <b>You need to have an active(unarchived) course to create a session!</b>
                </div>
            </div>
        </c:if>
        <input type="hidden"
            name="<%= Const.ParamsNames.USER_ID %>"
            value="${data.account.googleId}">
    </form>
    <form style="display:none;" id="ajaxForSessions" class="ajaxForSessionsForm"
        action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE %>">
        <input type="hidden"
            name="<%= Const.ParamsNames.USER_ID %>"
            value="${data.account.googleId}">
        <input type="hidden"
            name="<%= Const.ParamsNames.IS_USING_AJAX %>"
            value="on">
        <c:if test="${fsForm.feedbackSessionNameForSessionList != null && fsForm.courseIdForNewSession != null}">
            <input type="hidden"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                value="${fsForm.feedbackSessionNameForSessionList}">
            <input type="hidden"
                name="<%= Const.ParamsNames.COURSE_ID %>"
                value="${fsForm.courseIdForNewSession}">
        </c:if>
    </form>
</div>