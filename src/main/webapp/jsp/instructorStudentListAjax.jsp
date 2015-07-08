<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="courseId" value="${data.courseId}" />
<c:set var="courseIndex" value="${data.courseIndex}" />
<c:set var="hasSection" value="${data.hasSection}" />
<c:set var="sections" value="${data.sections}" />
<table class="table table-responsive table-striped table-bordered margin-0">
    <c:choose>
        <c:when test="${not empty sections}">
            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr id="resultsHeader-${courseIndex}">
                    <th>Photo</th>
                    <th id="button_sortsection-${courseIndex}" class="button-sort-none<c:if test="${not hasSection}"> hidden</c:if>" onclick="toggleSort(this,2)">
                        Section <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortteam-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,3)">
                        Team <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortstudentname-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,4)">
                        Student Name <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortemail-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,5)">
                        Email <span class="icon-sort unsorted"></span>
                    </th>
                    <th>Action(s)</th>
                </tr>
                <tr id="searchNoResults-${courseIndex}" class="hidden">
                    <th class="align-center color_white bold">Cannot find students in this course</th>
                </tr>
            </thead>
            <tbody>
                <c:set var="teamIndex" value="${-1}" />
                <c:set var="studentIndex" value="${-1}" />
                <c:forEach items="${sections}" var="section" varStatus="sectionIdx">
                    <c:set var="sectionIndex" value="${sectionIdx.index}" />
                    <%-- generated here but to be appended to #sectionChoices in instructorStudentList.jsp
                         will be transported via JavaScript in instructorStudentListAjax.js --%>
                    <div class="checkbox section-to-be-transported">
                        <input id="section_check-${courseIndex}-${sectionIndex}" type="checkbox" checked="checked" class="section_check">
                        <label for="section_check-${courseIndex}-${sectionIndex}">
                            [${courseId}] : ${section.sectionName}
                        </label>
                    </div>
                    <c:forEach items="${section.teams}" var="team">
                        <c:set var="teamIndex" value="${teamIndex + 1}" />
                        <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                             will be transported via JavaScript in instructorStudentListAjax.js --%>
                        <div class="checkbox team-to-be-transported">
                            <input id="team_check-${courseIndex}-${sectionIndex}-${teamIndex}" type="checkbox" checked="checked" class="team_check">
                            <label for="team_check-${courseIndex}-${sectionIndex}-${teamIndex}">
                                [${courseId}] : ${team.teamName}
                            </label>
                        </div>
                        <c:forEach items="${team.students}" var="student" varStatus="studentIdx">
                            <c:set var="studentIndex" value="${studentIndex + 1}" />
                            <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                                 will be transported via JavaScript in instructorStudentListAjax.js --%>
                            <div class="email-to-be-transported" id="student_email-c${courseIndex}.${studentIndex}">
                                ${student.studentEmail}
                            </div>
                            <tr id="student-c${courseIndex}.${studentIndex}">
                                <td id="studentphoto-c${courseIndex}.${studentIndex}">
                                    <div class="profile-pic-icon-click align-center" data-link="${student.photoUrl}">
                                        <a class="student-profile-pic-view-link btn-link">View Photo</a>
                                        <img src="" alt="No Image Given" class="hidden">
                                    </div>
                                </td>
                                <td id="studentsection-c${courseIndex}.${sectionIndex}"
                                    <c:if test="${not hasSection}">class="hidden"</c:if>>
                                    ${section.sectionName}
                                </td>
                                <td id="studentteam-c${courseIndex}.${sectionIndex}.${teamIndex}">
                                    ${team.teamName}
                                </td>
                                <td id="studentname-c${courseIndex}.${studentIndex}">
                                    ${student.studentName}
                                </td>
                                <td id="studentemail-c${courseIndex}.${studentIndex}">
                                    ${student.studentEmail}
                                </td>
                                <td class="no-print align-center">
                                    <a class="btn btn-default btn-xs student-view-for-test"
                                       href="${student.courseStudentDetailsLink}"
                                       title="<%= Const.Tooltips.COURSE_STUDENT_DETAILS %>"
                                       target="_blank"
                                       data-toggle="tooltip"
                                       data-placement="top"
                                       <c:if test="${not section.allowedToViewStudentInSection}">disabled="disabled"</c:if>>
                                        View
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-default btn-xs student-edit-for-test"
                                       href="${student.courseStudentEditLink}"
                                       title="<%= Const.Tooltips.COURSE_STUDENT_EDIT %>"
                                       target="_blank"
                                       data-toggle="tooltip"
                                       data-placement="top"
                                       <c:if test="${not section.allowedToModifyStudent}">disabled="disabled"</c:if>>
                                        Edit
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-default btn-xs student-delete-for-test"
                                       href="${student.courseStudentDeleteLink}"
                                       onclick="return toggleDeleteStudentConfirmation(${student.toggleDeleteConfirmationParams})"
                                       title="<%= Const.Tooltips.COURSE_STUDENT_DELETE %>"
                                       data-toggle="tooltip"
                                       data-placement="top"
                                       <c:if test="${not section.allowedToModifyStudent}">disabled="disabled"</c:if>>
                                        Delete
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-default btn-xs student-records-for-test"
                                       href="${student.courseStudentRecordsLink}"
                                       title="<%= Const.Tooltips.COURSE_STUDENT_RECORDS %>"
                                       target="_blank"
                                       data-toggle="tooltip"
                                       data-placement="top">
                                        All Records
                                    </a>
                                    &nbsp;
                                    <div class="dropdown inline">
                                        <a class="btn btn-default btn-xs dropdown-toggle"
                                           href="javascript:;"
                                           data-toggle="dropdown"
                                           <c:if test="${not section.allowedToGiveCommentInSection}">disabled="disabled"</c:if>>
                                            Add Comment
                                        </a>
                                        <ul class="dropdown-menu align-left" role="menu" aria-labelledby="dLabel">
                                            <li role="presentation">
                                                <a target="_blank"
                                                   role="menuitem"
                                                   tabindex="-1"
                                                   href="${student.courseStudentDetailsLink}&addComment=student">
                                                    Comment on ${student.studentName}
                                                </a>
                                            </li>
                                            <li role="presentation">
                                                <a target="_blank"
                                                   role="menuitem"
                                                   tabindex="-1"
                                                   href="${student.courseStudentDetailsLink}&addComment=team">
                                                    Comment on ${team.teamName}
                                                </a>
                                            </li>
                                            <c:if test="${hasSection}">
                                                <li role="presentation">
                                                    <a target="_blank"
                                                       role="menuitem"
                                                       tabindex="-1"
                                                       href="${student.courseStudentDetailsLink}&addComment=section">
                                                        Comment on ${section.sectionName}
                                                    </a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </c:when>
        <c:otherwise>
            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr>
                    <th class="align-center color_white bold">There are no students in this course</th>
                </tr>
            </thead>
        </c:otherwise>
    </c:choose>
</table>