package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;
// CHECKSTYLE.ON:ImportOrder

/**
 * Verify the counts of non-course entities are correct.
 */
@SuppressWarnings("PMD")
public class VerifyCourseEntityCounts extends DatastoreClient {
    private VerifyCourseEntityCounts() {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) throws Exception {
        new VerifyCourseEntityCounts().doOperationRemotely();
    }

    private void printEntityVerification(String className, int datastoreCount, long psqlCount) {
        System.out.println("========================================");
        System.out.println(className);
        System.out.println("Objectify count: " + datastoreCount);
        System.out.println("Postgres count: " + psqlCount);
        System.out.println("Correct number of rows?: " + (datastoreCount == psqlCount));
    }

    private Long countPostgresEntities(Class<? extends teammates.storage.sqlentity.BaseEntity> entity) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<? extends teammates.storage.sqlentity.BaseEntity> root = cr.from(entity);

        cr.select(cb.count(root));

        Long count = HibernateUtil.createQuery(cr).getSingleResult();
        HibernateUtil.commitTransaction();
        return count;
    }

    @Override
    protected void doOperation() {
        verifyCurrentEntities();
        verifyNewEntities();
    }

    private void verifyCurrentEntities() {
        Map<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entities =
                new HashMap<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>>();

        entities.put(teammates.storage.entity.Course.class, teammates.storage.sqlentity.Course.class);
        entities.put(teammates.storage.entity.FeedbackSession.class, teammates.storage.sqlentity.FeedbackSession.class);

        // Compare datastore "table" to postgres table for each entity
        for (Map.Entry<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entry : entities
                .entrySet()) {
            Class<? extends BaseEntity> objectifyClass = entry.getKey();
            Class<? extends teammates.storage.sqlentity.BaseEntity> sqlClass = entry.getValue();

            int objectifyEntityCount = ofy().load().type(objectifyClass).count();
            Long postgresEntityCount = countPostgresEntities(sqlClass);

            printEntityVerification(objectifyClass.getSimpleName(), objectifyEntityCount, postgresEntityCount);
        }
    }

    private void verifyNewEntities() {
        List<CourseStudent> students = ofy().load().type(CourseStudent.class).order("courseId").list();
        verifySectionEntities(students);
        verifyTeamEntities(students);
    }

    private void verifySectionEntities(List<CourseStudent> students) {
        int objectifyEntityCount = (int) students.stream().map(stu -> stu.getSectionName() + stu.getCourseId())
                .distinct().count();
        Long postgresEntityCount = countPostgresEntities(Section.class);

        printEntityVerification("Section", objectifyEntityCount, postgresEntityCount);
    }

    private void verifyTeamEntities(List<CourseStudent> students) {
        int objectifyEntityCount = (int) students.stream().map(stu -> stu.getTeamName() + stu.getCourseId())
                .distinct().count();
        Long postgresEntityCount = countPostgresEntities(Team.class);

        printEntityVerification("Team", objectifyEntityCount, postgresEntityCount);
    }
}
