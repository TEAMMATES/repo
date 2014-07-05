<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.StringHelper"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.SectionDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.TeamDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="teammates.ui.controller.InstructorStudentListPageData"%>
<%
    InstructorStudentListPageData data = (InstructorStudentListPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="shortcut icon" href="/favicon.png" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TEAMMATES - Instructor</title>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
        <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>

        <script type="text/javascript" src="/js/googleAnalytics.js"></script>
        <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
        
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorStudentList.js"></script>
        <jsp:include page="../enableJS.jsp"></jsp:include>
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />    
        <div id="frameBodyWrapper" class="container theme-showcase">
            <div id="topOfPage"></div>
            <h1>Instructor Students List</h1>
            <div class="well well-plain">
                <div class="row">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-10">
                                <div class="form-group">
                                    <input type="text" id="searchbox"
                                        title="<%=Const.Tooltips.SEARCH_STUDENT%>"
                                        class="form-control"
                                        data-toggle="tooltip"
                                        data-placement="top"
                                        placeholder="e.g. Charles Shultz"
                                        value="<%=data.searchKey == null ? "" : PageData.sanitizeForHtml(data.searchKey) %>">
                                </div>
                            </div>
                            <div class="col-md-2 nav">
                                <div class="form-group">
                                    <button id="button_search" class="btn btn-primary" type="submit" onclick="return applyFilters();" value="Search">
                                        <span class="glyphicon glyphicon-search"></span> Search
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                <div class="checkbox">
                                    <input id="option_check" type="checkbox">
                                    <label for="option_check">Show More Options</label>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="checkbox">
                                    <input id="displayArchivedCourses_check" type="checkbox" <%if(data.displayArchive){%>checked="checked"<%}%>>
                                    <label for="displayArchivedCourses_check">Display Archived Courses</label>
                                </div>
                            </div>
                        </div>
                     </div>
                 </div>
             </div>
            <br>
            <div id="moreOptionsDiv" class="well well-plain" style="display: none;">
                <form class="form-horizontal" role="form">
                    <div class="row">
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Courses</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input type="checkbox" value="" id="course_all" checked="checked"> 
                                <label for="course_all"><strong>Select all</strong></label>
                            </div>
                            <br>
                            <%
                                int courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                            %>
                                <div class="checkbox"><input id="course_check-<%=courseIdx %>" type="checkbox" checked="checked">
                                    <label for="course_check-<%=courseIdx %>">
                                    [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                                    </label>
                                </div>
                            <%
                                    }
                                }
                            %>
                        </div>
                        
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Sections</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input type="checkbox" value="" id="section_all" checked="checked"> 
                                <label for="section_all"><strong>Select all</strong></label>
                            </div>
                            <br>
                            <%
                                courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                                        int sectionIdx = -1;
                                        for(SectionDetailsBundle sectionDetails: courseDetails.sections) {
                                            sectionIdx++;
                            %>
                                <div class="checkbox"><input id="section_check-<%=courseIdx %>-<%=sectionIdx%>" type="checkbox" checked="checked">
                                    <label for="section_check-<%=courseIdx %>-<%=sectionIdx%>">
                                    [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(sectionDetails.name)%>
                                    </label>
                                </div>
                            <%          
                                        }
                                    }
                                }
                            %>
                        </div>

                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Teams</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input id="team_all" type="checkbox" checked="checked">
                                <label for="team_all"><strong>Select All</strong></label>
                            </div>
                            <br>
                            <%
                                courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                                        int sectionIdx = -1;
                                        int teamIdx = -1;
                                    for(SectionDetailsBundle sectionDetails: courseDetails.sections){
                                        sectionIdx++;
                                        for(TeamDetailsBundle teamDetails: sectionDetails.teams){
                                            teamIdx++;
                            %>
                                <div class="checkbox">
                                    <input id="team_check-<%=courseIdx %>-<%=sectionIdx%>-<%=teamIdx %>" type="checkbox" checked="checked">
                                    <label for="team_check-<%=courseIdx %>-<%=sectionIdx%>-<%=teamIdx%>">
                                    [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(teamDetails.name)%>
                                    </label>
                                </div>
                            <%
                                        }
                                    }
                                }
                                }
                            %>
                        </div>
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Emails</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input id="show_email" type="checkbox" checked="checked">
                                    <label for="show_email"><strong>Show Emails</strong></label>
                            </div>
                            <br>
                            <div id="emails">
                                <%
                                
                                    courseIdx = -1;
                                    for(CourseDetailsBundle courseDetails: data.courses){
                                        if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                            courseIdx++;
                                            int totalCourseStudents = courseDetails.stats.studentsTotal;
                                            if(totalCourseStudents >= 1){
                                                int studentIdx = -1;
                                                for(SectionDetailsBundle sectionDetails : courseDetails.sections){
                                                for(TeamDetailsBundle teamDetails: sectionDetails.teams){
                                                    for(StudentAttributes student: teamDetails.students){
                                                        studentIdx++;
                                %>
                                        <div id="student_email-c<%=courseIdx %>.<%=studentIdx%>"><%=student.email %></div>
                                <%
                                                    }
                                                }
                                                }
                                            }
                                        }
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
    
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <%
                courseIdx = -1;
                for (CourseDetailsBundle courseDetails : data.courses) {
                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                        courseIdx++;
                        int sortIdx = 2;
                        int numSections = courseDetails.stats.sectionsTotal;
                        int totalCourseStudents = courseDetails.stats.studentsTotal;
            %>

            <div class="well well-plain" id="course-<%=courseIdx%>">
                <div class="row">
                    <div class="col-md-10 text-color-<%=courseDetails.course.isArchived ? "default":"primary" %>">
                        <h4>
                            <strong>
                                [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                            </strong>
                        </h4>
                    </div>
                    <div class="col-md-2">
                        <a class="btn btn-default btn-xs pull-right pull-down course-enroll-for-test"
                            href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                            title="<%=Const.Tooltips.COURSE_ENROLL%>"
                            data-toggle="tooltip" data-placement="top"
                            <% if (!data.instructors.get(courseDetails.course.id).isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                            disabled="disabled"
                            <% } %>>
                                <span class="glyphicon glyphicon-list"></span> Enroll
                        </a>
                    </div>
                </div>
                <%
                    if (totalCourseStudents > 0) {
                %>
                        <table class="table table-responsive table-striped table-bordered">
                            <thead class="fill-<%=courseDetails.course.isArchived ? "default":"primary" %>">
                                <tr>
                                    <th>
                                        Photo
                                    </th>
                                    <% if(numSections != 0) { %>
                                        <th id="button_sortsection-<%=courseDetails.course.id%>" class="button-sort-none" onclick="toggleSort(this,<%=sortIdx++%>)">
                                            Section <span class="icon-sort unsorted"></span>
                                        </th>
                                    <% } %>
                                    <th id="button_sortteam-<%=courseDetails.course.id%>" class="button-sort-none" onclick="toggleSort(this,<%=sortIdx++%>)">
                                        Team <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortstudentname-<%=courseDetails.course.id%>" class="button-sort-none" onclick="toggleSort(this,<%=sortIdx++%>)">
                                        Student Name <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortemail-<%=courseDetails.course.id%>" class="button-sort-none" onclick="toggleSort(this,<%=sortIdx++%>)"> 
                                        Email <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th>Action(s)
                                    </th>
                                </tr>
                            </thead>
                            <%  
                                    int sectionIdx = -1;
                                    int teamIdx = -1;
                                    int studentIdx = -1;
                                    String photoUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?" + 
                                    	    Const.ParamsNames.STUDENT_EMAIL+"=%s&" + 
                                    	    Const.ParamsNames.COURSE_ID + "=%s";
                                    for(SectionDetailsBundle sectionDetails : courseDetails.sections){
                                        sectionIdx++;
                                        for(TeamDetailsBundle teamDetails: sectionDetails.teams){
                                            teamIdx++;
                                            for(StudentAttributes student: teamDetails.students){
                                                studentIdx++;
                            %>
                            <tr id="student-c<%=courseIdx %>.<%=studentIdx%>" style="display: table-row;">
                                <td id="studentphoto-c<%=courseIdx %>.<%=studentIdx%>" class="profile-pic-icon">
                                    <a class="student-photo-link-for-test btn-link" 
                                       data-link=<%=String.format(photoUrl, StringHelper.encrypt(student.email),  StringHelper.encrypt(student.course)) %>>
                                       View Photo</a>
                                    <img src="" alt="No Image Given" class="hidden">
                                </td>
                                <% if(numSections != 0) { %>
                                    <td id="studentsection-c<%=courseIdx %>.<%=sectionIdx%>"><%=PageData.sanitizeForHtml(sectionDetails.name)%></td>
                                <% } %>
                                <td id="studentteam-c<%=courseIdx %>.<%=sectionIdx%>.<%=teamIdx%>"><%=PageData.sanitizeForHtml(teamDetails.name)%></td>
                                <td id="studentname-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.name)%></td>
                                <td id="studentemail-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.email)%></td>
                                <td class="no-print align-center">
                                    <a class="btn btn-default btn-xs student-view-for-test" 
                                    href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% InstructorAttributes instructor = data.instructors.get(courseDetails.course.id);
                                       if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {%>
                                       disabled="diabled"
                                    <% } %>
                                    > View</a> 
                                    
                                    <a class="btn btn-default btn-xs student-edit-for-test"
                                    href="<%=data.getCourseStudentEditLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                       disabled="diabled"
                                    <% } %>
                                    > Edit</a> 
                                    
                                    <a class="btn btn-default btn-xs student-delete-for-test"
                                    href="<%=data.getCourseStudentDeleteLink(courseDetails.course.id, student)%>"
                                    onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(courseDetails.course.id)%>','<%=sanitizeForJs(student.name)%>')"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                       disabled="diabled"
                                    <% } %>> Delete</a>
                                    
                                    <a class="btn btn-default btn-xs student-records-for-test"
                                    href="<%=data.getStudentRecordsLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> All Records</a>
                                    
                                    <div class="dropdown" style="display:inline;">
                                      <a class="btn btn-default btn-xs dropdown-toggle" 
                                        href="javascript:;" data-toggle="dropdown"
                                        <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                            disabled="disabled"
                                        <% } %>
                                        > Add Comment</a>
                                      <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
                                        <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)
                                            +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=student"%>">
                                            Comment on <%=PageData.sanitizeForHtml(student.name)%></a></li>
                                        <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)
                                            +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=team"%>">
                                            Comment on <%=PageData.sanitizeForHtml(teamDetails.name)%></a></li>
                                        <% if(numSections != 0) { %>
                                        <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)
                                            +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=section"%>">
                                            Comment on <%=PageData.sanitizeForHtml(sectionDetails.name)%></a></li>
                                        <% } %>
                                      </ul>
                                    </div>
                                </td>
                            </tr>
                            <%
                                        }
                                    }
                                 }
                            %>
                        </table>
                        <%
                                } else {
                        %>
                        <table class="table table-responsive table-striped table-bordered">
                            <thead>
                                <tr class="fill-<%=courseDetails.course.isArchived ? "default":"primary" %>">
                                    <th class="align-center color_white bold"><%=Const.StatusMessages.INSTRUCTOR_COURSE_EMPTY %></th>
                                </tr>
                            </thead>
                        </table>
                        <%
                                }
                        %>
                    </div>
            <%
                    out.flush();
                    }
                }
            %>
            <br> <br> <br>
        </div>

        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </body>
</html>