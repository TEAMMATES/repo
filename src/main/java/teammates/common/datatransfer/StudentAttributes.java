package teammates.common.datatransfer;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.Student;

public class StudentAttributes extends EntityAttributes {
    
    //=========================================================================
    public enum UpdateStatus {
        // @formatter:off
        ERROR(0), 
        NEW(1), 
        MODIFIED(2), 
        UNMODIFIED(3), 
        NOT_IN_ENROLL_LIST(4), 
        UNKNOWN(5);
        // @formatter:on
        
        public static final int STATUS_COUNT = 6;
        public final int numericRepresentation;

        private UpdateStatus(int numericRepresentation) {
            this.numericRepresentation = numericRepresentation;
        }

        public static UpdateStatus enumRepresentation(int numericRepresentation) {
            switch (numericRepresentation) {
            case 0:
                return ERROR;
            case 1:
                return NEW;
            case 2:
                return MODIFIED;
            case 3:
                return UNMODIFIED;
            case 4:
                return NOT_IN_ENROLL_LIST;
            case 5:
                return UNKNOWN;
            default:
                return UNKNOWN;
            }
        }
    }

    //=========================================================================
    
    //Note: be careful when changing these variables as their names are used in *.json files.
    public String googleId;
    public String name;
    public String email;
    public String course = null;
    public String comments = null;
    public String team = null;
    public String section = null;
    public String key = null;

    public UpdateStatus updateStatus = UpdateStatus.UNKNOWN;
    
    public StudentAttributes(String id, String email, String name, String comments,
            String courseId, String team, String section) {
        this(section, team, name, email, comments, courseId);
        this.googleId = Sanitizer.sanitizeGoogleId(id);
    }

    public StudentAttributes() {
        
    }
    
    public StudentAttributes(String section, String team, String name, String email, String comment, String courseId) {
        this();
        this.section = Sanitizer.sanitizeTitle(section);
        this.team = Sanitizer.sanitizeTitle(team);
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
        this.comments = Sanitizer.sanitizeTextField(comment);
        this.course = Sanitizer.sanitizeTitle(courseId);
    }

    public StudentAttributes(Student student) {
        this();
        this.email = student.getEmail();
        this.course = student.getCourseId();
        this.name = student.getName();
        this.comments = Sanitizer.sanitizeTextField(student.getComments());
        this.team = Sanitizer.sanitizeTitle(student.getTeamName());
        this.section = ((student.getSectionName() == null) ? Const.DEFAULT_SECTION : Sanitizer.sanitizeTitle(student.getSectionName()));
        // TODO: Is this supposed to be null or "" ?? Find out and standardize.
        this.googleId = ((student.getGoogleId() == null) ? "" : student.getGoogleId());
        Long keyAsLong = student.getRegistrationKey();
        this.key = (keyAsLong == null ? null : Student
                .getStringKeyForLongKey(keyAsLong));

        // TODO: this is for backward compatibility with old system. Old system
        // considers "" as unregistered. It should be changed to consider
        // null as unregistered.
    }
    
    public String toEnrollmentString() {
        String enrollmentString = "";
        String enrollmentStringSeparator = "|";
        
        enrollmentString = this.section + enrollmentStringSeparator;
        enrollmentString += this.team + enrollmentStringSeparator;
        enrollmentString += this.name + enrollmentStringSeparator;
        enrollmentString += this.email + enrollmentStringSeparator;
        enrollmentString += this.comments;
        
        return enrollmentString;
        
    }

    public boolean isRegistered() {
        return googleId != null && !googleId.equals("");
    }

    public boolean isEnrollInfoSameAs(StudentAttributes otherStudent) {
        return (otherStudent != null) && otherStudent.email.equals(this.email)
                && otherStudent.course.equals(this.course)
                && otherStudent.name.equals(this.name)
                && otherStudent.comments.equals(this.comments)
                && otherStudent.team.equals(this.team)
                && otherStudent.section.equals(this.section);
    }

    public List<String> getInvalidityInfo() {
        
        //id is allowed to be null when the student is not registered
        Assumption.assertTrue(team != null);
        Assumption.assertTrue(comments != null);
        
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        if (isRegistered()) {
            error = validator.getInvalidityInfo(FieldType.GOOGLE_ID, googleId);
            if (!error.isEmpty()) {    errors.add(error);}
        }
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, course);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.EMAIL, email);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.TEAM_NAME, team);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.SECTION_NAME, section);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.STUDENT_ROLE_COMMENTS, comments);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.PERSON_NAME, name);
        if(!error.isEmpty()) { errors.add(error); }
        
        return errors;
    }
    
    public static void sortByTeamName(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
            public int compare(StudentAttributes s1, StudentAttributes s2) {
                String t1 = s1.team;
                String t2 = s2.team;
            
                //If the team name is the same, reorder by student name
                if(t1.compareTo(t2) == 0){
                    return s1.name.compareTo(s2.name);
                }
                return t1.compareTo(t2);
            }
        });
    }
    
    public static void sortByNameAndThenByEmail(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
            public int compare(StudentAttributes s1, StudentAttributes s2) {
                int result = s1.name.compareTo(s2.name);
                if (result == 0)
                    result = s1.email.compareTo(s2.email);
                return result;
            }
        });
    }
    
    public void updateWithExistingRecord(StudentAttributes originalStudent) {
        if(this.email == null){
            this.email = originalStudent.email;
        }
        if(this.name == null){
            this.name = originalStudent.name;
        }
        if(this.googleId == null){
            this.googleId = originalStudent.googleId;
        }
        if(this.team == null){
            this.team = originalStudent.team;
        }
        if(this.comments == null){
            this.comments = originalStudent.comments;
        }
        if(this.section == null){
            this.section = originalStudent.section;
        }
    }

    public Student toEntity() {
        return new Student(email, name, googleId, comments, course, team, section);
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        String indentString = StringHelper.getIndent(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString + "Student:" + name + "[" + email + "]" + EOL);
        return sb.toString();
    }

    @Override
    public String getIdentificationString() {
        return this.course + "/" + this.email;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Student";
    }
    
    @Override
    public void sanitizeForSaving() {
        this.googleId = Sanitizer.sanitizeGoogleId(this.googleId);
        this.email = Sanitizer.sanitizeEmail(this.email);
        this.course = Sanitizer.sanitizeTitle(this.course);
        this.name = Sanitizer.sanitizeName(this.name);
        this.team = Sanitizer.sanitizeTitle(this.team);
        this.section = Sanitizer.sanitizeTitle(this.section);
        this.comments = Sanitizer.sanitizeTextField(this.comments);
        this.googleId = Sanitizer.sanitizeForHtml(this.googleId);
        this.email = Sanitizer.sanitizeForHtml(this.email);
        this.course = Sanitizer.sanitizeForHtml(this.course);
        this.name = Sanitizer.sanitizeForHtml(this.name);
        this.team = Sanitizer.sanitizeForHtml(this.team);
        this.section = Sanitizer.sanitizeForHtml(this.section);
        this.comments = Sanitizer.sanitizeForHtml(this.comments);
    }
}
