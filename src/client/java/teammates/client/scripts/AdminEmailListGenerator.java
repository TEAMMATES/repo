package teammates.client.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;

/**
 * Generates txt file which contains a list of receiver emails.<br>
 * List file configuration is preset in admin email configuration section.
 */
public class AdminEmailListGenerator extends RemoteApiClient {

    private enum StudentStatus {
        REG, UNREG, ALL
    }

    private enum InstructorStatus {
        REG, UNREG, ALL
    }

    private int iterationCounter;

    //handle test data
    private boolean includeTestData = true;

    //admin email configuration
    private boolean student;
    private boolean instructor = true;
    private StudentStatus studentStatus = StudentStatus.ALL;
    private InstructorStatus instructorStatus = InstructorStatus.ALL;
    private String studentCreatedDateRangeStart = "02/03/2013";
    private String studentCreatedDateRangeEnd = "06/03/2015";
    private String instructorCreatedDateRangeStart;
    private String instructorCreatedDateRangeEnd = "31/12/2015";
    private String filePathForSaving = "C:\\Users\\Mo\\Desktop\\";

    private EmailListConfig emailListConfig = new EmailListConfig();
    private HashMap<String, Date> courseIdToCreatedDateMap = new HashMap<String, Date>();

    public static void main(String[] args) throws IOException {
        AdminEmailListGenerator adminEmailListGenerator = new AdminEmailListGenerator();
        adminEmailListGenerator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {

        try {
            getInstructorEmailConfiguration();
            getStudentEmailConfiguration();
            printToFile();
        } catch (InvalidParametersException e) {
            System.out.print(e.getMessage() + "\n");
        }

        System.out.print("\n\nstudent : " + emailListConfig.student + "\n");

        if (emailListConfig.student) {
            System.out.print("Student Status: ");
            switch (emailListConfig.studentStatus) {
                case REG:
                    System.out.print("REG\n");
                    break;
                case UNREG:
                    System.out.print("UNREG\n");
                    break;
                case ALL:
                default:
                    System.out.print("ALL\n");
                    break;
            }
        }

        if (emailListConfig.studentCreatedDateRangeStart != null) {
            System.out.print("student start : " + emailListConfig.studentCreatedDateRangeStart + "\n");
        }

        if (emailListConfig.studentCreatedDateRangeEnd != null) {
            System.out.print("student end : " + emailListConfig.studentCreatedDateRangeEnd + "\n");
        }

        System.out.print("instructor : " + emailListConfig.instructor + "\n");

        if (emailListConfig.instructor) {
            System.out.print("Instructor Status: ");
            switch (emailListConfig.studentStatus) {
                case REG:
                    System.out.print("REG\n");
                    break;
                case UNREG:
                    System.out.print("UNREG\n");
                    break;
                case ALL:
                default:
                    System.out.print("ALL\n");
                    break;
            }
        }

        if (emailListConfig.instructorCreatedDateRangeStart != null) {
            System.out.print("instructor start : " + emailListConfig.instructorCreatedDateRangeStart + "\n");
        }

        if (emailListConfig.instructorCreatedDateRangeEnd != null) {
            System.out.print("instructor end : " + emailListConfig.instructorCreatedDateRangeEnd + "\n");
        }

    }

    private void getInstructorEmailConfiguration() throws InvalidParametersException {
        emailListConfig.instructor = this.instructor;
        if (!emailListConfig.instructor) {
            return;
        }
        emailListConfig.instructorStatus = this.instructorStatus;
        emailListConfig.instructorCreatedDateRangeStart = getInputDate(instructorCreatedDateRangeStart);
        emailListConfig.instructorCreatedDateRangeEnd = getInputDate(instructorCreatedDateRangeEnd);
    }

    private void getStudentEmailConfiguration() throws InvalidParametersException {
        emailListConfig.student = this.student;
        if (!emailListConfig.student) {
            return;
        }
        emailListConfig.studentStatus = this.studentStatus;
        emailListConfig.studentCreatedDateRangeStart = getInputDate(studentCreatedDateRangeStart);
        emailListConfig.studentCreatedDateRangeEnd = getInputDate(studentCreatedDateRangeEnd);
    }

    private Date getInputDate(String dateString) throws InvalidParametersException {

        if (dateString == null) {
            return null;
        }

        String[] split = dateString.split("/");
        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);
        if (!isValidDate(day, month, year)) {
            throw new InvalidParametersException("Date format error");
        }
        return getDate(day, month, year);

    }

