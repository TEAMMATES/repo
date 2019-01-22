package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: moves a course to Recycle Bin (soft-delete) for an instructor.
 */
public class DeleteCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                logic.getCourse(idOfCourseToDelete), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

    }

    @Override
    public ActionResult execute() {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        try {
            logic.moveCourseToRecycleBin(idOfCourseToDelete);

            return new JsonResult("The course " + idOfCourseToDelete
                        + " has been deleted. You can restore it from the Recycle Bin manually.");
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_MULTI_STATUS);
        }
    }

}
