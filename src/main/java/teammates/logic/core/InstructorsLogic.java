package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.InstructorsDb;

/**
 * Handles  operations related to instructor roles.
 */
public class InstructorsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, no need for header 
    //  comments.
    
    public static final String ERROR_NO_INSTRUCTOR_LINES = "Course must have at lease one instructor\n";
    
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    
    private static Logger log = Utils.getLogger();
    
    private static InstructorsLogic instance = null;
    public static InstructorsLogic inst() {
        if (instance == null)
            instance = new InstructorsLogic();
        return instance;
    }
    

    public void addInstructor(String courseId, String name, String email) 
            throws InvalidParametersException, EntityAlreadyExistsException {
                
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, name, email);
        
        createInstructor(instructorToAdd);
    }

    public void createInstructor(String googleId, String courseId, String name, String email) 
            throws InvalidParametersException, EntityAlreadyExistsException {
                
        InstructorAttributes instructorToAdd = new InstructorAttributes(googleId, courseId, name, email);
        
        createInstructor(instructorToAdd);
    }
    
    public void createInstructor(InstructorAttributes instructorToAdd) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        log.info("going to create instructor :\n"+instructorToAdd.toString());
        
        instructorsDb.createEntity(instructorToAdd);
    }

    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        return instructorsDb.getInstructorForEmail(courseId, email);
    }

    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        return instructorsDb.getInstructorForGoogleId(courseId, googleId);
    }
    
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {
        return instructorsDb.getInstructorForRegistrationKey(encryptedKey);
    }

    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        return instructorsDb.getInstructorsForCourse(courseId);
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
        return instructorsDb.getInstructorsForGoogleId(googleId);
    }
    
    public String getKeyForInstructor(String courseId, String email)
            throws EntityDoesNotExistException {
        
        InstructorAttributes instructor = getInstructorForEmail(courseId, email);
        
        if (instructor == null) {
            throw new EntityDoesNotExistException("Instructor does not exist :" + email);
        }
    
        return instructor.key;
    }
    
    public List<InstructorAttributes> getInstructorsForEmail(String email) {
        return instructorsDb.getInstructorsForEmail(email);
    }

    /**
     * @deprecated Not scalable. Use only for admin features.
     */
    @Deprecated 
    public List<InstructorAttributes> getAllInstructors() {
        return instructorsDb.getAllInstructors();
    }


    public boolean isInstructorOfCourse(String instructorId, String courseId) {
        return instructorsDb.getInstructorForGoogleId(courseId, instructorId) != null;
    }
    
    public boolean isInstructorEmailOfCourse(String instructorEmail, String courseId) {
        return instructorsDb.getInstructorForEmail(courseId, instructorEmail) != null;
    }
    
    public boolean isNewInstructor(String googleId) {
        List<InstructorAttributes> instructorList = getInstructorsForGoogleId(googleId);
        
        if (instructorList.isEmpty()) {
            return true;
        } else if (instructorList.size() == 1 &&
                coursesLogic.isSampleCourse(instructorList.get(0).courseId)){
                return true;
        } else {
            return false;
        }
    }
    
    public void verifyInstructorExists(String instructorId)
            throws EntityDoesNotExistException {
        if (!accountsLogic.isAccountAnInstructor(instructorId)) {
            throw new EntityDoesNotExistException("Instructor does not exist :"
                    + instructorId);
        }
    }
    
    public void verifyIsInstructorOfCourse(String instructorId, String courseId)
            throws EntityDoesNotExistException {
        if (!isInstructorOfCourse(instructorId, courseId)) {
            throw new EntityDoesNotExistException("Instructor " + instructorId
                    + " does not belong to course " + courseId);
        }
    }
    
    public void verifyIsInstructorEmailOfCourse(String instructorEmail, String courseId)
            throws EntityDoesNotExistException {
        if (!isInstructorEmailOfCourse(instructorEmail, courseId)) {
            throw new EntityDoesNotExistException("Instructor " + instructorEmail
                    + " does not belong to course " + courseId);
        }
    }

    /**
     * Update the name and email address of an instructor with the specific Google ID.
     * @param googleId
     * @param instructor InstructorAttributes object containing the details to be updated
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException 
     */
    public void updateInstructorByGoogleId(String googleId, InstructorAttributes instructor) 
            throws InvalidParametersException, EntityDoesNotExistException {
        
        InstructorAttributes instructorToUpdate = getInstructorForGoogleId(instructor.courseId, googleId);
        if (instructorToUpdate == null) {
            throw new EntityDoesNotExistException("Instructor does not exist :" + googleId);
        }
        instructorToUpdate.name = instructor.name;
        instructorToUpdate.email = instructor.email;
        
        instructorsDb.updateInstructorByGoogleId(instructorToUpdate);
    }
    
    /**
     * Update the Google ID and name of an instructor with the specific email.
     * @param email
     * @param instructor InstructorAttributes object containing the details to be updated
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException 
     */
    public void updateInstructorByEmail(String email, InstructorAttributes instructor) 
            throws InvalidParametersException, EntityDoesNotExistException {
        
        InstructorAttributes instructorToUpdate = getInstructorForEmail(instructor.courseId, email);
        instructorToUpdate.googleId = instructor.googleId;
        instructorToUpdate.name = instructor.name;
        
        instructorsDb.updateInstructorByEmail(instructorToUpdate);
    }
    
    public MimeMessage sendRegistrationInviteToInstructor(String courseId, String instructorEmail) 
            throws EntityDoesNotExistException {
        
        CourseAttributes course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + courseId + "], trying to send invite email to student [" + instructorEmail + "]");
        }
        
        InstructorAttributes instructorData = getInstructorForEmail(courseId, instructorEmail);
        if (instructorData == null) {
            throw new EntityDoesNotExistException(
                    "Instructor [" + instructorEmail + "] does not exist in course [" + courseId + "]");
        }
        
        Emails emailMgr = new Emails();
        try {
            MimeMessage email = emailMgr.generateInstructorCourseJoinEmail(course, instructorData);
            emailMgr.sendEmail(email);
            
            return email;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
        
    }

    public void deleteInstructor(String courseId, String email) {
        instructorsDb.deleteInstructor(courseId, email);
    }

    public void deleteInstructorsForGoogleId(String googleId) {
        instructorsDb.deleteInstructorsForGoogleId(googleId);
    }

    public void deleteInstructorsForCourse(String courseId) {
        instructorsDb.deleteInstructorsForCourse(courseId);
    }

}