package teammates.client.scripts;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.CourseStudent;




/**
 * Script check if any teamname .
 *
 * <p>See issue #8830</p>
 */
public class ValidateAllTeamNameScript extends DataMigrationEntitiesBaseScript<CourseStudent> {

    private final Pattern emailRegex =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws IOException {
        new ValidateAllTeamNameScript().doOperationRemotely();
    }

    private boolean validate(String emailStr) {
        Matcher matcher = emailRegex.matcher(emailStr);
        return matcher.find();
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected String getLastPositionOfCursor() {
        return null;
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        return 100;
    }

    @Override
    protected boolean isMigrationNeeded(Key<CourseStudent> key) {
        CourseStudent student = ofy().load().key(key).now();
        String teamName = student.getTeamName();
        return teamName.equals(student.getEmail()) || validate(teamName);
    }

    @Override
    protected void migrateEntity(Key<CourseStudent> entity) {
        // no actual data migrations needed.
    }
}
