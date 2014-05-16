package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.AccountsDb;

/**
 * Handles the logic related to accounts.
 */
public class AccountsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, no need for header 
    //  comments.
        
    private static AccountsLogic instance = null;
    private static final AccountsDb accountsDb = new AccountsDb();
    
    private static Logger log = Utils.getLogger();
    
    public static AccountsLogic inst() {
        if (instance == null)
            instance = new AccountsLogic();
        return instance;
    }
    
    
    public void createAccount(AccountAttributes accountData) 
                    throws InvalidParametersException {
    
        List<String> invalidityInfo = accountData.getInvalidityInfo();
        if (!invalidityInfo.isEmpty()) {
            throw new InvalidParametersException(invalidityInfo);
        }
        
        log.info("going to create account :\n"+accountData.toString());
        
        accountsDb.createAccount(accountData);
    }
    
    /**
     * <b>Note: Now used for the purpose of testing only.</b><br>
     */
    public void createInstructorAccount(String googleId, String courseId,
            String name, String email, String institute)
                    throws InvalidParametersException, EntityAlreadyExistsException {

        InstructorsLogic.inst().createInstructor(googleId, courseId, name, email);

        // Create the Account if it does not exist
        if (accountsDb.getAccount(googleId) == null) {
            AccountAttributes accountToAdd = new AccountAttributes(googleId, name, true, email, institute);
            createAccount(accountToAdd);
        } else {
            makeAccountInstructor(googleId);
        }
    }

    public AccountAttributes getAccount(String googleId) {
        return accountsDb.getAccount(googleId);
    }
    
    public boolean isAccountPresent(String googleId) {
        return accountsDb.getAccount(googleId) != null;
    }
    
    public boolean isAccountAnInstructor(String googleId) {
        AccountAttributes a = accountsDb.getAccount(googleId);
        return a == null ? false : a.isInstructor;
    }

    public List<AccountAttributes> getInstructorAccounts() {
        return accountsDb.getInstructorAccounts();
    }
    
    public String getCourseInstitute(String courseId) {
        CourseAttributes cd = new CoursesLogic().getCourse(courseId);
        Assumption.assertNotNull("Trying to getCourseInstitute for inexistent course with id " + courseId, cd);
        List<InstructorAttributes> instructorList = InstructorsLogic.inst().getInstructorsForCourse(cd.id);
        
        Assumption.assertTrue("Course has no instructors: " + cd.id, !instructorList.isEmpty());
        // Retrieve institute field from one of the instructors of the course
        String institute = "";
        for (int i=0; i<instructorList.size(); i++) {
            String instructorGoogleId = instructorList.get(i).googleId;
            if(instructorGoogleId==null){
                continue;
            }
            AccountAttributes instructorAcc = accountsDb.getAccount(instructorGoogleId);
            if (instructorAcc != null) {
                institute = instructorAcc.institute;
                break;
            }
        }
        Assumption.assertNotEmpty("No institute found for the course", institute);
        return institute;
    }

    public void updateAccount(AccountAttributes account) throws InvalidParametersException {
        accountsDb.updateAccount(account);
    }
    
    public void joinCourseForStudent(String registrationKey, String googleId) 
            throws JoinCourseException {
        
        verifyStudentJoinCourseRequest(registrationKey, googleId);
        
        StudentAttributes student = StudentsLogic.inst().getStudentForRegistrationKey(registrationKey);
        
        //register the student
        student.googleId = googleId;
        try {
            StudentsLogic.inst().updateStudentCascade(student.email, student);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Student disappered while trying to register " + TeammatesException.toStringWithStackTrace(e));
        } catch (InvalidParametersException e) {
            throw new JoinCourseException(e.getMessage());
        } 
        
        if (accountsDb.getAccount(googleId) == null) {
            try {
                createStudentAccount(student);
            } catch (InvalidParametersException e) {
                throw new JoinCourseException(e.getLocalizedMessage());
            }
        }
    }
    
    public void joinCourseForInstructor(String encryptedKey, String googleId)
            throws JoinCourseException {
        
        verifyInstructorJoinCourseRequest(encryptedKey, googleId);
        
        InstructorAttributes instructor = InstructorsLogic.inst().getInstructorForRegistrationKey(encryptedKey);
        
        instructor.googleId = googleId;
        try {
            InstructorsLogic.inst().updateInstructorByEmail(instructor.email, instructor);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            throw new JoinCourseException(e.getMessage());
        }
        
        AccountAttributes account = accountsDb.getAccount(googleId);
        if(account == null) {
            try {
                createInstructorAccount(instructor);
            } catch (InvalidParametersException e) {
                throw new JoinCourseException(e.getMessage());
            }
        } else {
            makeAccountInstructor(googleId);
        }
    }
    
    private void verifyInstructorJoinCourseRequest(String encryptedKey, String googleId)
            throws JoinCourseException {
        
        InstructorAttributes instructorRole = InstructorsLogic.inst().getInstructorForRegistrationKey(encryptedKey);
        
        if (instructorRole == null) {
            String joinUrl = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN + "?regkey=" + encryptedKey;
            
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                    "You have used an invalid join link: " + joinUrl);
        } else if (instructorRole.isRegistered()) {
            if (instructorRole.googleId.equals(googleId)) {
                throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED,
                        googleId + " has already joined this course");
            } else {
                throw new JoinCourseException(Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                        String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                truncateGoogleId(instructorRole.googleId)));
            }
        }
    
        InstructorAttributes existingInstructor =
                InstructorsLogic.inst().getInstructorForGoogleId(instructorRole.courseId, googleId);
        
        if (existingInstructor != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                            googleId));
        }
    }
    
    private void verifyStudentJoinCourseRequest(String encryptedKey, String googleId)
            throws JoinCourseException {
        
        StudentAttributes studentRole = StudentsLogic.inst().getStudentForRegistrationKey(encryptedKey);
        
        if(studentRole == null){
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                    "You have used an invalid join link: "
                            + Const.ActionURIs.STUDENT_COURSE_JOIN 
                            + "?regkey=" + encryptedKey);
        } else if (studentRole.isRegistered()) {
            if (studentRole.googleId.equals(googleId)) {
                throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED,
                        googleId + " has already joined this course");
            } else {
                throw new JoinCourseException(
                        Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                        String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                    truncateGoogleId(studentRole.googleId)));
            }
        } 
    
        StudentAttributes existingStudent =
                StudentsLogic.inst().getStudentForGoogleId(studentRole.course, googleId);
        
        if (existingStudent != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                            googleId));
        }
    }

    public void downgradeInstructorToStudentCascade(String googleId) {
        InstructorsLogic.inst().deleteInstructorsForGoogleId(googleId);
        makeAccountNonInstructor(googleId);
    }

    public void makeAccountNonInstructor(String googleId) {
        AccountAttributes account = accountsDb.getAccount(googleId);
        if (account != null) {
            account.isInstructor = false;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException e) {
                Assumption.fail("Invalid account data detected unexpectedly " +
                        "while removing instruction privileges from account :"+account.toString());
            }
        }else {
            log.warning("Accounts logic trying to modify non-existent account a non-instructor :" + googleId );
        }
    }

    public void makeAccountInstructor(String googleId) {
        
        AccountAttributes account = accountsDb.getAccount(googleId);
        
        if (account != null) {
            account.isInstructor = true;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException e) {
                Assumption.fail("Invalid account data detected unexpectedly " +
                        "while adding instruction privileges to account :"+account.toString());
            }
        } else {
            log.warning("Accounts logic trying to modify non-existent account an instructor:" + googleId );
        }
    }

    public void deleteAccountCascade(String googleId) {
        InstructorsLogic.inst().deleteInstructorsForGoogleId(googleId);
        StudentsLogic.inst().deleteStudentsForGoogleId(googleId);
        accountsDb.deleteAccount(googleId); 
        //TODO: deal with orphan courses, submissions etc.
    }
    
    private void createStudentAccount(StudentAttributes student) throws InvalidParametersException {
        AccountAttributes account = new AccountAttributes();
        account.googleId = student.googleId;
        account.email = student.email;
        account.name = student.name;
        account.isInstructor = false;
        account.institute = getCourseInstitute(student.course);
        accountsDb.createAccount(account);
    }
    
    private void createInstructorAccount(InstructorAttributes instructor) throws InvalidParametersException {
        AccountAttributes account = new AccountAttributes();
        account.googleId = instructor.googleId;
        account.email = instructor.email;
        account.name = instructor.name;
        account.isInstructor = true;
        account.institute = getCourseInstitute(instructor.courseId);
        accountsDb.createAccount(account);
    }

    private String truncateGoogleId(String googleId) {
        String frontPart = googleId.substring(0, googleId.length() / 3);
        String endPart = googleId.substring(2 * googleId.length() / 3);
        return frontPart + ".." + endPart;
    }
}