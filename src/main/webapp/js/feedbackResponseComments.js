var addCount = 0;

$(document).ready(function(){
    var addCommentHandler = function(e) {
        var submitButton = $(this);
        var formObject = $(this).parent().parent();
        var addFormRow = $(this).parent().parent().parent();
        var formData = formObject.serialize();
        
        e.preventDefault();
        
        $.ajax({
            type : 'POST',
            url : 	submitButton.attr('href') + "?" + formData,
            beforeSend : function() {
                formObject.find("textarea").prop("disabled", true);
                submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error : function() {
                formObject.find("textarea").prop("disabled", false);
                setFormErrorMessage(submitButton, "Failed to save comment. Please try again.");
                submitButton.text("Add");
            },
            success : function(data) {
                setTimeout(function(){
                    if (!data.isError) {
                        // Inject new comment row
                    	addFormRow.parent().attr("class", "list-group comment-list");
                        addFormRow.before(generateNewCommentRow(data));
                        var newCommentRow = addFormRow.prev();
                        newCommentRow.find("form[class*='responseCommentEditForm'] > div > a").click(editCommentHandler);
                        newCommentRow.find("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
                        addCount++;
                        $("[data-toggle='tooltip']").tooltip({html: true});
                        
                        // Reset add comment form
                        formObject.find("textarea").prop("disabled", false);
                        formObject.find("textarea").val("");
                        submitButton.text("Add");
                        removeFormErrorMessage(submitButton);
                        addFormRow.prev().show();
                    } else {
                        formObject.find("textarea").prop("disabled", false);
                        setFormErrorMessage(submitButton, data.errorMessage);
                        submitButton.text("Add");
                    }
                },500);
            }
        });
    };
    $("form[class*='responseCommentAddForm'] > div > a").click(addCommentHandler);
    
    var editCommentHandler = function(e) {
        var submitButton = $(this);
        var formObject = $(this).parent().parent();
        var displayedText = $(this).parent().parent().prev();
        var commentBar = displayedText.parent().find("div");
        var formData = formObject.serialize();
        
        e.preventDefault();
        
        $.ajax({
            type : 'POST',
            url : 	submitButton.attr('href') + "?" + formData,
            beforeSend : function() {
                formObject.find("textarea").prop("disabled", true);
                submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error : function() {
                formObject.find("textarea").prop("disabled", false);
                setFormErrorMessage(submitButton, "Failed to save changes. Please try again.");
                submitButton.text("Save");
            },
            success : function(data) {
                setTimeout(function(){
                    if (!data.isError) {
                        // Update editted comment
                    	displayedText.text(data.comment.commentText.value);
                    	commentBar.show();
                        
                        // Reset edit comment form
                        formObject.find("textarea").prop("disabled", false);
                        formObject.find("textarea").val(data.comment.commentText.value);
                        submitButton.text("Save");
                        removeFormErrorMessage(submitButton);
                        formObject.hide();
                        displayedText.show();
                    } else {
                        formObject.find("textarea").prop("disabled", false);
                        setFormErrorMessage(submitButton, data.errorMessage);
                        submitButton.text("Save");
                    }
                },500);
            }
        });
    };
    $("form[class*='responseCommentEditForm'] > div > a").click(editCommentHandler);
    
    var deleteCommentHandler = function(e) {
        var submitButton = $(this);
        var formObject = $(this).parent();
        var deletedCommentRow = $(this).parent().parent();
        var formData = formObject.serialize();
        var editForm = submitButton.parent().next().next().next();
    	var frCommentList = submitButton.parent().parent().parent();
        
        e.preventDefault();
        
        $.ajax({
            type : 'POST',
            url : 	submitButton.attr('href') + "?" + formData,
            beforeSend : function() {
                submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error : function() {
                if (editForm.is(':visible')) {
                    setFormErrorMessage(editForm.find("div > a"), "Failed to delete comment. Please try again.");
                } else if (frCommentList.parent().find("div.delete_error_msg").length == 0) {
                	frCommentList.after("<div class=\"delete_error_msg alert alert-danger\">Failed to delete comment. Please try again.</div>");
                }
                submitButton.html("<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>");
            },
            success : function(data) {
                setTimeout(function(){
                    if (!data.isError) {
                    	var numberOfItemInFrCommentList = deletedCommentRow.parent().children('li');
                        if(numberOfItemInFrCommentList.length <= 2){
                        	deletedCommentRow.parent().hide();
                        }
                        deletedCommentRow.remove();
                        frCommentList.parent().find("div.delete_error_msg").remove();
                    } else {
                        if (editForm.is(':visible')) {
                        	setFormErrorMessage(editForm.find("div > a"), data.errorMessage);
                        } else if (frCommentList.parent().find("div.delete_error_msg").length == 0) {
                        	frCommentList.after("<div class=\"delete_error_msg alert alert-danger\">" + data.errorMessage + "</div>");
                        }
                        submitButton.html("<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>");
                    }
                },500);
            }
        });
    };
    $("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
});

function generateNewCommentRow(data) {
	var commentDate = new Date(data.comment.createdAt);
	var commentDateStr = commentDate.toString();
	var thisYear = commentDate.getFullYear();
	var indexOfYear = commentDateStr.indexOf(thisYear, 0);
	var formattedDate = commentDateStr.substring(0, indexOfYear - 1);
	
    var newRow =
    // Comment Row
	"<li class=\"list-group-item list-group-item-warning\" id=\"responseCommentRow-" + addCount + "\">"
	+ "<div id=\"commentBar-" + addCount + "\">"
    + "<span class=\"text-muted\">From: " + data.comment.giverEmail + " [" + formattedDate + "]</span>"
	// Delete form
    + "<form class=\"responseCommentDeleteForm pull-right\">"
    + 		"<a href=\"/page/instructorFeedbackResponseCommentDelete\" type=\"button\" id=\"commentdelete-" + data.comment.feedbackResponseCommentId + "\" class=\"btn btn-default btn-xs icon-button\"" 
    +    		" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Delete this comment\">" 
    +    		"<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>"
    +    	"</a>"
    + 	"<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
    + 	"<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
    + 	"<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
    + 	"<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
    + "</form>"
    + "<a type=\"button\" id=\"commentedit-" + addCount + "\" class=\"btn btn-default btn-xs icon-button pull-right\""
    + 		" onclick=\"showResponseCommentEditForm(" + addCount + ")\""
    + 		" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Edit this comment\">"
    + 	"<span class=\"glyphicon glyphicon-pencil glyphicon-primary\"></span>"
    + "</a>"
    + "</div>"
    // Display Saved Comment
    + "<div id=\"plainCommentText-" + addCount + "\">" + data.comment.commentText.value + "</div>"
    // Edit form
    + "<form style=\"display:none;\" id=\"responseCommentEditForm-" + addCount + "\" class=\"responseCommentEditForm\">"
    + 	"<div class=\"form-group\">"
    + 		"<textarea class=\"form-control\" rows=\"3\" placeholder=\"Your comment about this response\""
    + 			" name=\"" + FEEDBACK_RESPONSE_COMMENT_TEXT + "\""
    + 			" id=\"" + FEEDBACK_RESPONSE_COMMENT_TEXT + "\"-" + addCount + "\">" + data.comment.commentText.value + "</textarea>"
    +	 "</div>"
    + 	 "<div class=\"col-sm-offset-5\">"
    + 		"<a href=\"/page/instructorFeedbackResponseCommentEdit\" type=\"button\" class=\"btn btn-primary\" id=\"button_save_comment_for_edit-" + addCount + "\">"
    + 			"Save"
    + 		"</a><span> </span>"
    +    	"<input type=\"button\" class=\"btn btn-default\" value=\"Cancel\" onclick=\"return hideResponseCommentEditForm(" + addCount + ");\">"
    + 	 "</div>"
    + 	 "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
    + 	 "<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
    + 	 "<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
    + 	 "<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
    + "</form>"
    + "</li>";
    return newRow;
}

function removeFormErrorMessage(submitButton) {
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().remove();
    }
}

function setFormErrorMessage(submitButton, msg){
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().text(msg);
    } else {
        submitButton.next().after("<span id=\"errorMessage\" class=\"pull-right \"> " + msg + "</span>");
    }
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
    $("#responseCommentTable"+id).show();
    $("#showResponseCommentAddForm"+id).show();
    $("#responseCommentAddForm"+id).focus();
}

function hideResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
    if($("#responseCommentTable"+ id + " > li").length <= 1){
    	$("#responseCommentTable"+id).hide();
    }
    $("#showResponseCommentAddForm"+id).hide();
    removeFormErrorMessage($("#button_save_comment_for_add" + id));
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
	var id;
	if(giverIndex || qnIndex || commentIndex){
		id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
	} else {
		id = "-"+recipientIndex;
	}
	var commentBar = $("#plainCommentText"+id).parent().find("#commentBar"+id);
	commentBar.hide();
    $("#plainCommentText"+id).hide();
    $("#responseCommentEditForm"+id+" > div > textarea").val($("#plainCommentText"+id).text());
    $("#responseCommentEditForm"+id).show();
    $("#responseCommentEditForm"+id+" > div > textarea").focus();
}

function hideResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
    var id;
    if(giverIndex || qnIndex || commentIndex){
    	id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
    } else {
    	id = "-"+recipientIndex;
    }
    var commentBar = $("#plainCommentText"+id).parent().find("#commentBar"+id);
    commentBar.show();
    $("#plainCommentText"+id).show();
    $("#responseCommentEditForm"+id).hide();
    removeFormErrorMessage($("#button_save_comment_for_edit" + id));
}

function showNewlyAddedResponseCommentEditForm(addedIndex) {
    $("#responseCommentRow-"+addedIndex).hide();
    if ($("#responseCommentEditForm-"+addedIndex).prev().is(':visible')) {
        $("#responseCommentEditForm-"+addedIndex).prev().remove();
    }
    $("#responseCommentEditForm-"+addedIndex).show();
}