package teammates.performance.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import teammates.common.util.Url;

/**
 * Represents properties in test.properties file.
 */
public final class TestProperties {

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/jmeter/resources/data";

    /** The value of "test.app.url" in test.properties file. */
    public static final String TEAMMATES_URL;

    /** The value of "test.csrf.key" in test.properties file. */
    public static final String CSRF_KEY;

    /** The value of "test.backdoor.key" in test.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of "test.jmeter.home" in test.properties file. */
    public static final String JMETER_HOME;

    /** The value of "test.jmeter.properties" in test.properties file. */
    public static final String JMETER_PROPERTIES_PATH;

    private TestProperties() {
        // access static fields directly
    }

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/jmeter/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            Properties buildProperties = new Properties();
            try (InputStream buildPropStream = Files.newInputStream(Paths.get("src/main/resources/build.properties"))) {
                buildProperties.load(buildPropStream);
            }

            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties", "").toLowerCase();

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isDevServer() {
        return TEAMMATES_URL.matches("^https?://localhost:[0-9]+(/.*)?");
    }

    /**
     * Creates the test data folder if it does not exist.
     */
    public static boolean createTestDataFolder() {
        File testDataDirectory = new File(TestProperties.TEST_DATA_FOLDER);
        if (!testDataDirectory.exists()) {
            return testDataDirectory.mkdir();
        }
        return true;
    }

}
