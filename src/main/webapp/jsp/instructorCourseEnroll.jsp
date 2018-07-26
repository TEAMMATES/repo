<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>

<c:set var="cssIncludes">
  <link rel="stylesheet" href="<%= FrontEndLibrary.HANDSONTABLE_CSS %>" type="text/css">
</c:set>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.HANDSONTABLE %>"></script>
  <script type="text/javascript" src="/js/instructorCourseEnrollPage.js"></script>
</c:set>
<c:set var="SESSION_TOKEN">
  <%=Const.ParamsNames.SESSION_TOKEN%>
</c:set>
<c:set var="COURSE_ID">
  <%=Const.ParamsNames.COURSE_ID%>
</c:set>
<ti:instructorPage title="Enroll Students for ${data.courseId}" cssIncludes="${cssIncludes}" jsIncludes="${jsIncludes}">

  <div class="panel panel-primary">
    <div class="panel-body fill-plain">
      <div class="text-muted padding-15px">
        <span class="glyphicon glyphicon-info-sign"></span><a href="#more-spreadsheet-info">
        Scroll down</a> to see more information about the spreadsheet interfaces.
        <br>
        <span class="glyphicon glyphicon-info-sign"></span> If you want to enroll more than
        <strong>100</strong> students into one course, divide students into sections containing no more than
        <strong>100</strong> students.
      </div>
      <form id="student-spreadsheet-form" method="post"
            class="form-horizontal" role="form">
        <input type="hidden" name="${SESSION_TOKEN}" value="${data.sessionToken}">
        <input type="hidden" name="${COURSE_ID}" value="${data.courseId}">
        <div class="col-md-12">
          <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}"/>
          <div class="panel panel-default" >
            <div class="panel-heading" id="existing-data-spreadsheet">
              <div class="pull-right margin-left-7px">
                <span class="glyphicon glyphicon-chevron-down"></span>
              </div>
              <div class="display-status pull-right"></div>
              <strong>Existing students</strong>
            </div>
            <div class="panel-collapse collapse">
              <div class="panel-body padding-0">
                <div id="existingDataSpreadsheet"></div>
              </div>
            </div>
          </div>
          <div class="bottom-margin-correction">
            <div class="panel panel-default">
              <div class="panel-heading" id="enroll-spreadsheet">
                <div class="pull-right margin-left-7px">
                  <span class="glyphicon glyphicon-chevron-down"></span>
                </div>
                <div class='display-status pull-right'></div>
                <strong>New students</strong>
              </div>
              <div class="panel-collapse collapse">
                <div class="panel-body padding-0">
                  <div id="enrollSpreadsheet"></div>
                </div>
              </div>
            </div>
            <div class="row enroll-students-spreadsheet-buttons">
              <div class="col-md-6">
                <div class="input-group">
                    <span class="input-group-btn">
                      <button type="button" title="Add" id="button_add_empty_rows" class="btn btn-primary btn-md">
                        Add row(s)
                      </button>
                    </span>
                  <div class="col-xs-4">
                    <input type="number" id="number-of-rows" class="form-control" value="1" min="0">
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <button type="submit" title="Enroll" id="button_enroll" name="button_enroll"
                        class="btn btn-primary btn-md pull-right"
                        formaction="${data.instructorCourseEnrollSaveLink}">
                  Enroll students
                </button>
              </div>
            </div>

            <br>
            <textarea class="form-control" id="enrollstudents" name="enrollstudents"
                      placeholder="Paste student data here ...">${fn:escapeXml(data.enrollStudents)}</textarea>
          </div>
        </div>
      </form>
    </div>
  </div>

  <br>

  <div class="more-info" id="more-spreadsheet-info">
    <h2> More info </h2>
    <hr style="width: 80%; margin-left: 0px;">
    <ul>
      <li>
        <span class="more-info-point-title">Spreadsheet Information</span>
        <ul>
          <li>
            If you have student data in a spreadsheet, simply copy the relevant cell-range from your spreadsheet and
            paste into the <code>New students</code> spreadsheet interface above.<br><br>
            <table class="table table-striped table-bordered">
              <tr>
                <th>Section</th>
                <th>Team</th>
                <th>Name</th>
                <th>Email</th>
                <th>Comments</th>
              </tr>
              <tr>
                <td>Tutorial Group 1</td>
                <td>Team 1</td>
                <td>Tom Jacobs</td>
                <td>tom@example.com</td>
                <td></td>
              </tr>
              <tr>
                <td>Tutorial Group 1</td>
                <td>Team 1</td>
                <td>Jean Wong</td>
                <td>jean@example.com</td>
                <td>Exchange Student</td>
              </tr>
              <tr>
                <td>Tutorial Group 1</td>
                <td>Team 1</td>
                <td>Ravi Kumar</td>
                <td>ravi@example.com</td>
                <td></td>
              </tr>
              <tr>
                <td>Tutorial Group 2</td>
                <td>Team 2</td>
                <td>Chun Ling</td>
                <td>ling@example.com</td>
                <td></td>
              </tr>
              <tr>
                <td>Tutorial Group 2</td>
                <td>Team 2</td>
                <td>Desmond Wu</td>
                <td>desmond@example.com</td>
                <td></td>
              </tr>
              <tr>
                <td>Tutorial Group 2</td>
                <td>Team 3</td>
                <td>Harsha Silva</td>
                <td>harsha@example.com</td>
                <td></td>
              </tr>
            </table>
          </li>
          <li>Each existing cells can be edited by just clicking on it.</li>
          <li>The entire table can be sorted by just clicking on the column name.</li>
          <li>You can re-arrange column order by clicking on the column header and dragging them left or right.</li>
          <li>To access more edit functions, right-click on a cell.</li>
          <li>Column width can be adjusted.</li>
          <li>Expand the <code>Existing students</code> spreadsheet interface to view existing students in the course.</li>
        </ul>
      </li>
      <li>
        <span class="more-info-point-title">Columns Information</span>
        <ul>
          <li class="more-info-column-info">
            <samp>Section</samp> [Compulsory for courses having more than 100 students]: Section name/ID
          </li>
          <li class="more-info-column-info">
            <samp>Team</samp> [Compulsory]: Team name/ID
            <ul>
              <li class="more-info-sub-point-details">
                A team must be unique within a course. A team cannot be in 2 different sections.
              </li>
              <li class="more-info-sub-point-details">
                If you do not have teams in your course, use “N/A” as the team name for all students.
              </li>
            </ul>
          </li>
          <li class="more-info-column-info">
            <samp>Name</samp> [Compulsory]: Student name
          </li>
          <li class="more-info-column-info">
            <samp>Email</samp> [Compulsory]: The email address used to contact the student.<br>
            <ul>
              <li class="more-info-sub-point-details">
                This need not be a Gmail address.
              </li>
              <li class="more-info-sub-point-details">
                It should be unique for each student.
                If two students are given the same email, they will be considered the same student.
              </li>
            </ul>
          </li>
          <li class="more-info-column-info">
            <samp>Comments</samp> [Optional]: Any other information you want to record about a student.
          </li>
        </ul>
      </li>
      <li>
        <span class="more-info-point-title">Mass editing enrolled students</span>
        <ul>
          <li class="more-info-mass-edit-info">
            To mass-edit data of enrolled students (except email address), simply use this page to re-enroll them with
            the
            updated data .
          </li>
          <li class="more-info-mass-edit-info">
            To DELETE students or to UPDATE EMAIL address of a student, please go to the <code>courses</code> page and
            click the <code>Students -> View/Edit</code> link of the course.
          </li>
        </ul>
      </li>
    </ul>
  </div>
</ti:instructorPage>
