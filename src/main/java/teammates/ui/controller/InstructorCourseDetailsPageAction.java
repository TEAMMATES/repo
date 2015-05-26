package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing the details page for a course of an instructor
 */
public class InstructorCourseDetailsPageAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException{
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        //this is for ajax loading of the htm table in the modal
        boolean isHtmlTableNeeded = getRequestParamAsBoolean(Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED);
        
        Assumption.assertNotNull(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        
        /* Setup page data for the "Course Details" page */
        InstructorCourseDetailsPageData data = new InstructorCourseDetailsPageData(account);

        if (isHtmlTableNeeded) {
            data.studentListHtmlTableAsString = StringHelper.csvToHtmlTable(
                                                    logic.getCourseStudentListAsCsv(courseId, account.googleId));            
            statusToAdmin = "instructorCourseDetails Page Ajax Html table Load<br>" 
                            + "Viewing Student List Table for Course <span class=\"bold\">[" + courseId + "]</span>";
            return createAjaxResult(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS, data);
        } else {
            data.studentListHtmlTableAsString = "";
        }
              
        data.currentInstructor = instructor;
        data.courseDetails = logic.getCourseDetails(courseId);
        data.students = logic.getStudentsForCourse(courseId);
        data.instructors = logic.getInstructorsForCourse(courseId); 

        StudentAttributes.sortByNameAndThenByEmail(data.students);
        
        statusToAdmin = "instructorCourseDetails Page Load<br>" 
                        + "Viewing Course Details for Course <span class=\"bold\">[" + courseId + "]</span>";
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS, data);   
        return response;
    }
}
