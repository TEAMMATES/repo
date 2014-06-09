package teammates.common.datatransfer;

import java.util.HashMap;
import teammates.common.util.Const;

public final class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, Boolean> sectionRecord;
    private HashMap<String, Boolean> sectionLevel;
    private HashMap<String, HashMap<String, Boolean>> sessionLevel;
    
    public InstructorPrivileges() {
        this.courseLevel = new HashMap<String, Boolean>();
        this.sectionRecord = new HashMap<String, Boolean>();
        this.sectionLevel = new HashMap<String, Boolean>();
        this.sessionLevel = new HashMap<String, HashMap<String, Boolean>>();
    }
    
    public InstructorPrivileges(String instrRole) {
        this();
        switch (instrRole) {
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            setDefaultPrivilegesForCoowner();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            setDefaultPrivilegesForManager();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            setDefaultPrivilegesForObserver();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            setDefaultPrivilegesForTutor();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER:
            setDefaultPrivilegesForHelper();
            break;
        default:
            setDefaultPrivilegesForHelper();
            break;
        }
    }
    
    public void setDefaultPrivilegesForCoowner() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, true);
    }
    
    public void setDefaultPrivilegesForManager() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, true);
    }
    
    public void setDefaultPrivilegesForObserver() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void setDefaultPrivilegesForTutor() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, true);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void setDefaultPrivilegesForHelper() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        this.courseLevel.put(privilegeName, isAllowed);
    }
    
    /**
     * Updates the sectionId in sections record if sectionId exists
     * or adds sectionId to sections record
     * @param sectionId
     * @param isSelected
     */
    public void addSectionToSectionRecord(String sectionId, boolean isSelected) {
        updateSectionInSectionRecord(sectionId, isSelected);      
    }
    
    public void updateSectionInSectionRecord(String sectionId, boolean isSelected) {
        this.sectionRecord.put(sectionId, isSelected);
    }
    
    public void updatePrivilegeInSectionLevel(String privilegeName, boolean isAllowed) {
        this.sectionLevel.put(privilegeName, isAllowed);
    }
    
    /**
     * Adds sessionId to sessionLevel with privileges configured in sectionLevel
     * @param sessionId
     */
    public void addSessionToSessionLevel(String sessionId) {        
        this.sessionLevel.put(sessionId, getPrivilegesForSessionsInSections());
    }
    
    public void addSessionToSessionLevel(String sessionId, HashMap<String, Boolean> privileges) {
        this.sessionLevel.put(sessionId, privileges);
    }
    
    public void updateSessionPrivilegeInSessionLevel(String sessionId, String privilegeName, boolean isAllowed) {
        if (!this.sessionLevel.containsKey(sessionId)) {
            addSessionToSessionLevel(sessionId);
        }
        this.sessionLevel.get(sessionId).put(privilegeName, isAllowed);
    }
    
    public HashMap<String, Boolean> getPrivilegesForSessionsInSections() {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        return privileges;
    }

    public HashMap<String, Boolean> getCourseLevelPrivileges() {
        return this.courseLevel;
    }
    
    public HashMap<String, Boolean> getSectionRecord() {
        return this.sectionRecord;
    }

    public HashMap<String, Boolean> getSectionLevelPrivileges() {
        return this.sectionLevel;
    }

    public HashMap<String, HashMap<String, Boolean>> getSessionLevelPrivileges() {
        return this.sessionLevel;
    }
    
    /**
     * @param privilegeName
     * @return true if the value for privilegeName is true
     */
    public boolean isAllowedInCourseLevel(String privilegeName) {
        if (!this.courseLevel.containsKey(privilegeName)) {
            return false;
        }
        
        return this.courseLevel.get(privilegeName).booleanValue();
    }
    
    /**
     * @param sectionId
     * @return true if section is contained and the value is true
     */
    public boolean isAllowedForSectionLevel(String sectionId) {
        if (!this.sectionRecord.containsKey(sectionId)) {
            return false;
        }
        
        return this.sectionRecord.get(sectionId).booleanValue();
    }
    
    /**
     * @param sectionId
     * @param privilegeName
     * @return true if the user is allowed the privilege:privilegeName for the section:sectionId
     */
    public boolean isAllowedForSectionInSectionLevel(String sectionId, String privilegeName) {
        if (!this.sectionRecord.containsKey(sectionId) ||
                !this.sectionRecord.get(sectionId).booleanValue()) {
            return false;
        }
        if (!this.sectionLevel.containsKey(privilegeName)) {
            return false;
        }
        
        return this.sectionLevel.get(privilegeName).booleanValue();
    }
    
    /**
     * @param sectionId
     * @param sessionId
     * @param privilegesName
     * @return true if section is included and session is included and the value
     *         for the privilegeName under this session is true
     *         or section is included and session is not included but the value for
     *         the privilegeName under this section is true
     */
    public boolean isAllowedForSessionInSessionLevel(String sectionId, String sessionId, String privilegesName) {
        if (!this.sectionRecord.containsKey(sectionId) ||
                !this.sectionRecord.get(sectionId).booleanValue()) {
            return false;
        }
        if (!this.sessionLevel.containsKey(sessionId)) {
            if (!this.sectionLevel.containsKey(privilegesName)) {
                return false;
            } else {
                return this.sectionLevel.get(privilegesName).booleanValue();
            }
        } else {
            if (!this.sessionLevel.get(sessionId).containsKey(privilegesName)) {
                return false;
            } else {
                return this.sessionLevel.get(sessionId).get(privilegesName).booleanValue();
            }
        }
    }
    
    public boolean equals(Object another) {
        if (!(another instanceof InstructorPrivileges)) {
            return false;
        }
        if (another == this) {
            return true;
        }
        
        InstructorPrivileges rhs = (InstructorPrivileges)another;
        return this.getCourseLevelPrivileges().equals(rhs.getCourseLevelPrivileges()) &&
                this.getSectionLevelPrivileges().equals(rhs.getSectionLevelPrivileges()) &&
                this.getSessionLevelPrivileges().equals(rhs.getSessionLevelPrivileges());
    }
    
}