    private void printToFile() {

        if (!emailListConfig.student && !emailListConfig.instructor) {
            System.out.print("No email list to be generated. Exiting now..\n\n");
            return;
        }

        HashSet<String> studentEmailSet = new HashSet<String>();
        HashSet<String> instructorEmailSet = new HashSet<String>();

        if (emailListConfig.student) {
            studentEmailSet = addStudentEmailIntoSet(studentEmailSet);
        }

        if (emailListConfig.instructor) {
            instructorEmailSet = addInstructorEmailIntoSet(instructorEmailSet);
        }

        writeEmailsIntoTextFile(studentEmailSet, instructorEmailSet);
    }

    private HashSet<String> addInstructorEmailIntoSet(HashSet<String> instructorEmailSet) {
        String q = "SELECT FROM " + Instructor.class.getName();
        @SuppressWarnings("unchecked")
        List<Instructor> allInstructors = (List<Instructor>) PM.newQuery(q).execute();

        for (Instructor instructor : allInstructors) {
            if (googleIdNotNullAndInstructorStatusREG(instructor)
                    || googleIdNullAndInstructorStatusUNREG(instructor)
                    || emailListConfig.instructorStatus == InstructorStatus.ALL) {
                if (isInstructorCreatedInRange(instructor)) {
                    instructorEmailSet.add(instructor.getEmail());
                }
            }
            updateProgressIndicator();
        }

        return instructorEmailSet;
    }

    private boolean googleIdNotNullAndInstructorStatusREG(Instructor instructor) {
        return instructor.getGoogleId() != null && emailListConfig.instructorStatus == InstructorStatus.REG;
    }

    private boolean googleIdNullAndInstructorStatusUNREG(Instructor instructor) {
        return instructor.getGoogleId() == null && emailListConfig.instructorStatus == InstructorStatus.UNREG;
    }

    private HashSet<String> addStudentEmailIntoSet(HashSet<String> studentEmailSet) {
        String q = "SELECT FROM " + CourseStudent.class.getName();
        @SuppressWarnings("unchecked")
        List<CourseStudent> allStudents = (List<CourseStudent>) PM.newQuery(q).execute();

        for (CourseStudent student : allStudents) {
            if ((isRegistered(student) && emailListConfig.studentStatus == StudentStatus.REG
                    || !isRegistered(student) && emailListConfig.studentStatus == StudentStatus.UNREG
                    || emailListConfig.studentStatus == StudentStatus.ALL)
                    && isStudentCreatedInRange(student)) {
                studentEmailSet.add(student.getEmail());
            }
            updateProgressIndicator();
        }
        return studentEmailSet;
    }

    private boolean isRegistered(CourseStudent student) {
        return student.getGoogleId() != null && !student.getGoogleId().isEmpty();
    }

