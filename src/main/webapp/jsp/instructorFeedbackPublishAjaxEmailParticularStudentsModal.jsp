<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table-responsive table table-bordered">
  <tr class="background-color-medium-gray">
    <th></th>
    <th class="button_sortsection button-sort-none toggle-sort">
      Section
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortteam button-sort-none toggle-sort">
      Team
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortname button-sort-none toggle-sort">
      Student Name
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortemail button-sort-none toggle-sort">
      Email
      <span class="icon-sort unsorted"></span>
    </th>
  </tr>
  <c:forEach items="${data.responseStatus.noResponse}" var="userToEmailEmail">
    <tr class="bg-danger">
      <td class="align-center">
        <div class="checkbox">
          <label>
            <input type="checkbox" class="table-column-no-float" name="<%= Const.ParamsNames.SEND_PUBLISHED_EMAIL %>" value="${userToEmailEmail}">
          </label>
        </div>
      </td>
      <td>
        ${data.responseStatus.emailSectionTable[userToEmailEmail]}
      </td>
      <td>
        ${data.responseStatus.emailTeamNameTable[userToEmailEmail]}
      </td>
      <td>
        ${data.responseStatus.emailNameTable[userToEmailEmail]}
      </td>
      <td>
        ${userToEmailEmail}
      </td>
    </tr>
  </c:forEach>
  <c:forEach items="${data.responseStatus.studentsWhoResponded}" var="userToEmailEmail">
    <tr class="bg-info">
      <td class="align-center">
        <div class="checkbox">
          <label>
            <input type="checkbox" class="table-column-no-float" name="<%= Const.ParamsNames.SEND_PUBLISHED_EMAIL %>" value="${userToEmailEmail}">
          </label>
        </div>
      </td>
      <td>
        ${data.responseStatus.emailSectionTable[userToEmailEmail]}
      </td>
      <td>
        ${data.responseStatus.emailTeamNameTable[userToEmailEmail]}
      </td>
      <td>
        ${data.responseStatus.emailNameTable[userToEmailEmail]}
      </td>
      <td>
        ${userToEmailEmail}
      </td>
    </tr>
  </c:forEach>
</table>

<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.fsName}">
