package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.storage.entity.UsageStatistics;

/**
 * Handles CRUD operations for system usage statistics objects.
 *
 * @see UsageStatistics
 * @see UsageStatisticsAttributes
 */
public final class UsageStatisticsDb extends EntitiesDb<UsageStatistics, UsageStatisticsAttributes> {

    private static final UsageStatisticsDb instance = new UsageStatisticsDb();

    private UsageStatisticsDb() {
        // prevent initialization
    }

    public static UsageStatisticsDb inst() {
        return instance;
    }

    @Override
    LoadType<UsageStatistics> load() {
        return ofy().load().type(UsageStatistics.class);
    }

    @Override
    boolean hasExistingEntities(UsageStatisticsAttributes entityToCreate) {
        Key<UsageStatistics> keyToFind = Key.create(UsageStatistics.class, entityToCreate.getStartTime().toEpochMilli());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    UsageStatisticsAttributes makeAttributes(UsageStatistics entity) {
        assert entity != null;

        return UsageStatisticsAttributes.valueOf(entity);
    }

}
