package teammates.test.cases.storage;

import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class StudentsDbTest extends BaseComponentTestCase {
    

    private StudentsDb studentsDb = new StudentsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testDefaultTimestamp() throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {        
        
        StudentAttributes s = createNewStudent();
        
        StudentAttributes student = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        assertNotNull(student);
        
        student.setCreated_NonProduction(null);
        student.setUpdatedAt_NonProduction(null);
        
        Date defaultStudentCreationTimeStamp = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        
        ______TS("success : defaultTimeStamp for createdAt date");
        
        assertEquals(defaultStudentCreationTimeStamp, student.getCreatedAt());
        
        ______TS("success : defaultTimeStamp for updatedAt date");
        
        assertEquals(defaultStudentCreationTimeStamp, student.getUpdatedAt());
    }
    
    @Test
    public void testTimestamp() throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {        
        ______TS("success : created");
        
        StudentAttributes s = createNewStudent();
        
        StudentAttributes student = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        assertNotNull(student);
        
        // Assert dates are now.
        AssertHelper.assertDateIsNow(student.getCreatedAt());
        AssertHelper.assertDateIsNow(student.getUpdatedAt());
        
        
        ______TS("success : update lastUpdated");
        
        s.name = "new-name";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team,
                                                     s.section, s.email, s.googleId, s.comments);
        StudentAttributes updatedStudent = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        
        // Assert lastUpdate has changed, and is now.
        assertFalse(student.getUpdatedAt().equals(updatedStudent.getUpdatedAt()));
        AssertHelper.assertDateIsNow(updatedStudent.getUpdatedAt());
        
        ______TS("success : keep lastUpdated");
        
        s.name = "new-name-2";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team,
                                                     s.section, s.email, s.googleId, s.comments, true);
        StudentAttributes updatedStudent2 = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        
        // Assert lastUpdate has NOT changed.
        assertTrue(updatedStudent.getUpdatedAt().equals(updatedStudent2.getUpdatedAt()));
    }
    
    @Test
    public void testCreateStudent() throws EntityAlreadyExistsException, InvalidParametersException {
        
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student";
        s.lastName = "student";
        s.email = "valid-fresh@email.com";
        s.team = "validTeamName";
        s.section = "validSectionName";
        s.comments = "";
        s.googleId = "validGoogleId";

        ______TS("fail : invalid params"); 
        s.course = "invalid id space";
        try {
            studentsDb.createEntity(s);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(COURSE_ID_ERROR_MESSAGE, s.course,
                            REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        verifyAbsentInDatastore(s);

        ______TS("success : valid params");
        s.course = "valid-course";
        
        // remove possibly conflicting entity from the database
        studentsDb.deleteStudent(s.course, s.email);
        
        studentsDb.createEntity(s);
        verifyPresentInDatastore(s);
        StudentAttributes retrievedStudent = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        assertTrue(retrievedStudent.isEnrollInfoSameAs(s));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course + "not existing", s.googleId));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course, s.googleId + "not existing"));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course + "not existing", s.googleId + "not existing"));
        
        ______TS("fail : duplicate");
        try {
            studentsDb.createEntity(s);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(
                    String.format(
                            StudentsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                            s.getEntityTypeAsString())
                            + s.getIdentificationString(), e.getMessage());
        }

        ______TS("null params check");
        try {
            studentsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetStudent() throws InvalidParametersException, EntityDoesNotExistException {
        
        StudentAttributes s = createNewStudent();
        s.googleId = "validGoogleId";
        s.team = "validTeam";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        
        ______TS("typical success case: existent");
        StudentAttributes retrieved = studentsDb.getStudentForEmail(s.course, s.email);
        assertNotNull(retrieved);
        assertNotNull(studentsDb.getStudentForRegistrationKey(retrieved.key));
        assertNotNull(studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(retrieved.key)));
        assertNull(studentsDb.getStudentForRegistrationKey("notExistingKey"));
        ______TS("non existant student case");
        retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
        assertNull(retrieved);
        
        StudentAttributes s2 = createNewStudent("one.new@gmail.com");
        s2.googleId = "validGoogleId2";
        studentsDb.updateStudentWithoutSearchability(s2.course, s2.email, s2.name, s2.team, s2.section, s2.email, s2.googleId, s2.comments);
        studentsDb.deleteStudentsForGoogleIdWithoutDocument(s2.googleId);
        assertNull(studentsDb.getStudentForGoogleId(s2.course, s2.googleId));
        
        s2 = createNewStudent("one.new@gmail.com");
        assertTrue(studentsDb.getUnregisteredStudentsForCourse(s2.course).get(0).isEnrollInfoSameAs(s2));
        
        assertTrue(s.isEnrollInfoSameAs(studentsDb.getStudentsForGoogleId(s.googleId).get(0)));
        assertTrue(studentsDb.getStudentsForCourse(s.course).get(0).isEnrollInfoSameAs(s) ||
                   studentsDb.getStudentsForCourse(s.course).get(0).isEnrollInfoSameAs(s2));
        assertTrue(studentsDb.getStudentsForTeam(s.team, s.course).get(0).isEnrollInfoSameAs(s));
        
        
        ______TS("null params case");
        try {
            studentsDb.getStudentForEmail(null, "valid@email.com");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }        
        try {
            studentsDb.getStudentForEmail("any-course-id", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        studentsDb.deleteStudent(s.course, s.email);
    }
    
    @Test
    public void testupdateStudentWithoutDocument() throws InvalidParametersException, EntityDoesNotExistException {
        
        // Create a new student with valid attributes
        StudentAttributes s = createNewStudent();
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
        
        ______TS("non-existent case");
        try {
            studentsDb.updateStudentWithoutSearchability("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non-existent-section", "non.existent.ID", "blah", "blah");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT + "non-existent-course/non@existent.email", e.getMessage());
        }
        
        // Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
        ______TS("null course case");
        try {
            studentsDb.updateStudentWithoutSearchability(null, s.email, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        ______TS("null email case");
        try {
            studentsDb.updateStudentWithoutSearchability(s.course, null, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        ______TS("duplicate email case");
        s = createNewStudent();
        // Create a second student with different email address
        StudentAttributes s2 = createNewStudent("valid2@email.com");
        try {
            studentsDb.updateStudentWithoutSearchability(s.course, s.email, "new-name", "new-team", "new-section", s2.email, "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_EMAIL_ALREADY_USED + s2.name + "/" + s2.email, 
                         e.getMessage());
        }

        ______TS("typical success case");
        String originalEmail = s.email;
        s.name = "new-name-2";
        s.team = "new-team-2";
        s.email = "new-email-2";
        s.googleId = "new-id-2";
        s.comments = "this are new comments";
        studentsDb.updateStudentWithoutSearchability(s.course, originalEmail, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        
        StudentAttributes updatedStudent = studentsDb.getStudentForEmail(s.course, s.email);
        assertTrue(updatedStudent.isEnrollInfoSameAs(s));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeleteStudent() throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes s = createNewStudent();
        s.googleId = "validGoogleId";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        // Delete
        studentsDb.deleteStudentWithoutDocument(s.course, s.email);
        
        StudentAttributes deleted = studentsDb.getStudentForEmail(s.course, s.email);
        
        assertNull(deleted);
        studentsDb.deleteStudentsForGoogleIdWithoutDocument(s.googleId);
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course, s.googleId));
        int currentStudentNum = studentsDb.getAllStudents().size();
        s = createNewStudent();
        createNewStudent("secondStudent@mail.com");
        assertEquals(2 + currentStudentNum, studentsDb.getAllStudents().size());
        studentsDb.deleteStudentsForCourseWithoutDocument(s.course);
        assertEquals(currentStudentNum, studentsDb.getAllStudents().size());
        // delete again - should fail silently
        studentsDb.deleteStudentWithoutDocument(s.course, s.email);
        
        // Null params check:
        try {
            studentsDb.deleteStudentWithoutDocument(null, s.email);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            studentsDb.deleteStudentWithoutDocument(s.course, null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        studentsDb.deleteStudent(s.course, s.email);

      //Untested case: The deletion is not persisted immediately (i.e. persistence delay) 
      //       Reason: Difficult to reproduce a persistence delay during testing
    }
    
    private StudentAttributes createNewStudent() throws InvalidParametersException {
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student";
        s.course = "valid-course";
        s.email = "valid@email.com";
        s.team = "validTeamName";
        s.section = "validSectionName";
        s.comments = "";
        s.googleId = "";
        try {
            studentsDb.createEntity(s);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it's already inside
            ignorePossibleException();
        }
        
        return s;
    }
    
    private StudentAttributes createNewStudent(String email) throws InvalidParametersException {
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student 2";
        s.course = "valid-course";
        s.email = email;
        s.team = "valid team name";
        s.section = "valid section name";
        s.comments = "";
        s.googleId = "";
        try {
            studentsDb.createEntity(s);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it's already inside
            ignorePossibleException();
        }
        
        return s;
    }
}
