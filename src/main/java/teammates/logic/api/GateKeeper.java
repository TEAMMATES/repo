package teammates.logic.api;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.StudentsDb;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GateKeeper {
    private static UserService userService = UserServiceFactory.getUserService();
    
    /* This enum is not used at the moment. It is for future reference.
     * We plan to pass Activity as an additional parameter to access control
     * methods for finer-grain access control. e.g., to block some instructors
     * from viewing results of an evaluation.
     *
     */
    public enum Activity {
        ADD, VIEW, UPDATE, DELETE
    }
    
    // TODO: refactor this! gate keeper should not use Db level APIs
    private static final StudentsDb studentsDb = new StudentsDb();
    private static EvaluationsDb evaluationsDb = new EvaluationsDb();

    private static GateKeeper instance = null;
    public static GateKeeper inst() {
        if (instance == null)
            instance = new GateKeeper();
        return instance;
    }
    
    @SuppressWarnings("unused")
    private void ____USER_related_methods________________________________() {
    }
    
    public boolean isUserLoggedOn() {
        return userService.getCurrentUser() != null;
    }
    
    public UserType getCurrentUser() {
        User user = getCurrentGoogleUser();
        if (user == null) {
            return null;
        }

        //TODO: instead of just taking nickname, we can keep the whole object.
        UserType userType = new UserType(user.getNickname());

        if (isAdministrator()) {
            userType.isAdmin = true;
        }
        if (isInstructor()) {
            userType.isInstructor = true;
        }

        if (isStudent()) {
            userType.isStudent = true;
        }
        
        return userType;
    }

    public String getLoginUrl(String redirectPage) {
        
        User user = userService.getCurrentUser();

        if (user != null) {
            return redirectPage;
        } else {
            return userService.createLoginURL(redirectPage);
        }
    }

    public String getLogoutUrl(String redirectPage) {
        return userService.createLogoutURL(redirectPage);
    }
    
    /*
     * These methods ensures the logged in user is of a particular type.
     */
    @SuppressWarnings("unused")
    private void ____ACCESS_control_per_user_type_________________________() {
    }
    
    /** Verifies the user is logged in */
    public void verifyLoggedInUserPrivileges(){
        if(isUserLoggedOn()) return;
        throw new UnauthorizedAccessException("User is not logged in");
    }
    
    /** Verifies that the logged in user is the admin and there is no
     * masquerading going on.
     */
    public void verifyAdminPrivileges(AccountAttributes account){
        String loggedInUser = getCurrentGoogleUser().getNickname();
        if (isUserLoggedOn() 
                && userService.isUserAdmin()
                && loggedInUser.equals(account.googleId)) 
            return;
        
        throw new UnauthorizedAccessException("User "+loggedInUser+" does not have admin privilleges");
    }

    /** Verifies that the nominal user has instructor privileges.
     */
    public void verifyInstructorPrivileges(AccountAttributes account){
        
        if(account.isInstructor) {
            return;
        }
        throw new UnauthorizedAccessException("User "+account.googleId+" does not have instructor privilleges");

    }
    
    /** Verifies that the nominal user has student privileges. Currently, all
     * logged in users as student privileges.
     */
    public void verifyStudentPrivileges(AccountAttributes account){
        verifyLoggedInUserPrivileges();
    }
    
    /*These methods ensures that the nominal user specified has access 
     * to a given entity.
     */
    @SuppressWarnings("unused")
    private void ____ACCESS_control_per_entity_________________________() {
    }
    
    public void verifyAccessible(StudentAttributes student, CourseAttributes course){
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.id, "course ID");
        
        if(!student.course.equals(course.id)){
            throw new UnauthorizedAccessException(
                    "Course [" + course.id + "] is not accessible to student ["+ student.email+ "]");
        }
    }
    
    public void verifyAccessible(StudentAttributes student, EvaluationAttributes evaluation){
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(evaluation, "evaluation");
        verifyNotNull(evaluation.courseId, "course ID in the evaluation");
        
        if(!student.course.equals(evaluation.courseId)){
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to student ["+ student.email+ "]");
        }
    }
    
    public void verifyAccessible(StudentAttributes student, FeedbackSessionAttributes feedbacksession){
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.courseId, "feedback session's course ID");
        
        if(!student.course.equals(feedbacksession.courseId) ||
            feedbacksession.isPrivateSession() == true){
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to student ["+ student.email + "]");
        }
        
        if(feedbacksession.isVisible() == false) {
            throw new UnauthorizedAccessException(
                    "This feedback session is not yet visible.");
        }
    }
    
    public void verifyAccessible(StudentAttributes student, List<SubmissionAttributes> submissions){
        verifyNotNull(student, "student");
        
        if(submissions.size() == 0) return;
        
        verifyAccessible(
                student, 
                evaluationsDb.getEvaluation(
                        submissions.get(0).course, 
                        submissions.get(0).evaluation));
        
        for(SubmissionAttributes s: submissions){
            if(!s.reviewer.equals(student.email)){
                throw new UnauthorizedAccessException("Student [" + student.email + "] cannot edit submission of ["+ s.reviewer+ "]");
            }
        }
    }

    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course){
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.id, "course ID");
        if(!instructor.courseId.equals(course.id)){
            throw new UnauthorizedAccessException("Course [" + course.id + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.id, "course ID");
        if(!instructor.courseId.equals(course.id)){
            throw new UnauthorizedAccessException("Course [" + course.id + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.id + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String sectionName, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.id, "course ID");
        if(!instructor.courseId.equals(course.id)){
            throw new UnauthorizedAccessException("Course [" + course.id + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
        if (!instructor.isAllowedForPrivilege(sectionName, privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.id + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, EvaluationAttributes evaluation){
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(evaluation, "evaluation");
        verifyNotNull(evaluation.courseId, "course ID in the evaluation");
        if(!instructor.courseId.equals(evaluation.courseId)){
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, EvaluationAttributes evaluation, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(evaluation, "evaluation");
        verifyNotNull(evaluation.courseId, "course ID in the evaluation");
        if (!instructor.courseId.equals(evaluation.courseId)) {
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, EvaluationAttributes evaluation, String sectionName, String sessionName, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(evaluation, "evaluation");
        verifyNotNull(evaluation.courseId, "course ID in the evaluation");
        if (!instructor.courseId.equals(evaluation.courseId)) {
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
        if (!instructor.isAllowedForPrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + sessionName, privilegeName)) {
            throw new UnauthorizedAccessException(
                    "Evaluation [" + evaluation.name + 
                    "] is not accessible to instructor ["+ instructor.email+ "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, 
            FeedbackSessionAttributes feedbacksession, boolean creatorOnly){
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.courseId, "feedback session's course ID");
        
        if(!instructor.courseId.equals(feedbacksession.courseId)){
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "]");
        }

        if (creatorOnly &&
                !feedbacksession.creatorEmail.equals(
                instructor.email)) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "] for this purpose");
        }           
    }
    
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
            boolean creatorOnly, String privilegeName){
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.courseId, "feedback session's course ID");
        
        if(!instructor.courseId.equals(feedbacksession.courseId)){
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "]");
        }

        if (creatorOnly &&
                !feedbacksession.creatorEmail.equals(
                instructor.email)) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "] for this purpose");
        }
        
        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "]");
        }
    }
    
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
            boolean creatorOnly, String sectionName, String sessionName, String privilegeName){
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.courseId, "feedback session's course ID");
        
        if(!instructor.courseId.equals(feedbacksession.courseId)){
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "]");
        }

        if (creatorOnly &&
                !feedbacksession.creatorEmail.equals(
                instructor.email)) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "] for this purpose");
        }
        
        if (!instructor.isAllowedForPrivilege(sectionName, sessionName, privilegeName)) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbacksession.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "]");
        }
    }
    
    /*These methods ensures that the nominal user specified can perform the 
     * specified action on a given entity.
     */
    @SuppressWarnings("unused")
    private void ____ACCESS_control_per_entity_per_activity________________() {
    }
    
    //TODO: to be implemented when we adopt more finer-grain access control.
    
    @SuppressWarnings("unused")
    private void ____PRIVATE_methods________________________________() {
    }

    private void verifyNotNull(Object object, String typeName) {
        if(object==null){
            throw new UnauthorizedAccessException("Trying to access system using a non-existent "+ typeName + " entity");
        }
        
    }

    private User getCurrentGoogleUser() {        
        return userService.getCurrentUser();
    }
    
    private boolean isAdministrator() {
        return isUserLoggedOn() && userService.isUserAdmin();
    }

    private boolean isInstructor() {
        User user = userService.getCurrentUser();
        return isUserLoggedOn() &&  AccountsLogic.inst().isAccountAnInstructor(user.getNickname());
    }

    private boolean isStudent() {
        User user = userService.getCurrentUser();
        return isUserLoggedOn() && studentsDb.getStudentsForGoogleId(user.getNickname()).size()!=0;
    }
    

}
