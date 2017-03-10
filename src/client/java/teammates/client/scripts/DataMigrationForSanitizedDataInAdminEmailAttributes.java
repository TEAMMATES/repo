package teammates.client.scripts;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.storage.api.AdminEmailsDb;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Text;

public class DataMigrationForSanitizedDataInAdminEmailAttributes extends RemoteApiClient {
    private static final boolean isPreview = true;
    private AdminEmailsDb adminEmailsDb = new AdminEmailsDb();
    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInAdminEmailAttributes migrator =
                new DataMigrationForSanitizedDataInAdminEmailAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<AdminEmailAttributes> allEmails = adminEmailsLogic.getAllAdminEmails();
        int count = 0;
        int numberOfAffectedEmails = 0;
        String itemName = "admin email";

        if (isPreview) {
            System.out.println("Checking sanitization for admin emails...");
        }
        for (AdminEmailAttributes email : allEmails) {
            count++;
            DataMigrationHelper.printCountRegularly(count, itemName);
            if (isPreview) {
                if (isEmailSanitized(email)) {
                    previewAdminEmail(email);
                    numberOfAffectedEmails++;
                }
            } else {
                fixSanitizedDataForEmail(email);
            }
        }
        if (isPreview) {
            System.out.println("There are/is " + numberOfAffectedEmails + " affected email(s)!");
        } else {
            System.out.println("Sanitization fixing done!");
        }
    }

    /**
     * Prints out the subject, current contents and desanitized contents of the email
     */
    private void previewAdminEmail(AdminEmailAttributes email) {
        System.out.println("Previewing email having subject: " + email.getSubject());
        String content = email.getContentValue();
        System.out.println("contents:\n" + content);
        System.out.println("new contents:\n" + SanitizationHelper.desanitizeFromHtml(content));

        System.out.println();
    }

    private boolean isEmailSanitized(AdminEmailAttributes email) {
        return DataMigrationHelper.isSanitizedHtml(email.getContentValue());
    }

    /**
     * Checks if email data is sanitized
     * and desanitizes and updates the email in the database if it has sanitized data.
     * If there is no sanitized data, the method does nothing.
     */
    private void fixSanitizedDataForEmail(AdminEmailAttributes email) {
        if (isEmailSanitized(email)) {
            try {
                email.content =
                        new Text(SanitizationHelper.desanitizeFromHtml(email.getContentValue()));
                adminEmailsDb.updateAdminEmail(email);
            } catch (InvalidParametersException e) {
                System.out.println("Email " + email.getSubject() + " invalid!");
                e.printStackTrace();
            } catch (EntityDoesNotExistException e) {
                System.out.println("Email " + email.getSubject() + " does not exist!");
                e.printStackTrace();
            }
        }
    }
}
