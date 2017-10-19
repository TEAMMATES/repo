<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
  <script type="text/javascript" src="/js/instructorCourseEnrollHandsontable.js"></script>
</c:set>
<c:set var="SESSION_TOKEN">
  <%=Const.ParamsNames.SESSION_TOKEN%>
</c:set>
<ti:instructorPage title="Enroll Students for ${data.courseId}" cssIncludes="${cssIncludes}" jsIncludes="${jsIncludes}">

  <div class="panel panel-primary">
    <div class="panel-body fill-plain">
      <div class="text-muted padding-15px">
        <span class="glyphicon glyphicon-exclamation-sign glyphicon-primary"></span> If you want to enroll more then <strong>100</strong> students into one course, divide students into sections containing no more than <strong>100</strong> students.
      </div>
      <br>
      <form action="${data.instructorCourseEnrollSaveLink}" method="post" class="form-horizontal" role="form">
        <input type="hidden" name="${SESSION_TOKEN}" value="${data.sessionToken}">
        <div class="col-md-12">
          <div class="form-group">
            <div id="student-data-spreadsheet">
              <div class="col-md-10 col-md-offset-1">
                <div class="panel panel-default">
                  <div class="panel-heading">
                    <strong>Student data</strong>
                  </div>
                  <div class="panel-body">
                    <div id="spreadsheet"></div>
                  </div>
                </div>
                <div class="gap-10px"></div>

                <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />

                <div class="row">
                  <div class="col-md-6">
                    <button type="button" title="Add" id="addEmptyRows" name="empty_rows" class="btn btn-primary btn-md">
                    Add
                    </button>
                    <input type="number" name="number_of_rows" value="1" min="0">
                    <label>Rows</label>
                  </div>
                  <div class="col-md-6">
                    <button type="submit" title="Enroll" id="button_enroll" name="button_enroll" class="btn btn-primary btn-md pull-right">
                    Enroll students
                    </button>
                  </div>
                </div>
                <br>
                <textarea class="form-control" id="enrollstudents" name="enrollstudents" rows="6" cols="120" placeholder="Paste student data here ...">${fn:escapeXml(data.enrollStudents)}</textarea>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
  <br>
  <div class="moreInfo">
    <h2> More info </h2>
    <hr style="width: 80%; margin-left: 0px;">
    <ul>
      <li>
        <span class="moreInfoPointTitle">Spreadsheet Information</span>
        <div class="moreInfoPointDetails">
          <ul>
            <li>
              If you have student data in a spreadsheet, simply copy the relevant cell-range from your spreadsheet and paste into the table above.
              <a id="spreadsheet-link" href="https://docs.google.com/spreadsheets/d/1jODYiO_TcSoQ82t4E9rX-kfn1l7sEhb810rdlVJxHo0/edit?usp=sharing"></a>
            </li>
            <li>Each existing cells can be edited by just clicking on it.</li>
            <li>The entire table can be sorted by just clicking on the column name.</li>
            <li>You can re-arrange column order by clicking on the column header and dragging them left or right.</li>
            <li>To access more edit functions, right-click on a cell.</li>
            <li>Column width can be adjusted.</li>
          </ul>
        </div>
      </li>
      <li>
        <span class="moreInfoPointTitle">Columns Information</span>
        <div class="moreInfoPointDetails">
          <ul>
            <li class="moreInfoColumnInfo">
              <samp>Section</samp> [Compulsory for courses having more than 100 students]: Section name/ID
            </li>
            <li class="moreInfoColumnInfo">
              <samp>Team</samp> [Compulsory]: Team name/ID
              <div class="moreInfoPointDetails">
                <ul>
                  <li class="moreInfoEmailDetails">
                    A team must be unique within a course. A team cannot be in 2 different sections.
                  </li>
                  <li class="moreInfoEmailDetails">
                    If you do not have teams in your course, use “N/A” as the team name for all students.
                  </li>
                </ul>
              </div>
            </li>
            <li class="moreInfoColumnInfo">
              <samp>Name</samp> [Compulsory]: Student name
            </li>
            <li class="moreInfoColumnInfo">
              <samp>Email</samp> [Compulsory]: The email address used to contact the student.<br>
              <div class="moreInfoPointDetails">
                <ul>
                  <li class="moreInfoEmailDetails">
                    This need not be a Gmail address.
                  </li>
                  <li class="moreInfoEmailDetails">
                    It should be unique for each student.
                    If two students are given the same email, they will be considered the same student.
                  </li>
                </ul>
              </div>
            </li>
            <li class="moreInfoColumnInfo">
              <samp>Comments</samp> [Optional]: Any other information you want to record about a student.
            </li>
          </ul>
        </div>
      </li>
      <li>
        <span class="moreInfoPointTitle">Mass editing enrolled students</span>
        <div class="moreInfoPointDetails">
          <ul>
            <li class="moreInfoMassEditInfo">
              To mass-edit data of enrolled students (except email address), simply use this page to re-enroll them with the updated data .
            </li>
            <li class="moreInfoMassEditInfo">
              To DELETE students or to UPDATE EMAIL address of a student, please go to the <code>courses</code> page and click the <code>Students -> View/Edit</code> link of the course.
            </li>
          </ul>
        </div>
      </li>
    </ul>
  </div>
</ti:instructorPage>
