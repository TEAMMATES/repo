package teammates.ui.template;

import java.util.List;

public class ArchivedCoursesTableRow {
    private String courseId;
    private String courseName;
    private String createdAtDateString;
    private String createdAtDateStamp;
    private List<ElementTag> actions;

    public ArchivedCoursesTableRow(String courseIdParam, String courseNameParam,
            String createdAtDateStringParam, String createdAtDateStampParam,
            List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.createdAtDateString = createdAtDateStringParam;
        this.createdAtDateStamp = createdAtDateStampParam;
        this.actions = actionsParam;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCreatedAtDateString() {
        return createdAtDateString;
    }

    public String getCreatedAtDateStamp() {
        return createdAtDateStamp;
    }

    public List<ElementTag> getActions() {
        return actions;
    }
}
