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
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.joda.time.DateTime;

/**
 * Generates txt file which contains a list of receiver emails.<br>
 * List file configuration is preset in admin email configuration section.  
 */
public class AdminEmailListGenerator extends RemoteApiClient {
    
    private static enum StudentStatus { REG, UNREG, ALL }
    
    private static enum InstructorStatus { REG, UNREG, ALL }
    
    private static final PersistenceManager pm = JDOHelper
                                                   .getPersistenceManagerFactory("transactions-optional")
                                                   .getPersistenceManager();
    
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
                System.out.print("ALL\n");
                break;
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
                System.out.print("ALL\n");
                break;
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
        List<?> allInstructors = (List<?>) pm.newQuery(q).execute();
        
        for (Object object : allInstructors) {
            Instructor instructor = (Instructor) object;
            // intended casting of ? to remove unchecked casting
            if ((instructor.getGoogleId() != null && emailListConfig.instructorStatus == InstructorStatus.REG 
                     || instructor.getGoogleId() == null && emailListConfig.instructorStatus == InstructorStatus.UNREG 
                     || emailListConfig.instructorStatus == InstructorStatus.ALL)
                 && isInstructorCreatedInRange(instructor)) {
                instructorEmailSet.add(instructor.getEmail());
            }
            updateProgressIndicator();
        }
        
        return instructorEmailSet;
    }
    
    private  HashSet<String> addStudentEmailIntoSet(HashSet<String> studentEmailSet) {
        String q = "SELECT FROM " + Student.class.getName();
        List<?> allStudents = (List<?>) pm.newQuery(q).execute();

        for (Object object : allStudents) {
            Student student = (Student) object;
            // intended casting from ? due to unchecked casting
            if ((student.isRegistered() && emailListConfig.studentStatus == StudentStatus.REG 
                     || !student.isRegistered() && emailListConfig.studentStatus == StudentStatus.UNREG 
                     || emailListConfig.studentStatus == StudentStatus.ALL)
                 && isStudentCreatedInRange(student)) {
                studentEmailSet.add(student.getEmail());
            }
            updateProgressIndicator();
        } 
        return studentEmailSet;
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

        if (instructorCreatedAt == null) {
            return false;
        }
        
        if (emailListConfig.instructorCreatedDateRangeEnd == null 
            && emailListConfig.instructorCreatedDateRangeStart == null) {
            //no range set
            return true;
        } else if (emailListConfig.instructorCreatedDateRangeStart != null 
                   && emailListConfig.instructorCreatedDateRangeEnd == null) {
            //after a specific date
            return instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart);
        } else if (emailListConfig.instructorCreatedDateRangeStart == null 
                   && emailListConfig.instructorCreatedDateRangeEnd != null) {
            //before a specific date
            return instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd);
            
        } else if (emailListConfig.instructorCreatedDateRangeStart != null 
                   && emailListConfig.instructorCreatedDateRangeEnd != null) {
            //within a date interval   
            return instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart) 
                && instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd);
        }
        
        return false;
        
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

    private boolean isStudentCreatedInRange(Student student) {
        
        Date studentCreatedAt = getStudentCreatedDate(student);

        if (studentCreatedAt == null) {
            return false;
        }
        
        if (emailListConfig.studentCreatedDateRangeEnd == null 
            && emailListConfig.studentCreatedDateRangeStart == null) {
            //no range set
            return true;
        } else if (emailListConfig.studentCreatedDateRangeStart != null 
                   && emailListConfig.studentCreatedDateRangeEnd == null) {
            //after a specific date
            return studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart);
            
        } else if (emailListConfig.studentCreatedDateRangeStart == null 
                   && emailListConfig.studentCreatedDateRangeEnd != null) {
            //before a specific date
            return studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd);
        } else if (emailListConfig.studentCreatedDateRangeStart != null 
                   && emailListConfig.studentCreatedDateRangeEnd != null) {
            //within a date interval   
            return studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart) 
                && studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd);
        }
        
        return false;
        
    }
    
    private Date getStudentCreatedDate(Student student) {
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
        
        Query q = pm.newQuery(Course.class);
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
            Account account = pm.getObjectById(Account.class, key);
            
            if (JDOHelper.isDeleted(account)) {
                return null;
            } 
            
            return account;
            
        } catch (IllegalArgumentException iae) {
            return null;            
        } catch (JDOObjectNotFoundException je) {
            return null;
        }
    }

    private String getCurrentDateForDisplay() {
        Date now = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(now);
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
        
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
            System.out.print("------------------  iterations count:" + iterationCounter + "  ------------------------\n");
        }
    }
    
    class EmailListConfig {
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
