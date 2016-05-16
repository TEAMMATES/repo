package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.logic.api.Logic;
import teammates.storage.api.CoursesDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;

/**
 * Script to set the isArchived attribute of instructors if the course's isArchived 
 * attribute is set.
 * 
 * If the course is not archived, the instructors of the course will not be modified
 * 
 * Assumptions: the default value of an instructor without an isArchived attribute is null, and not false
 * 
 */
public class DataMigrationForIsArchivedAttribute extends RemoteApiClient {

    private Logic logic = new Logic();
    private CoursesDb coursesDb = new CoursesDb();
    private final boolean isPreview = true;
    private final boolean isModifyingOnlyArchivedCourses = true;
    
    public static void main(String[] args) throws IOException {
        DataMigrationForIsArchivedAttribute migrator = new DataMigrationForIsArchivedAttribute();
        migrator.doOperationRemotely();
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        List<CourseAttributes> courses = isModifyingOnlyArchivedCourses ? getArchivedCourses() 
                                                                        : getAllCourses();
        
        try {
            for (CourseAttributes course : courses) {
                migrateCourse(course);
            }
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            e.printStackTrace();
        }
    }

    
    private void migrateCourse(CourseAttributes course) throws InvalidParametersException,
                                    EntityDoesNotExistException {
        if (course.isArchived) {
            if (isPreview) {
                previewInstructorsIsArchivedInCourse(course);
            } else {
                setInstructorsIsArchivedInCourse(course);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private List<CourseAttributes> getAllCourses() {
        return coursesDb.getAllCourses();
    }
    
    private List<CourseAttributes> getArchivedCourses() {
        Query query = Datastore.getPersistenceManager().newQuery(Course.class);
        query.declareParameters("Boolean archiveStatusParam");
        query.setFilter("archiveStatus == archiveStatusParam");

        @SuppressWarnings("unchecked")
        List<Course> courseList = (List<Course>) Datastore.getPersistenceManager()
                .newQuery(query).execute(true);
        List<CourseAttributes> courseAttributesList = new ArrayList<CourseAttributes>();
        for (Course c : courseList) {
            courseAttributesList.add(new CourseAttributes(c));
        }
        
        return courseAttributesList;
    }

    /**
     * For the given course, set the isArchived attribute of the instructors in the course 
     * @throws EntityDoesNotExistException 
     * @throws InvalidParametersException
     */
    private void setInstructorsIsArchivedInCourse(CourseAttributes course) throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertEquals(false, isPreview);
        Assumption.assertTrue(course.isArchived);
        
        System.out.println("Updating instructors of old archived course: " + course.id);
        
        List<InstructorAttributes> instructorsOfCourse = logic.getInstructorsForCourse(course.id);
        for (InstructorAttributes instructor: instructorsOfCourse) {
            
            // only update if migration had not been done for the instructor
            if (instructor.isArchived == null) {
                instructor.isArchived = true;
                logic.updateInstructorByEmail(instructor.email, instructor);    
                
                System.out.println("Successfully updated instructor: [" + instructor.email + "] " + instructor.name);
            }
        
        }
        
        System.out.println("");
        
    }
    
    /**
     * For preview mode, prints out the instructors of the course and their isArchived status 
     * @throws EntityDoesNotExistException 
     * @throws InvalidParametersException
     */
    private void previewInstructorsIsArchivedInCourse(CourseAttributes course) throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertEquals(true, isPreview);
        Assumption.assertTrue(course.isArchived);
        
        System.out.println("Previewing instructors of old archived course: " + course.id);
        
        List<InstructorAttributes> instructorsOfCourse = logic.getInstructorsForCourse(course.id);
        for (InstructorAttributes instructor: instructorsOfCourse) {
            System.out.println("Instructor: " + instructor.googleId + " : " + instructor.isArchived);
            
            if (instructor.isArchived == null) {
                System.out.println("======= Migration has not been done yet =======");
            }
        }
        
        System.out.println("");
    }

}
