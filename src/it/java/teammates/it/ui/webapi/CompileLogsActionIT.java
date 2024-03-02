package teammates.it.ui.webapi;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.ui.webapi.CompileLogsAction;

/**
 * SUT: {@link CompileLogsAction}.
 */
public class CompileLogsActionIT extends BaseActionIT<CompileLogsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_LOG_COMPILATION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        ______TS("No email should be sent if there are no logs");

        CompileLogsAction action = getAction();
        action.execute();

        verifyNoEmailsSent();

        ______TS("No email should be sent if there are no recent error logs");

        SourceLocation sourceLocation = new SourceLocation("file5", 5L, "func5");
        long timestampTooDistant = Instant.now().minusSeconds(7 * 60).toEpochMilli();
        long correctTimestamp = Instant.now().minusSeconds(30).toEpochMilli();
        mockLogsProcessor.insertErrorLog("errorlogtrace1", "errorloginsertid1", sourceLocation,
                timestampTooDistant, "Error message 1", null);
        mockLogsProcessor.insertWarningLog("warninglogtrace1", "warningloginsertid1", sourceLocation,
                correctTimestamp, "Warning message 1", null);

        action = getAction();
        action.execute();

        verifyNoEmailsSent();

        ______TS("Email should be sent if there are recent error logs");

        mockLogsProcessor.insertErrorLog("errorlogtrace1", "errorloginsertid1", sourceLocation,
                correctTimestamp, "Error message 1", null);

        action = getAction();
        action.execute();

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION),
                emailSent.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

}
