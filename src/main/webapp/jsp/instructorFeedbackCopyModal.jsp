<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackEditCopyPageData" %>
<%
    InstructorFeedbackEditCopyPageData data = (InstructorFeedbackEditCopyPageData) request.getAttribute("data");
%>
<div class="form-group">
    <label for="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>" class="control-label">
        Name for copied sessions
    </label>
    <input class="form-control"
           id="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>"
           type="text"
           name="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>"
           value="<%= data.fsName %>">
</div>
<% for (CourseAttributes course: data.courses) { %>
    <div class="checkbox">
        <label>
            <input type="checkbox"
                   name="<%= Const.ParamsNames.COPIED_COURSES_ID %>"
                   value="<%= course.id %>">
            <% if (course.id.equals(data.courseId)) { %>
                [<span class="text-color-red"><%= course.id %></span>] : <%= course.name %>
                <br>
                <span class="text-color-red small">{Session currently in this course}</span>       
            <% } else { %>
                [<%= course.id %>] : <%= course.name %>
            <% } %>
        </label>
    </div>
<% } %>
<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="<%= data.courseId %>">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%= data.fsName %>">