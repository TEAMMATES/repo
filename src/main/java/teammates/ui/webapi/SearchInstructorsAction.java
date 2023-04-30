package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;

/**
 * Searches for instructors.
 */
public class SearchInstructorsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<Instructor> instructors;
        try {
            instructors = sqlLogic.searchInstructorsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        List<InstructorData> instructorDataList = new ArrayList<>();
        for (Instructor instructor : instructors) {
            InstructorData instructorData = new InstructorData(instructor);
            instructorData.addAdditionalInformationForAdminSearch(
                    instructor.getRegKey(),
                    sqlLogic.getCourseInstitute(instructor.getCourseId()),
                    instructor.getGoogleId());

            instructorDataList.add(instructorData);
        }

        InstructorsData instructorsData = new InstructorsData();
        instructorsData.setInstructors(instructorDataList);

        return new JsonResult(instructorsData);
    }
}
