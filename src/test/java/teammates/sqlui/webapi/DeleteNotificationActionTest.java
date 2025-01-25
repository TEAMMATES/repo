package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteNotificationAction;

import java.time.Instant;
import java.util.UUID;

public class DeleteNotificationActionTest extends BaseActionTest<DeleteNotificationAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testExecute_notificationExists_success() {
        Notification testNotification = new Notification(
                Instant.now(),
                Instant.ofEpochMilli(Instant.now().toEpochMilli() + 10000),
                NotificationStyle.INFO,
                NotificationTargetUser.GENERAL,
                "title",
                "message");

        when(mockLogic.getNotification(testNotification.getId())).thenReturn(testNotification);

        String[] params = {
                Const.ParamsNames.NOTIFICATION_ID, testNotification.getId().toString(),
        };

        DeleteNotificationAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Notification has been deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_notificationDoesNotExists_failSilently() {
        UUID invalidUuid = UUID.randomUUID();
        when(mockLogic.getNotification(invalidUuid)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.NOTIFICATION_ID, invalidUuid.toString(),
        };

        DeleteNotificationAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Notification has been deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingNotificationUuid_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.NOTIFICATION_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        String[] params = {};

        verifyHttpParameterFailure(params);
    }
}
