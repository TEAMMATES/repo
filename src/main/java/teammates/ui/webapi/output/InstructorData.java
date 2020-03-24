package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.StringHelper;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    private String googleId;
    private final String courseId;
    private final String email;
    @Nullable
    private Boolean isDisplayedToStudents;
    @Nullable
    private String displayedToStudentsAs;
    private final String name;
    @Nullable
    private InstructorPermissionRole role;
    private JoinState joinState;
    @Nullable
    private String key;
    @Nullable
    private String institute;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.googleId = instructorAttributes.getGoogleId();
        this.courseId = instructorAttributes.getCourseId();
        this.email = instructorAttributes.getEmail();
        this.role = instructorAttributes.getRole() == null ? null
                : InstructorPermissionRole.getEnum(instructorAttributes.getRole());
        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents();
        this.displayedToStudentsAs = instructorAttributes.getDisplayedName();
        this.name = instructorAttributes.getName();

        this.joinState = instructorAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
    }

    public InstructorPermissionRole getRole() {
        return role;
    }

    public void setRole(InstructorPermissionRole role) {
        this.role = role;
    }

    public Boolean getIsDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public void setIsDisplayedToStudents(Boolean displayedToStudents) {
        isDisplayedToStudents = displayedToStudents;
    }

    public String getDisplayedToStudentsAs() {
        return displayedToStudentsAs;
    }

    public void setDisplayedToStudentsAs(String displayedToStudentsAs) {
        this.displayedToStudentsAs = displayedToStudentsAs;
    }

    public String getName() {
        return name;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public String getKey() {
        return key;
    }

    public void setKey(List<InstructorAttributes> instructorAttributes) {
        this.key = StringHelper.encrypt(instructorAttributes.stream()
                .filter((InstructorAttributes instructorAttribute)
                        -> this.googleId.equals(instructorAttribute.getGoogleId()))
                .collect(Collectors.toList()).get(0).getKey());
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    /**
     * Hides some attributes for search result.
     */
    public void hideInformationForSearch() {
        setRole(null);
        setDisplayedToStudentsAs(null);
        setIsDisplayedToStudents(null);
    }
}
