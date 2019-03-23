package teammates.performance.scripts.setup;

import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.performance.scripts.LoadTestDataInDatastore;
import teammates.performance.scripts.create.config.CreateStudentProfileTestConfigData;
import teammates.performance.scripts.create.config.CreateTestConfigData;
import teammates.performance.scripts.create.data.CreateStudentProfileTestData;
import teammates.performance.scripts.create.data.CreateTestData;

/**
 *  Sets up the Student Profile performance test by generating the relevant data and creating entities in the datastore.
 */
public final class SetupStudentProfileTest {

    protected static final Logger log = Logger.getLogger();

    public static void main(String[] args) {
        CreateTestData dataCreator = new CreateStudentProfileTestData();
        CreateTestConfigData configDataCreator = new CreateStudentProfileTestConfigData();

        JSONObject jsonData = dataCreator.createJsonData();

        try {
            dataCreator.writeJsonDataToFile(jsonData, dataCreator.getPathToOutputJson());
            configDataCreator.createConfigDataCsvFile();
        } catch (IOException | ParseException ex) {
            log.severe(TeammatesException.toStringWithStackTrace(ex));
        }

        LoadTestDataInDatastore.addToDatastore(dataCreator.getPathToOutputJson());
    }

}
