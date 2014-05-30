<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.ActivityLogEntry"%>
<%@ page import="teammates.ui.controller.AdminActivityLogPageData"%>

<%
	AdminActivityLogPageData data = (AdminActivityLogPageData) request
			.getAttribute("data");
%>
<!DOCTYPE html>
<html>

<head>
    <link rel="shortcut icon" href="/favicon.png" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <title>TEAMMATES - Administrator</title>
    
    
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="/stylesheets/teammatesCommon.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
              <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
              <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
              <![endif]-->
    
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminActivityLog.js"></script>
    <script type="text/javascript"
        src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
</head>

<body>
    <div id="dhtmltooltip"></div>

    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />

    <div class="container theme-showcase" role="main">
        <div id="frameBody">
            <div id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <div id="headerOperation" class="page-header">
                    <h1>Admin Activity Log</h1>
                </div>

                <div class="well well-plain">
                    <form class="form-horizontal" method="post"
                        action="" id="activityLogFilter" role="form">

                        <div class="panel-heading" id="filterForm">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="input-group">
                                            <span
                                                class="input-group-btn">
                                                <button
                                                    class="btn btn-default"
                                                    type="submit"
                                                    name="search_submit">Filter</button>
                                            </span> <input type="text"
                                                class="form-control"
                                                id="filterQuery"
                                                name="filterQuery"
                                                value="<%=data.filterQuery%>">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <a href="#"
                                    class="btn btn-link center-block"
                                    onclick="toggleReference()"><span
                                    id="referenceText"> Show
                                        Reference</span><br> <span
                                    class="glyphicon glyphicon-chevron-down"
                                    id="detailButton"></span> </a>

                            </div>

                            <div id="filterReference">
                                <div class="form-group">

                                    <div class="col-md-12">
                                        <div class="alert alert-success">
                                            <p class="text-center">
                                                <span
                                                    class="glyphicon glyphicon-filter"></span>
                                                A query is formed by a
                                                list of filters. Each
                                                filter is in the format
                                                <strong>&nbsp;[filter
                                                    label]: [value1,
                                                    value2, value3....]</strong><br>
                                            </p>
                                        </div>

                                        <p class="text-center">
                                            <span
                                                class="glyphicon glyphicon-hand-right"></span>
                                            Combine filters with the <span
                                                class="label label-warning">
                                                AND</span> keyword or the <span
                                                class="label label-warning">|</span>
                                            separator.

                                        </p>
                                    </div>

                                </div>
                                <small>
                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">
                                                <strong>Sample
                                                    Queries:</strong> <br>
                                                <ul>
                                                    <li>E.g. role:
                                                        Instructor AND
                                                        request:
                                                        InstructorCourse,
                                                        InstructorEval
                                                        AND from:
                                                        15/03/13</li>
                                                    <li>E.g. from:
                                                        13/3/13 AND to:
                                                        17/3/13 AND
                                                        person:
                                                        teammates.test
                                                        AND response:
                                                        Pageload, System
                                                        Error Report,
                                                        Servlet Action
                                                        Failure</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">

                                                <strong>
                                                    Possible Labels:</strong>&nbsp;from,
                                                to, person, role,
                                                request, response<br>
                                                <ul>

                                                    <li>E.g. from:
                                                        13/03/13</li>


                                                    <li>E.g. to:
                                                        13/03/13</li>
                                                    <li>E.g.
                                                        person:
                                                        teammates.coord</li>
                                                    <li>E.g. role:
                                                        Instructor,
                                                        Student</li>
                                                    <li>E.g.
                                                        request:
                                                        InstructorEval,
                                                        StudentHome,
                                                        evaluationclosingreminders
                                                    </li>
                                                    <li>E.g.
                                                        response:
                                                        Pageload, System
                                                        Error Report,
                                                        Delete Course</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Roles: </strong>
                                                Instructor, Student,
                                                Unknown

                                            </p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Servlets
                                                    Requests: </strong> <br>
                                                <br>
                                            <div
                                                class="table-responsive">
                                                <table
                                                    class="table table-condensed">
                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">

                                                                <li
                                                                    class="list-group-item">instructorHome</li>
                                                                <li
                                                                    class="list-group-item">instructorCourse</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseEnroll</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseEdit</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseRemind</li>

                                                            </ul>

                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">instructorCourseDelete</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseDetails</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseStudentEdit</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseStudentDelete</li>
                                                                <li
                                                                    class="list-group-item">instructorCourseStudentDetails</li>
                                                            </ul>

                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">instructorEval</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalExport</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalEdit</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalDelete</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalRemind</li>
                                                            </ul>
                                                        </td>

                                                    </tr>

                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">instructorEvalPublish</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalUnpublish</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalResults</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalSubmissionEditHandler</li>
                                                                <li
                                                                    class="list-group-item">instructorEvalSubmissionEdit</li>

                                                                <li
                                                                    class="list-group-item">instructorEvalSubmissionView</li>
                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">

                                                                <li
                                                                    class="list-group-item list-group-item-success">studentHome
                                                                </li>
                                                                <li
                                                                    class="list-group-item list-group-item-success">studentCourseDetails</li>
                                                                <li
                                                                    class="list-group-item list-group-item-success">studentCourseJoin</li>
                                                                <li
                                                                    class="list-group-item list-group-item-success">studentEvalEditHandler</li>
                                                                <li
                                                                    class="list-group-item list-group-item-success">studentEvalEdit</li>
                                                                <li
                                                                    class="list-group-item list-group-item-success">studentEvalResults</li>
                                                            </ul>
                                                        </td>

                                                        <td>

                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item list-group-item-danger">evaluationclosingreminders</li>
                                                                <li
                                                                    class="list-group-item list-group-item-danger">evaluationopeningreminders</li>

                                                            </ul>

                                                        </td>
                                                    </tr>

                                                </table>
                                            </div>
                                            </p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Responses:
                                                </strong> <br> <br>
                                            <div
                                                class="table-responsive">


                                                <table
                                                    class="table table-condensed">

                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Remind
                                                                    Students
                                                                    About
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Evaluation
                                                                    Closing
                                                                    reminders</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Evaluation
                                                                    Opening
                                                                    reminders</li>


                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Publish
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Unpublish
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Registration</li>


                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Pageload</li>
                                                                <li
                                                                    class="list-group-item">System
                                                                    Error
                                                                    Report</li>
                                                                <li
                                                                    class="list-group-item">Servlet
                                                                    Action
                                                                    Failure</li>


                                                            </ul>
                                                        </td>

                                                    </tr>

                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Add
                                                                    New
                                                                    Course</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Course</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Course
                                                                    Info</li>
                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Enroll
                                                                    Students</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Student
                                                                    Details</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Student</li>

                                                                <li
                                                                    class="list-group-item">Student
                                                                    Joining
                                                                    Course</li>
                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Create
                                                                    New
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Evaluation
                                                                    Info</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Evaluation</li>

                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Submission</li>


                                                            </ul>
                                                        </td>
                                                </table>
                                            </div>

                                            </p>

                                        </div>

                                    </div>
                                </small>
                            </div>

                        </div>

                    </form>


                </div>

                <%
                	if (data.queryMessage != null) {
                %>
                <div class="alert alert-danger">
                    <span class="glyphicon glyphicon-warning-sign"></span>
                    <%
                    	out.println(" " + data.queryMessage);
                    %>
                </div>
                <%
                	}
                %>



                <input type="hidden" name="offset"
                    value="<%=data.offset%>"> <input
                    type="hidden" name="pageChange" value="false">


            </div>


            <br> <br>


            <%
            	List<ActivityLogEntry> appLogs = data.logs;
            	if (appLogs != null) {
            %>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <strong>Activity Log</strong>
                </div>
                <div class="table-responsive">
                    <table class="table table-striped dataTable">

                        <thead>
                            <tr>
                                <th width="10%">Date</th>
                                <th>[Role][Google
                                    ID][Name][Email][Action]</th>
                            </tr>

                        </thead>
                        <tbody>
                            <%
                            	if (appLogs.isEmpty()) {
                            %>
                            <tr>
                                <td colspan='2'><i>No
                                        application logs found</i></td>
                            </tr>
                            <%
                            	} else {
                            			for (ActivityLogEntry log : appLogs) {
                            %>
                            <tr>
                                <td><%=log.getDateInfo()%></td>
                                <td><%=log.getRoleInfo()%>&nbsp;&nbsp;<%=log.getPersonInfo()%>&nbsp;&nbsp;<%=log.getActionInfo()%>
                                    <br><%=log.getMessageInfo()%></td>
                            </tr>
                            <%
                            	}
                            		}
                            %>

                        </tbody>
                    </table>
                </div>

            </div>
            <%
            	}
            %>

            <jsp:include
                page="<%=Const.ViewURIs.STATUS_MESSAGE_WITHOUT_FOCUS%>" />
            <br>

            <div>
                <a href="#frameBodyWrapper" class="btn  btn-primary"><span
                    class="glyphicon glyphicon-arrow-up"></span> Back To
                    Top</a>
            </div>
            <br> <br>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>