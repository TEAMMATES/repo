package teammates.ui.template;

import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentInfoTable {
    private String name;
    private String email;
    private String section;
    private String team;
    private String comments;
    private String courseId;
    private String courseName;
    private boolean hasSection;

    public StudentInfoTable(StudentAttributes student, String courseName, boolean hasSection) {
        this.name = student.name;
        this.email = student.email;
        this.section = student.section;
        this.team = student.team;
        this.comments = student.comments;
        this.courseId = student.course;
        this.courseName = courseName;
        this.hasSection = hasSection;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSection() {
        return section;
    }

    public String getTeam() {
        return team;
    }

    public String getComments() {
        return comments;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public boolean getHasSection() {
        return hasSection;
    }
}
