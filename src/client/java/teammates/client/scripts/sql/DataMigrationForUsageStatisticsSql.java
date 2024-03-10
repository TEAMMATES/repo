package teammates.client.scripts.sql;

import java.util.UUID;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.sqlentity.UsageStatistics;

/**
 * Data migration class for usage statistics.
 */
public class DataMigrationForUsageStatisticsSql extends
        DataMigrationEntitiesBaseScriptSql<
            teammates.storage.entity.UsageStatistics,
            UsageStatistics> {

    public static void main(String[] args) {
        new DataMigrationForUsageStatisticsSql().doOperationRemotely();
    }

    @Override
    protected Query<teammates.storage.entity.UsageStatistics> getFilterQuery() {
        // returns all UsageStatistics entities
        return ofy().load().type(teammates.storage.entity.UsageStatistics.class);
    }

    /**
     * Set to true to preview the migration without actually performing it.
     */
    @Override
    protected boolean isPreview() {
        return false;
    }

    /**
     * Always returns true, as the migration is needed for all entities from Datastore to CloudSQL .
     */
    @Override
    protected boolean isMigrationNeeded(teammates.storage.entity.UsageStatistics entity) {
        return true;
    }

    @Override
    protected void migrateEntity(teammates.storage.entity.UsageStatistics oldEntity) throws Exception {
        UsageStatistics newEntity = new UsageStatistics(
                oldEntity.getStartTime(),
                oldEntity.getTimePeriod(),
                oldEntity.getNumResponses(),
                oldEntity.getNumCourses(),
                oldEntity.getNumStudents(),
                oldEntity.getNumInstructors(),
                oldEntity.getNumAccountRequests(),
                oldEntity.getNumEmails(),
                oldEntity.getNumSubmissions()
        );

        try {
            UUID oldUuid = UUID.fromString(oldEntity.getId());
            newEntity.setId(oldUuid);
        } catch (IllegalArgumentException iae) {
            // Auto-generated UUID from entity is created in newEntity constructor.
            // Do nothing.
        }
        saveEntityDeferred(newEntity);
    }
}
