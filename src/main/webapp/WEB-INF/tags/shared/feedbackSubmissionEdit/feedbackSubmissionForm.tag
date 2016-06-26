<%@ tag description="studentFeedbackSubmissionEdit.jsp - Display student feedback submission form" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<%@ attribute name="feedbackSubmissionForm" type="teammates.ui.controller.FeedbackSubmissionEditPageData" required="true" %>

<form method="post" name="form_student_submit_response" action="${feedbackSubmissionForm.submitAction}">
    <jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
    
    <div class="bold align-center">
        <label class=" text-color-gray font-weight-normal" id="last-submitted"></label>
    </div>
    
    <div class="bold align-center"> 
        <c:if test="${feedbackSubmissionForm.moderation}">       
            <input name="moderatedperson" value="${feedbackSubmissionForm.studentToViewPageAs.email}" type="hidden">
        </c:if>

        <c:choose>
            <c:when test="${empty feedbackSubmissionForm.bundle.questionResponseBundle}">
                    There are no questions for you to answer here!
            </c:when>

           
            <c:otherwise>
                <input type="hidden" name="isRedirectPage" id="isRedirectPage" value="true">

                <input type="button" class="btn btn-primary"
                    id="response_save_button" data-toggle="tooltip"
                    data-placement="top"
                    title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE_WITHOUT_REDIRECTING%>"
                    value="Save Feedback"
                    <c:if test="${feedbackSubmissionForm.preview or (not feedbackSubmissionForm.submittable)}">
                           disabled style="background: #66727A;"
                    </c:if>>


                <input type="submit" class="btn btn-primary margin-left-10px"
                       id="response_submit_button" data-toggle="tooltip"
                       data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>"
                       value="Submit Feedback"
                       <c:if test="${feedbackSubmissionForm.preview or (not feedbackSubmissionForm.submittable)}">
                           disabled style="background: #66727A;"
                       </c:if>>
            </c:otherwise>
        </c:choose>
    </div>
    <br> 
    <br>
</form>