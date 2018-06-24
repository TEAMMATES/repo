<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - feedback sessions 'template question' modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="feedbackSessionName" required="true"%>
<%@ attribute name="courseId" required="true"%>

<div class="modal fade" id="addTemplateQuestionModal" tabindex="-1" role="dialog" aria-labelledby="addTemplateQuestionModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="addTemplateQuestionModalTitle">Add Template Questions</h4>
      </div>
    </div>
  </div>
</div>
