package teammates.storage.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Instructor;

/**
 * Handles CRUD Operations for instructor roles.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 * 
 */
public class InstructorsDb extends EntitiesDb{
    
    private static final Logger log = Utils.getLogger();
        
    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return null if no matching objects. 
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
    
        Instructor i = getInstructorEntityForEmail(courseId, email);
    
        if (i == null) {
            log.info("Trying to get non-existent Instructor: " + courseId +"/"+ email );
            return null;
        }
    
        return new InstructorAttributes(i);
    }

    
    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return null if no matching objects. 
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
    
        Instructor i = getInstructorEntityForGoogleId(courseId, googleId);
    
        if (i == null) {
            log.info("Trying to get non-existent Instructor: " + googleId);
            return null;
        }
    
        return new InstructorAttributes(i);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if no matching instructor.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey){
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, encryptedKey);
        
        encryptedKey = encryptedKey.trim();
        String decryptedKey = StringHelper.decrypt(encryptedKey);
        
        Instructor instructor = getInstructorEntityForRegistrationKey(decryptedKey);
        if (instructor == null) {
            return null;
        }
    
        return new InstructorAttributes(instructor);
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return empty list if no matching objects. 
     */
    public List<InstructorAttributes> getInstructorsForEmail(String email) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        
        List<Instructor> instructorList = getInstructorEntitiesForEmail(email);
        
        List<InstructorAttributes> instructorDataList = new ArrayList<InstructorAttributes>();
        for (Instructor i : instructorList) {
            if(!JDOHelper.isDeleted(i)){
                instructorDataList.add(new InstructorAttributes(i));
            }
        }
        
        return instructorDataList;
    }
    
    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return empty list if no matching objects. 
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        
        List<Instructor> instructorList = getInstructorEntitiesForGoogleId(googleId);
        
        List<InstructorAttributes> instructorDataList = new ArrayList<InstructorAttributes>();
        for (Instructor i : instructorList) {
            if(!JDOHelper.isDeleted(i)){
                instructorDataList.add(new InstructorAttributes(i));
            }
        }
        
        return instructorDataList;
    }
    
    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return empty list if no matching objects. 
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Instructor> instructorList = getInstructorEntitiesForCourse(courseId);
        
        List<InstructorAttributes> instructorDataList = new ArrayList<InstructorAttributes>();
        for (Instructor i : instructorList) {
            if(!JDOHelper.isDeleted(i)){
                instructorDataList.add(new InstructorAttributes(i));
            }
        }
        
        return instructorDataList;
    }
    
    /**
     * Not scalable. Don't use unless for admin features.
     * @return {@code InstructorAttributes} objects for all instructor 
     * roles in the system.
     */
    @Deprecated
    public List<InstructorAttributes> getAllInstructors() {
        
        List<InstructorAttributes> list = new LinkedList<InstructorAttributes>();
        List<Instructor> entities = getInstructorEntities();
        Iterator<Instructor> it = entities.iterator();
        while(it.hasNext()) {
            list.add(new InstructorAttributes(it.next()));
        }    
        return list;
    }
    

    /**
     * Updates the instructor. Cannot modify Course ID or google id.
     * Updates only name and email.<br>
     * Does not follow the 'keep existing' policy <br> 
     * @throws InvalidParametersException 
     * @throws EntityDoesNotExistException 
     */
    public void updateInstructorByGoogleId(InstructorAttributes instructorAttributesToUpdate) throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instructorAttributesToUpdate);
         
        if (!instructorAttributesToUpdate.isValid()) {
            throw new InvalidParametersException(instructorAttributesToUpdate.getInvalidityInfo());
        }
        instructorAttributesToUpdate.sanitizeForSaving();
        
        Instructor instructorToUpdate = getInstructorEntityForGoogleId(
                instructorAttributesToUpdate.courseId, 
                instructorAttributesToUpdate.googleId);
        
        if(instructorToUpdate == null){
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + instructorAttributesToUpdate.googleId
                        + ThreadHelper.getCurrentThreadStack());
        }

        instructorToUpdate.setName(instructorAttributesToUpdate.name);
        instructorToUpdate.setEmail(instructorAttributesToUpdate.email);
        
        //TODO: update institute name
        //TODO: make courseId+email the non-modifiable values
        
        getPM().close();
    }
    
    /**
     * Updates the instructor. Cannot modify Course ID or email.
     * Updates only Google ID and name.<br>
     * Does not follow the 'keep existing' policy <br> 
     * @throws InvalidParametersException 
     */
    public void updateInstructorByEmail(InstructorAttributes instructorAttributesToUpdate) throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instructorAttributesToUpdate);
        
        if (!instructorAttributesToUpdate.isValid()) {
            throw new InvalidParametersException(instructorAttributesToUpdate.getInvalidityInfo());
        }
        instructorAttributesToUpdate.sanitizeForSaving();
        
        Instructor instructorToUpdate = getInstructorEntityForEmail(
                instructorAttributesToUpdate.courseId, 
                instructorAttributesToUpdate.email);
        
        if(instructorToUpdate == null){
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + instructorAttributesToUpdate.email
                        + ThreadHelper.getCurrentThreadStack());
        }
        
        instructorToUpdate.setGoogleId(instructorAttributesToUpdate.googleId);
        instructorToUpdate.setName(instructorAttributesToUpdate.name);
        
        //TODO: update institute name
        //TODO: make courseId+email the non-modifiable values
        
        getPM().close();
    }
    
    /**
     * Fails silently if no such instructor. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     */
    public void deleteInstructor(String courseId, String email) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Instructor instructorToDelete = getInstructorEntityForEmail(courseId, email);

        if (instructorToDelete == null) {
            return;
        }

        getPM().deletePersistent(instructorToDelete);
        getPM().flush();

        // Check delete operation persisted
        int elapsedTime = 0;
        Instructor instructorCheck = getInstructorEntityForEmail(courseId, email);
        while ((instructorCheck != null)
                && (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
            ThreadHelper.waitBriefly();
            instructorCheck = getInstructorEntityForEmail(courseId, email);
            elapsedTime += ThreadHelper.WAIT_DURATION;
        }
        if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
            log.severe("Operation did not persist in time: deleteInstructor->"
                    + email);
        }

        //TODO: reuse the method in the parent class instead
    }
    
    /**
     * Fails silently if no such instructor. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     */
    public void deleteInstructorsForGoogleId(String googleId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        List<Instructor> instructorList = getInstructorEntitiesForGoogleId(googleId);

        getPM().deletePersistentAll(instructorList);
        getPM().flush();
    }
    
    /**
     * Fails silently if no such instructor. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     */
    public void deleteInstructorsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Instructor> instructorList = getInstructorEntitiesForCourse(courseId);

        getPM().deletePersistentAll(instructorList);
        getPM().flush();
    }
    
    private Instructor getInstructorEntityForGoogleId(String courseId, String googleId) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String googleIdParam, String courseIdParam");
        q.setFilter("googleId == googleIdParam && courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(googleId, courseId);
        
        if (instructorList.isEmpty()
                || JDOHelper.isDeleted(instructorList.get(0))) {
            return null;
        }

        return instructorList.get(0);
    }
    
    private Instructor getInstructorEntityForEmail(String courseId, String email) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String courseIdParam, String emailParam");
        q.setFilter("courseId == courseIdParam && email == emailParam");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(courseId, email);
        
        if (instructorList.isEmpty()
                || JDOHelper.isDeleted(instructorList.get(0))) {
            return null;
        }

        return instructorList.get(0);
    }
    
    private Instructor getInstructorEntityForRegistrationKey(String key) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String regKey");
        q.setFilter("registrationKey == regKey");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(key);
        
        if (instructorList.isEmpty()
                || JDOHelper.isDeleted(instructorList.get(0))) {
            return null;
        }

        return instructorList.get(0);
    }
    
    private List<Instructor> getInstructorEntitiesForGoogleId(String googleId) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String googleIdParam");
        q.setFilter("googleId == googleIdParam");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(googleId);
        
        return instructorList;
    }
    
    private List<Instructor> getInstructorEntitiesForEmail(String email) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String emailParam");
        q.setFilter("email == emailParam");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(email);
        
        return instructorList;
    }

    private List<Instructor> getInstructorEntitiesForCourse(String courseId) {
        
        Query q = getPM().newQuery(Instructor.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(courseId);
        
        return instructorList;
    }

    private List<Instructor> getInstructorEntities() {
        
        String query = "select from " + Instructor.class.getName();
            
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) getPM()
                .newQuery(query).execute();
    
        return instructorList;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        
        InstructorAttributes instructorToGet = (InstructorAttributes) attributes;    
            
        return getInstructorEntityForEmail(instructorToGet.courseId, instructorToGet.email);
    }
    

}

