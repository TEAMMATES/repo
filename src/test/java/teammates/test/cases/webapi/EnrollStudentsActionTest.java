package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.EnrollStudentsAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.StudentData;
import teammates.ui.webapi.output.StudentsData;
import teammates.ui.webapi.request.StudentsEnrollRequest;

/**
 * SUT: {@link EnrollStudentsAction}.
 */
public class EnrollStudentsActionTest extends BaseActionTest<EnrollStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        // See test cases below.
    }

    @Test
    public void testExecute_withNewStudent_shouldBeAddedToDatabase() {
        String courseId = typicalBundle.students.get("student1InCourse1").getCourse();
        StudentAttributes newStudent = getTypicalNewStudent(courseId);
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(newStudent));

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        List<StudentData> enrolledStudents = executeActionAndReturnResults(courseId, req);

        assertEquals(1, enrolledStudents.size());
        verifyStudentInDatabase(newStudent, enrolledStudents.get(0).getCourseId(), enrolledStudents.get(0).getEmail());
        verifyCorrectResponseData(req.getStudentEnrollRequests().get(0), enrolledStudents.get(0));
    }

    @Test
    public void testExecute_withExistingStudent_shouldBeUpdatedToDatabase() {
        StudentAttributes studentToUpdate = typicalBundle.students.get("student1InCourse1");
        String courseId = studentToUpdate.getCourse();
        studentToUpdate.name = "new name";
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(studentToUpdate));

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        List<StudentData> enrolledStudents = executeActionAndReturnResults(courseId, req);

        assertEquals(1, enrolledStudents.size());
        verifyStudentInDatabase(studentToUpdate, enrolledStudents.get(0).getCourseId(), enrolledStudents.get(0).getEmail());
        verifyCorrectResponseData(req.getStudentEnrollRequests().get(0), enrolledStudents.get(0));
    }

    @Test
    public void testExecute_withEmailFieldChanged_shouldCreateNewStudent() {
        String courseId = typicalBundle.students.get("student1InCourse1").getCourse();
        StudentAttributes originalStudent = typicalBundle.students.get("student1InCourse1");
        StudentAttributes newStudent = originalStudent.getCopy();
        newStudent.email = "newEmail@example.com";
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(newStudent));

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        List<StudentData> enrolledStudents = executeActionAndReturnResults(courseId, req);

        assertEquals(1, enrolledStudents.size());
        verifyStudentInDatabase(newStudent, enrolledStudents.get(0).getCourseId(), enrolledStudents.get(0).getEmail());
        verifyStudentInDatabase(originalStudent, originalStudent.getCourse(), originalStudent.getEmail());
        verifyCorrectResponseData(req.getStudentEnrollRequests().get(0), enrolledStudents.get(0));
    }

    @Test
    public void testExecute_withInvalidEnrollRequests_shouldNotBeEnrolled() {
        String courseId = typicalBundle.students.get("student1InCourse1").getCourse();
        StudentAttributes validNewStudent = getTypicalNewStudent(courseId);
        StudentAttributes invalidNewStudent = getTypicalNewStudent(courseId);
        invalidNewStudent.email = "invalidEmail";
        StudentAttributes validExistingStudent = typicalBundle.students.get("student1InCourse1");
        validExistingStudent.name = "new name";
        StudentAttributes invalidExistingStudent = typicalBundle.students.get("student2InCourse1");
        invalidExistingStudent.team = "invalid | team % name";
        StudentsEnrollRequest req = prepareRequest(
                Arrays.asList(validNewStudent, invalidNewStudent, validExistingStudent, invalidExistingStudent));

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        List<StudentData> enrolledStudents = executeActionAndReturnResults(courseId, req);

        assertEquals(2, enrolledStudents.size());
        verifyStudentInDatabase(validNewStudent, enrolledStudents.get(0).getCourseId(),
                enrolledStudents.get(0).getEmail());
        verifyStudentInDatabase(validExistingStudent, enrolledStudents.get(1).getCourseId(),
                enrolledStudents.get(1).getEmail());
        verifyCorrectResponseData(req.getStudentEnrollRequests().get(0), enrolledStudents.get(0));
        verifyCorrectResponseData(req.getStudentEnrollRequests().get(2), enrolledStudents.get(1));
    }

    @Test
    public void testExecute_withDuplicatedTeamNameAmongSectionsToExistingStudents_shouldThrowInvalidBodyException() {
        String courseId = typicalBundle.courses.get("typicalCourse1").getId();
        StudentAttributes studentInCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1 = getTypicalNewStudent(courseId);
        student1.team = studentInCourse1.getTeam();
        student1.section = "random section 1";
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(student1));
        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        verifyDuplicatedTeamNameDetected(courseId, req, student1.team, studentInCourse1.section, student1.section);
    }

    @Test
    public void testExecute_withDuplicatedTeamNameAmongSectionsInInput_shouldThrowInvalidBodyException() {
        String courseId = typicalBundle.courses.get("typicalCourse1").getId();
        StudentAttributes student1 = getTypicalNewStudent(courseId);
        student1.team = "typical random team";
        student1.section = "random section 1";
        StudentAttributes student2 = getTypicalNewStudent(courseId);
        student2.team = student1.team;
        student2.section = "random section 2";
        student2.email = "differentemail@test.com";
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(student1, student2));
        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        verifyDuplicatedTeamNameDetected(courseId, req, student1.team, student1.section, student2.section);
    }

    private void verifyCorrectResponseData(StudentsEnrollRequest.StudentEnrollRequest request, StudentData response) {
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getSection(), response.getSectionName());
        assertEquals(request.getTeam(), response.getTeamName());
        assertEquals(request.getComments(), response.getComments());
    }

    private void verifyDuplicatedTeamNameDetected(String courseId, StudentsEnrollRequest req, String expectedTeam,
                                                  String expectedSectionOne, String expectedSectionTwo) {
        String expectedMessage = "Team \"%s\" is detected in both Section \"%s\" and Section \"%s\"."
                + " Please use different team names in different sections";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        EnrollStudentsAction action = getAction(req, params);
        InvalidHttpRequestBodyException actualException =
                assertThrows(InvalidHttpRequestBodyException.class, () -> action.execute());
        assertEquals(actualException.getMessage(),
                String.format(expectedMessage, expectedTeam, expectedSectionOne, expectedSectionTwo));
    }

    private StudentsEnrollRequest prepareRequest(List<StudentAttributes> enrolledStudents) {
        List<StudentsEnrollRequest.StudentEnrollRequest> requestList = new ArrayList<>();
        enrolledStudents.forEach(student -> {
            requestList.add(new StudentsEnrollRequest.StudentEnrollRequest(student.getName(),
                    student.getEmail(), student.getTeam(), student.getSection(),
                    student.getComments()));
        });

        return new StudentsEnrollRequest(requestList);
    }

    private List<StudentData> executeActionAndReturnResults(String courseId, StudentsEnrollRequest req) {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = action.execute();
        assertEquals(result.getStatusCode(), HttpStatus.SC_OK);

        return ((StudentsData) result.getOutput()).getStudents();
    }

    private void verifyStudentInDatabase(StudentAttributes expectedStudent,
                                         String actualStudentCourse, String actualStudentEmail) {
        StudentAttributes actualStudent =
                logic.getStudentForEmail(actualStudentCourse, actualStudentEmail);
        assertEquals(expectedStudent.getCourse(), actualStudent.getCourse());
        assertEquals(expectedStudent.getName(), actualStudent.getName());
        assertEquals(expectedStudent.getEmail(), actualStudent.getEmail());
        assertEquals(expectedStudent.getTeam(), actualStudent.getTeam());
        assertEquals(expectedStudent.getSection(), actualStudent.getSection());
        assertEquals(expectedStudent.getComments(), actualStudent.getComments());
    }

    private StudentAttributes getTypicalNewStudent(String courseId) {
        return StudentAttributes
                    .builder(courseId, "email@test.com")
                    .withName("name")
                    .withComment("")
                    .withTeamName("team")
                    .withSectionName("section")
                    .build();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, "",
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
}
