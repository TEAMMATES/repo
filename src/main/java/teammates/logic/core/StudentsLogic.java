package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import com.google.gson.Gson;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.storage.api.StudentsDb;

/**
 * Handles  operations related to student roles.
 */
public class StudentsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, no need for header 
    //  comments.
    
    private static StudentsLogic instance = null;
    private StudentsDb studentsDb = new StudentsDb();
    
    private CoursesLogic coursesLogic = CoursesLogic.inst();
    private EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    
    private static Logger log = Utils.getLogger();
    
    public static StudentsLogic inst() {
        if (instance == null)
            instance = new StudentsLogic();
        return instance;
    }
    
    public void createStudentCascade(StudentAttributes studentData) 
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        
        createStudentCascadeWithSubmissionAdjustmentScheduled(studentData);
        
        if (!coursesLogic.isCoursePresent(studentData.course)) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + studentData.course + "]");
        }
        
        evaluationsLogic.adjustSubmissionsForNewStudent(
                studentData.course, studentData.email, studentData.team);
    }
    
    public void createStudentCascadeWithSubmissionAdjustmentScheduled(StudentAttributes studentData) 
            throws InvalidParametersException, EntityAlreadyExistsException {    
        studentsDb.createEntity(studentData);
    }

    public StudentAttributes getStudentForEmail(String courseId, String email) {
        return studentsDb.getStudentForEmail(courseId, email);
    }

    public StudentAttributes getStudentForCourseIdAndGoogleId(String courseId, String googleId) {
        return studentsDb.getStudentForGoogleId(courseId, googleId);
    }

    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        return studentsDb.getStudentForRegistrationKey(registrationKey);
    }

    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId);
    }

    public List<StudentAttributes> getStudentsForCourse(String courseId) 
            throws EntityDoesNotExistException {
        return studentsDb.getStudentsForCourse(courseId);
    }
    
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        return studentsDb.getStudentsForTeam(teamName, courseId);
    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        return studentsDb.getUnregisteredStudentsForCourse(courseId);
    }
    
    public String getKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {
        
        StudentAttributes studentData = getStudentForEmail(courseId, email);
    
        if (studentData == null) {
            throw new EntityDoesNotExistException("Student does not exist: [" + courseId + "/" + email + "]");
        }
    
        return studentData.key;
    }
    
    public String getEncryptedKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {
        
        StudentAttributes studentData = getStudentForEmail(courseId, email);
        
        if (studentData == null) {
            throw new EntityDoesNotExistException("Student does not exist: [" + courseId + "/" + email + "]");
        }
    
        return StringHelper.encrypt(studentData.key);
    }

    public boolean isStudentInAnyCourse(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId).size()!=0;
    }

    public boolean isStudentInCourse(String courseId, String studentEmail) {
        return studentsDb.getStudentForEmail(courseId, studentEmail) != null;
    }
    
    public boolean isStudentInTeam(String courseId, String teamName, String studentEmail) {
        
        StudentAttributes student = getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return false;
        }
        
        List<StudentAttributes> teammates = getStudentsForTeam(teamName, courseId);        
        for(StudentAttributes teammate : teammates) {
            if (teammate.email.equals(student.email)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        StudentAttributes student1 = getStudentForEmail(courseId, student1Email);
        if(student1 == null) {
            return false;
        }
        return isStudentInTeam(courseId, student1.team, student2Email);
    }
    
    public void updateStudentCascade(String originalEmail, StudentAttributes student) 
            throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
        updateStudentCascadeWithSubmissionAdjustmentScheduled(originalEmail, student);
        
        /* finalEmail is the string to be used to represent a student's email.
         * This is because:
         *  - originalEmail cannot be used when student's email is being updated with a new valid email
         *  - student.email cannot be used always because it is null when non-email attributes
         *    of a student are being updated or when the new email to be updated is invalid
         */
        FieldValidator validator = new FieldValidator();
        String finalEmail = (student.email == null || !validator
                .getInvalidityInfo(FieldType.EMAIL, student.email).isEmpty()) ?
                originalEmail : student.email;
        
        // adjust submissions if moving to a different team
        if (isTeamChanged(originalStudent.team, student.team)) {
            evaluationsLogic.adjustSubmissionsForChangingTeam(student.course, finalEmail, student.team);
            frLogic.updateFeedbackResponsesForChangingTeam(student.course, finalEmail, originalStudent.team, student.team);
        }
    }
    
    public void updateStudentCascadeWithSubmissionAdjustmentScheduled(String originalEmail, 
            StudentAttributes student) 
            throws EntityDoesNotExistException, InvalidParametersException {
        // Edit student uses KeepOriginal policy, where unchanged fields are set
        // as null. Hence, we can't do isValid() for student here.
        // After updateWithReferenceToExistingStudentRecord method called,
        // the student should be valid
    
        // here is like a db access that can be avoided if we really want to optimize the code
        studentsDb.verifyStudentExists(student.course, originalEmail);
        
        StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
        
        // prepare new student
        student.updateWithExistingRecord(originalStudent);
        
        if(!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }
        
        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.googleId, student.comments);    
        
        // cascade email change, if any
        if (!originalEmail.equals(student.email)) {
            evaluationsLogic.updateStudentEmailForSubmissionsInCourse(student.course, originalEmail, student.email);
            frLogic.updateFeedbackResponsesForChangingEmail(student.course, originalEmail, student.email);
        }
    }
    
    public List<StudentAttributes> enrollStudents(String enrollLines,
            String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException {

        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException("Course does not exist :"
                    + courseId);
        }
        
        if (enrollLines.isEmpty()) {
            throw new EnrollException(Const.StatusMessages.ENROLL_LINE_EMPTY);
        }
        
        List<String> invalidityInfo = getInvalidityInfoInEnrollLines(enrollLines, courseId);
        if (!invalidityInfo.isEmpty()) {
            throw new EnrollException(StringHelper.toString(invalidityInfo, "<br>"));
        }

        ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
        ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<StudentEnrollDetails>();
        ArrayList<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
        
        String[] linesArray = enrollLines.split(Const.EOL);

        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);
        
        int startLine;
        if (saf.hasHeader()) {
            startLine = 1;
        } else {
            startLine = 0;
        }
        
        for (int i = startLine; i < linesArray.length; i++) {
            String line = linesArray[i];
            
            if (StringHelper.isWhiteSpace(line)) {
                continue;
            }
            
            StudentAttributes student = saf.makeStudent(line, courseId);
            studentList.add(student);
        }

        // TODO: can we use a batch persist operation here?
        // enroll all students
        for (StudentAttributes student : studentList) {
            StudentEnrollDetails enrollmentDetails;
            
            enrollmentDetails = enrollStudent(student);
            student.updateStatus = enrollmentDetails.updateStatus;
            
            enrollmentList.add(enrollmentDetails);
            returnList.add(student);
        }
        
        //Adjust submissions for each evaluation within the course
        List<EvaluationAttributes> evaluations = evaluationsLogic
                .getEvaluationsForCourse(courseId);
        
        for(EvaluationAttributes eval : evaluations) {
            //Schedule adjustment of submissions for evaluation in course
            scheduleSubmissionAdjustmentForEvaluationInCourse(enrollmentList,courseId,eval.name);
        }
        
        //Adjust submissions for all feedback responses within the course
        List<FeedbackSessionAttributes> feedbackSessions = FeedbackSessionsLogic.inst()
                .getFeedbackSessionsForCourse(courseId);
        
        for (FeedbackSessionAttributes session : feedbackSessions) {
            //Schedule adjustment of submissions for feedback session in course
            scheduleSubmissionAdjustmentForFeedbackInCourse(enrollmentList,courseId,
                    session.feedbackSessionName);
        }

        // add to return list students not included in the enroll list.
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
        for (StudentAttributes student : studentsInCourse) {
            if (!isInEnrollList(student, returnList)) {
                student.updateStatus = StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST;
                returnList.add(student);
            }
        }

        return returnList;
    }

    private void scheduleSubmissionAdjustmentForFeedbackInCourse(
            ArrayList<StudentEnrollDetails> enrollmentList, String courseId, String sessionName) {
        // private methods -- should I test this?
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, sessionName);
        
        Gson gsonBuilder = Utils.getTeammatesGson();
        String enrollmentDetails = gsonBuilder.toJson(enrollmentList);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollmentDetails);
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.FEEDBACK_SUBMISSION_ADJUSTMENT_TASK_QUEUE,
                Const.ActionURIs.FEEDBACK_SUBMISSION_ADJUSTMENT_WORKER, paramMap);
        
    }

    private void scheduleSubmissionAdjustmentForEvaluationInCourse(
            ArrayList<StudentEnrollDetails> enrollmentList, String courseId, String evalName) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.EVALUATION_NAME, evalName);
        
        Gson gsonBuilder = Utils.getTeammatesGson();
        String enrollmentDetails = gsonBuilder.toJson(enrollmentList);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollmentDetails);
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.EVAL_SUBMISSION_ADJUSTMENT_TASK_QUEUE,
                Const.ActionURIs.EVAL_SUBMISSION_ADJUSTMENT_WORKER, paramMap);
    }

    public MimeMessage sendRegistrationInviteToStudent(String courseId, String studentEmail) 
            throws EntityDoesNotExistException {
        
        CourseAttributes course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
        }
        
        StudentAttributes studentData = getStudentForEmail(courseId, studentEmail);
        if (studentData == null) {
            throw new EntityDoesNotExistException(
                    "Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
        }
        
        Emails emailMgr = new Emails();
        try {
            MimeMessage email = emailMgr.generateStudentCourseJoinEmail(course, studentData);
            emailMgr.sendEmail(email);
            return email;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
        
    }
    
    public List<MimeMessage> sendRegistrationInviteForCourse(String courseId) {
        List<StudentAttributes> studentDataList = getUnregisteredStudentsForCourse(courseId);
        
        ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
    
        //TODO: sending mail should be moved to somewhere else.
        for (StudentAttributes s : studentDataList) {
            try {
                MimeMessage email = sendRegistrationInviteToStudent(courseId, s.email);
                emailsSent.add(email);
            } catch (EntityDoesNotExistException e) {
                Assumption
                        .fail("Unexpected EntitiyDoesNotExistException thrown when sending registration email"
                                + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return emailsSent;
    }

    public void deleteStudentCascade(String courseId, String studentEmail) {
        // delete responses first as we need to know the student's team.
        frLogic.deleteFeedbackResponsesForStudent(courseId, studentEmail);
        studentsDb.deleteStudent(courseId, studentEmail);
        SubmissionsLogic.inst().deleteAllSubmissionsForStudent(courseId, studentEmail);
    }

    public void deleteStudentsForGoogleId(String googleId) {
        studentsDb.deleteStudentsForGoogleId(googleId);
    }

    public void deleteStudentsForCourse(String courseId) {
        studentsDb.deleteStudentsForCourse(courseId);
    }
    
    public void adjustSubmissionsForEnrollments(
            ArrayList<StudentEnrollDetails> enrollmentList,
            EvaluationAttributes eval) throws InvalidParametersException, EntityDoesNotExistException {
        // will not be tested as submissions are depreciated
        
        for(StudentEnrollDetails enrollment : enrollmentList) {
            if(enrollment.updateStatus == UpdateStatus.MODIFIED &&
                    isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
                evaluationsLogic.adjustSubmissionsForChangingTeamInEvaluation(enrollment.course,
                        enrollment.email, enrollment.newTeam, eval.name);
            } else if (enrollment.updateStatus == UpdateStatus.NEW) {
                evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(
                        enrollment.course, enrollment.email, enrollment.newTeam, eval.name);
            }
        }
    }
    
    public void adjustFeedbackResponseForEnrollments(
            ArrayList<StudentEnrollDetails> enrollmentList,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {
        for(StudentEnrollDetails enrollment : enrollmentList) {
            if(enrollment.updateStatus == UpdateStatus.MODIFIED &&
                    isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
                frLogic.updateFeedbackResponseForChangingTeam(enrollment, response);
            }
        }
    }
    
    private StudentEnrollDetails enrollStudent(StudentAttributes validStudentAttributes) {
        StudentAttributes originalStudentAttributes = getStudentForEmail(
                validStudentAttributes.course, validStudentAttributes.email);
        
        StudentEnrollDetails enrollmentDetails = new StudentEnrollDetails();
        enrollmentDetails.course = validStudentAttributes.course;
        enrollmentDetails.email = validStudentAttributes.email;
        enrollmentDetails.newTeam = validStudentAttributes.team;
        
        try {
            if (validStudentAttributes.isEnrollInfoSameAs(originalStudentAttributes)) {
                enrollmentDetails.updateStatus = UpdateStatus.UNMODIFIED;
            } else if (originalStudentAttributes != null) {
                updateStudentCascadeWithSubmissionAdjustmentScheduled(originalStudentAttributes.email, validStudentAttributes);
                enrollmentDetails.updateStatus = UpdateStatus.MODIFIED;
                
                if(!originalStudentAttributes.team.equals(validStudentAttributes.team)) {
                    enrollmentDetails.oldTeam = originalStudentAttributes.team;
                }
            } else {
                createStudentCascadeWithSubmissionAdjustmentScheduled(validStudentAttributes);
                enrollmentDetails.updateStatus = UpdateStatus.NEW;
            }
        } catch (Exception e) {
            //TODO: need better error handling here. This error is not 'unexpected'. e.g., invalid student data
            /* Note: If this method is only called by the public method enrollStudents(String,String),
            * then there won't be any invalid student data, since validity check has been done in that method
            */
            enrollmentDetails.updateStatus = UpdateStatus.ERROR;
            String errorMessage = "Exception thrown unexpectedly while enrolling student: " 
                    + validStudentAttributes.toString() + Const.EOL + TeammatesException.toStringWithStackTrace(e);
            log.severe(errorMessage);
        }
        
        return enrollmentDetails;
    }
    
    /* All empty lines or lines with only white spaces will be skipped.
     * The invalidity info returned are in HTML format.
     */
    private List<String> getInvalidityInfoInEnrollLines(String lines, String courseId) throws EnrollException {
        List<String> invalidityInfo = new ArrayList<String>();
        String[] linesArray = lines.split(Const.EOL);
        ArrayList<String>  studentEmailList = new ArrayList<String>();
        
        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);
        
        int startLine;
        if (saf.hasHeader()) {
            startLine = 1;
            studentEmailList.add(new String());
        } else {
            startLine = 0;
        }
        
        for (int i = startLine; i < linesArray.length; i++) {
            String line = linesArray[i];
            try {
                if (StringHelper.isWhiteSpace(line)) {
                    continue;
                }
                StudentAttributes student = saf.makeStudent(line, courseId);
                
                if (!student.isValid()) {
                    String info = StringHelper.toString(student.getInvalidityInfo(),
                                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, info));
                }
                
                if(isStudentEmailDuplicated(student.email,studentEmailList)){
                    String info = StringHelper.toString(getInvalidityInfoInDuplicatedEmail(student.email,studentEmailList,linesArray), 
                                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, info));
                }
            } catch (EnrollException e) {
                String info = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, e.getMessage());
                invalidityInfo.add(info);
            }
        }
        
        return invalidityInfo;
    }
    
    private List<String> getInvalidityInfoInDuplicatedEmail(String email,
            ArrayList<String> studentEmailList,String[] linesArray){
        List<String> info = new ArrayList<String>();
        info.add("Same email address as the student in line \"" + linesArray[studentEmailList.indexOf(email)]+ "\"");
        return info;
    }
    
    private boolean isStudentEmailDuplicated(String email, 
            ArrayList<String> studentEmailList){
        boolean isEmailDuplicated = studentEmailList.contains(email);
        return isEmailDuplicated;
    }
    
    private boolean isInEnrollList(StudentAttributes student,
            ArrayList<StudentAttributes> studentInfoList) {
        for (StudentAttributes studentInfo : studentInfoList) {
            if (studentInfo.email.equalsIgnoreCase(student.email)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTeamChanged(String originalTeam, String newTeam) {
        return (newTeam != null) && (originalTeam != null)
                && (!originalTeam.equals(newTeam));
    }
    
}