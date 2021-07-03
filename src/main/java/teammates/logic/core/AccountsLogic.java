package teammates.logic.core;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;

/**
 * Handles operations related to accounts.
 *
 * @see AccountAttributes
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final Logger log = Logger.getLogger();

    private static AccountsLogic instance = new AccountsLogic();

    private static final AccountsDb accountsDb = new AccountsDb();

    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private AccountsLogic() {
        // prevent initialization
    }

    public static AccountsLogic inst() {
        return instance;
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the Datastore.
     */
    AccountAttributes createAccount(AccountAttributes accountData)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsDb.createEntity(accountData);
    }

    public AccountAttributes getAccount(String googleId) {
        return accountsDb.getAccount(googleId);
    }

    public boolean isAccountAnInstructor(String googleId) {
        AccountAttributes a = accountsDb.getAccount(googleId);
        return a != null && a.isInstructor;
    }

    public String getCourseInstitute(String courseId) {
        CourseAttributes cd = coursesLogic.getCourse(courseId);
        assert cd != null : "Trying to getCourseInstitute for inexistent course with id " + courseId;
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(cd.getId());

        assert !instructorList.isEmpty() : "Course has no instructors: " + cd.getId();
        // Retrieve institute field from one of the instructors of the course
        String institute = "";
        for (InstructorAttributes instructor : instructorList) {
            String instructorGoogleId = instructor.googleId;
            if (instructorGoogleId == null) {
                continue;
            }
            AccountAttributes instructorAcc = accountsDb.getAccount(instructorGoogleId);
            if (instructorAcc != null) {
                institute = instructorAcc.institute;
                break;
            }
        }
        assert !StringHelper.isEmpty(institute) : "No institute found for the course";
        return institute;
    }

    /**
     * Joins the user as a student.
     */
    public StudentAttributes joinCourseForStudent(String registrationKey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        StudentAttributes student = validateStudentJoinRequest(registrationKey, googleId);

        // Register the student
        student.googleId = googleId;
        try {
            studentsLogic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(student.course, student.email)
                            .withGoogleId(student.googleId)
                            .build());
        } catch (EntityDoesNotExistException e) {
            assert false : "Student disappeared while trying to register " + TeammatesException.toStringWithStackTrace(e);
        }

        if (accountsDb.getAccount(googleId) == null) {
            createStudentAccount(student);
        }

        return student;
    }

    /**
     * Joins the user as an instructor and sets the institute if it is not null.
     * If the given institute is null, the instructor is given the institute of an existing instructor of the same course.
     */
    public InstructorAttributes joinCourseForInstructor(String encryptedKey, String googleId, String institute,
            String mac, String timezone)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        InstructorAttributes instructor = validateInstructorJoinRequest(encryptedKey, googleId, institute, mac);

        // Register the instructor
        instructor.googleId = googleId;
        try {
            instructorsLogic.updateInstructorByEmail(
                    InstructorAttributes.updateOptionsWithEmailBuilder(instructor.courseId, instructor.email)
                            .withGoogleId(instructor.googleId)
                            .build());
        } catch (EntityDoesNotExistException e) {
            assert false : "Instructor disappeared while trying to register "
                    + TeammatesException.toStringWithStackTrace(e);
        }

        AccountAttributes account = accountsDb.getAccount(googleId);
        String instituteToSave = institute == null ? getCourseInstitute(instructor.courseId) : institute;

        if (account == null) {
            try {
                createAccount(AccountAttributes.builder(googleId)
                        .withName(instructor.name)
                        .withEmail(instructor.email)
                        .withInstitute(instituteToSave)
                        .withIsInstructor(true)
                        .build());
            } catch (EntityAlreadyExistsException e) {
                assert false : "Account already exists.";
            }
        } else {
            makeAccountInstructor(googleId);
        }

        // Update the googleId of the student entity for the instructor which was created from sample data.
        StudentAttributes student = studentsLogic.getStudentForEmail(instructor.courseId, instructor.email);
        if (student != null) {
            student.googleId = googleId;
            studentsLogic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(student.course, student.email)
                            .withGoogleId(student.googleId)
                            .build());
        }

        // Update timezone of course if needed
        if (timezone != null) {
            try {
                coursesLogic.updateCourseCascade(CourseAttributes.updateOptionsBuilder(instructor.courseId)
                        .withTimezone(ZoneId.of(timezone))
                        .build());
            } catch (DateTimeException dte) {
                log.info("Timezone '" + timezone + "' of sample course with id '" + instructor.courseId
                        + "' is not supported. UTC will be used instead.");
            }
        }

        return instructor;
    }

    private InstructorAttributes validateInstructorJoinRequest(String encryptedKey,
                                                               String googleId,
                                                               String institute,
                                                               String mac)
            throws EntityDoesNotExistException, EntityAlreadyExistsException, InvalidParametersException {

        if (institute != null && !StringHelper.isCorrectSignature(institute, mac)) {
            throw new InvalidParametersException("Institute authentication failed.");
        }

        InstructorAttributes instructorForKey = instructorsLogic.getInstructorForRegistrationKey(encryptedKey);

        if (instructorForKey == null) {
            throw new EntityDoesNotExistException("No instructor with given registration key: " + encryptedKey);
        }

        if (instructorForKey.isRegistered()) {
            if (instructorForKey.googleId.equals(googleId)) {
                AccountAttributes existingAccount = accountsDb.getAccount(googleId);
                if (existingAccount != null && existingAccount.isInstructor) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            // Check if this Google ID has already joined this course
            InstructorAttributes existingInstructor =
                    instructorsLogic.getInstructorForGoogleId(instructorForKey.courseId, googleId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private StudentAttributes validateStudentJoinRequest(String encryptedKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        StudentAttributes studentRole = studentsLogic.getStudentForRegistrationKey(encryptedKey);

        if (studentRole == null) {
            throw new EntityDoesNotExistException("No student with given registration key: " + encryptedKey);
        }

        if (studentRole.isRegistered()) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        // Check if this Google ID has already joined this course
        StudentAttributes existingStudent =
                studentsLogic.getStudentForCourseIdAndGoogleId(studentRole.course, googleId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }

    /**
     * Downgrades an instructor account to student account.
     *
     * <p>Cascade deletes all instructors associated with the account.
     */
    public void downgradeInstructorToStudentCascade(String googleId) throws EntityDoesNotExistException {
        instructorsLogic.deleteInstructorsForGoogleIdCascade(googleId);

        try {
            accountsDb.updateAccount(
                    AccountAttributes.updateOptionsBuilder(googleId)
                            .withIsInstructor(false)
                            .build()
            );
        } catch (InvalidParametersException e) {
            assert false : "Invalid account data detected unexpectedly "
                    + "while removing instruction privileges from account " + googleId + ": " + e.getMessage();
        }
    }

    /**
     * Makes an account as an instructor account.
     */
    void makeAccountInstructor(String googleId) throws InvalidParametersException, EntityDoesNotExistException {
        accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(googleId)
                        .withIsInstructor(true)
                        .build()
        );
    }

    /**
     * Deletes both instructor and student privileges, as long as the account and associated student profile.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     */
    public void deleteAccountCascade(String googleId) {
        if (accountsDb.getAccount(googleId) == null) {
            return;
        }

        profilesLogic.deleteStudentProfile(googleId);

        // to prevent orphan course
        List<InstructorAttributes> instructorsToDelete =
                instructorsLogic.getInstructorsForGoogleId(googleId, false);
        for (InstructorAttributes instructorToDelete : instructorsToDelete) {
            if (instructorsLogic.getInstructorsForCourse(instructorToDelete.getCourseId()).size() <= 1) {
                // the instructor is the last instructor in the course
                coursesLogic.deleteCourseCascade(instructorToDelete.getCourseId());
            }
        }

        instructorsLogic.deleteInstructorsForGoogleIdCascade(googleId);
        studentsLogic.deleteStudentsForGoogleIdCascade(googleId);
        accountsDb.deleteAccount(googleId);
    }

    /**
     * Creates a student account.
     */
    private void createStudentAccount(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {

        AccountAttributes account = AccountAttributes.builder(student.googleId)
                .withEmail(student.email)
                .withName(student.name)
                .withIsInstructor(false)
                .withInstitute(getCourseInstitute(student.course))
                .build();

        accountsDb.createEntity(account);
    }

}
