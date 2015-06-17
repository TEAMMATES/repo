package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class InstructorStudentListAjaxPageData extends PageData {

    public InstructorStudentListAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public List<SectionDetailsBundle> courseSectionDetails;
    public CourseAttributes course;
    public boolean hasSection;
    public Map<String, String> emailPhotoUrlMapping;
    public Map<String, Map<String, Boolean>> sectionPrivileges;
    public int courseIdx;

    public String getCourseStudentDetailsLink(StudentAttributes student, String userId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getCourseStudentEditLink(StudentAttributes student, String userId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getCourseStudentDeleteLink(StudentAttributes student, String userId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getStudentRecordsLink(StudentAttributes student, String userId) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }

}

