package teammates.storage.search;

import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.DataBundle;
import teammates.test.BaseTestCaseWithObjectifyAccess;

/**
 * Base class for all search tests.
 */
public abstract class BaseSearchTest extends BaseTestCaseWithObjectifyAccess {

    protected DataBundle dataBundle;

    @BeforeMethod
    public void baseClassSetup() {
        prepareTestData();
    }

    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
        putDocuments(dataBundle);
    }

}