    private void writeEmailsIntoTextFile(HashSet<String> studentEmailSet,
            HashSet<String> instructorEmailSet) {

        try {

            File newFile = new File(filePathForSaving + this.getCurrentDateForDisplay() + ".txt");
            FileOutputStream fos = new FileOutputStream(newFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            Writer w = new BufferedWriter(osw);

            int studentEmailCount = 0;
            if (!studentEmailSet.isEmpty()) {
                for (String email : studentEmailSet) {
                    if (!includeTestData && email.endsWith(".tmt")) {
                        continue;
                    }
                    w.write(email + ",");
                    studentEmailCount++;
                }
            }

            int instructorEmailCount = 0;
            if (!instructorEmailSet.isEmpty()) {
                for (String email : instructorEmailSet) {
                    if (!includeTestData && email.endsWith(".tmt")) {
                        continue;
                    }
                    w.write(email + ",");
                    instructorEmailCount++;
                }
            }

            System.out.print("Student email num: " + studentEmailCount + "\n");
            System.out.print("Instructor email num: " + instructorEmailCount + "\n");
            w.close();

        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }
    }

    private boolean isInstructorCreatedInRange(Instructor instructor) {

        Date instructorCreatedAt = getInstructorCreatedDate(instructor);
        boolean instructorCreatedDateRangeEndCheckIsNull = (emailListConfig.instructorCreatedDateRangeEnd == null);
        boolean instructorCreatedDateRangeStartCheckIsNull = (emailListConfig.instructorCreatedDateRangeStart == null);
        if (instructorCreatedAt == null) {
            return false;
        }

        if (instructorCreatedDateRangeEndCheckIsNull) {

            if (instructorCreatedDateRangeStartCheckIsNull) {
                //no range set
                return true;
            }
            //after a specific date
            return instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart);

        }

        if (instructorCreatedDateRangeStartCheckIsNull) {
            //before a specific date
            return instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd);

        }
        //within a date interval
        return instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart)
                && instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd);


    }

    private Date getInstructorCreatedDate(Instructor instructor) {

        if (instructor.getGoogleId() != null && !instructor.getGoogleId().isEmpty()) {
            Account account = getAccountEntity(instructor.getGoogleId());
            if (account != null) {
                return account.getCreatedAt();
            }
        }

        if (courseIdToCreatedDateMap.get(instructor.getCourseId()) != null) {
            return courseIdToCreatedDateMap.get(instructor.getCourseId());
        }

        Course course = getCourseEntity(instructor.getCourseId());

        if (course != null) {
            courseIdToCreatedDateMap.put(instructor.getCourseId(), course.getCreatedAt());
            return course.getCreatedAt();
        }

        return null;

    }

    private boolean isStudentCreatedInRange(CourseStudent student) {

        Date studentCreatedAt = getStudentCreatedDate(student);
        boolean studentCreatedDateRangeEndIsNull = emailListConfig.studentCreatedDateRangeEnd == null;
        boolean studentCreatedDateRangeStartIsNull = emailListConfig.studentCreatedDateRangeStart == null;

        if (studentCreatedAt == null) {
            return false;
        }

        if (studentCreatedDateRangeEndIsNull) {

            if (studentCreatedDateRangeStartIsNull) {
                //no range set
                return true;
            } else if (!studentCreatedDateRangeStartIsNull) {
                //after a specific date
                return studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart);
            }
        } else if (!studentCreatedDateRangeEndIsNull) {
            if (studentCreatedDateRangeStartIsNull) {
                //before a specific date
                return studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd);
            } else if (!studentCreatedDateRangeStartIsNull) {
                //within a date interval
                return studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart)
                        && studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd);
            }
        }
        return false;

    }

    private Date getStudentCreatedDate(CourseStudent student) {
        if (student.getGoogleId() != null && !student.getGoogleId().isEmpty()) {
            Account account = getAccountEntity(student.getGoogleId());
            if (account != null) {
                return account.getCreatedAt();
            }
        }

        if (courseIdToCreatedDateMap.get(student.getCourseId()) != null) {
            return courseIdToCreatedDateMap.get(student.getCourseId());
        }

        Course course = getCourseEntity(student.getCourseId());

        if (course != null) {
            courseIdToCreatedDateMap.put(student.getCourseId(), course.getCreatedAt());
            return course.getCreatedAt();
        }

        return null;

    }

    private Course getCourseEntity(String courseId) {

        Query q = PM.newQuery(Course.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("ID == courseIdParam");

        @SuppressWarnings("unchecked")
        List<Course> courseList = (List<Course>) q.execute(courseId);

        if (courseList.isEmpty() || JDOHelper.isDeleted(courseList.get(0))) {
            return null;
        }

        return courseList.get(0);
    }

    private Account getAccountEntity(String googleId) {

        try {
            Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
            Account account = PM.getObjectById(Account.class, key);

            if (JDOHelper.isDeleted(account)) {
                return null;
            }

            return account;

        } catch (IllegalArgumentException | JDOObjectNotFoundException e) {
            return null;
        }
    }

    private String getCurrentDateForDisplay() {
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(now);
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        System.out.print(formatTime(cal.getTime()) + "\n");
        return formatTime(cal.getTime());

    }

    private String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("[HH-mm]dd-MMM-yyyy").format(date);

    }

    private boolean isValidDate(int day, int month, int year) {

        boolean isDateValid = false;

        if (day <= 0 || month <= 0 || year <= 0) {
            isDateValid = false;
        } else if (day > getMaxNumOfDayForMonth(month, year)) {
            isDateValid = false;
        } else {
            isDateValid = true;
        }

        if (isDateValid) {
            System.out.print("Date Entered is valid.\n\n");
        } else {
            System.out.print("Date is not valid. Please Re-enter date.\n\n");
        }

        return isDateValid;

    }

    private int getMaxNumOfDayForMonth(int month, int year) {

        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    private Date getDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, day, 0, 0, 0);

        return cal.getTime();

    }

    private void updateProgressIndicator() {
        iterationCounter++;
        if (iterationCounter % 1000 == 0) {
            System.out.print("------------------ iterations count:" + iterationCounter + " ------------------------\n");
        }
    }

    private static class EmailListConfig {

        public boolean student;
        public boolean instructor;
        public StudentStatus studentStatus = StudentStatus.ALL;
        public InstructorStatus instructorStatus = InstructorStatus.ALL;
        public Date studentCreatedDateRangeStart;
        public Date studentCreatedDateRangeEnd;
        public Date instructorCreatedDateRangeStart;
        public Date instructorCreatedDateRangeEnd;
    }
}
