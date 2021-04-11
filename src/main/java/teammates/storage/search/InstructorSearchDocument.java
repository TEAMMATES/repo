package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The {@link SearchDocument} object that defines how we store document for instructors.
 */
class InstructorSearchDocument extends SearchDocument<InstructorAttributes> {

    InstructorSearchDocument(InstructorAttributes instructor) {
        super(instructor);
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        InstructorAttributes instructor = attribute;

        CourseAttributes course = coursesDb.getCourse(instructor.courseId);

        fields.put("id", instructor.getEmail() + "%" + instructor.getCourseId());
        fields.put("name", instructor.getName());
        fields.put("email", instructor.getEmail());
        fields.put("courseId", instructor.getCourseId());
        fields.put("courseName", course == null ? "" : course.getName());
        fields.put("googleId", instructor.getGoogleId());
        fields.put("role", instructor.getRole());
        fields.put("displayedName", instructor.getDisplayedName());

        return fields;
    }

}
