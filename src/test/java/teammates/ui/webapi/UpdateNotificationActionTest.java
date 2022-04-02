package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationUpdateRequest;

/**
 * SUT: {@link UpdateNotificationAction}.
 */
public class UpdateNotificationActionTest extends BaseActionTest<UpdateNotificationAction> {
    private static final String TEST_NOTIFICATION = "notification1";
    private final NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get(TEST_NOTIFICATION);

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void testExecute() throws Exception {
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, testNotificationAttribute.getNotificationId(),
        };
        NotificationUpdateRequest req = getTypicalUpdateRequest();
        long startTime = req.getStartTimestamp();
        long endTime = req.getEndTimestamp();
        NotificationStyle style = req.getStyle();
        NotificationTargetUser targetUser = req.getTargetUser();
        String title = req.getTitle();
        String message = req.getMessage();
        String invalidTitle = "";
        String invalidNotificationId = "InvalidNotificationId";

        loginAsAdmin();

        ______TS("Typical Case: Update notification successfully");
        req = getTypicalUpdateRequest();
        UpdateNotificationAction action = getAction(req, requestParams);
        NotificationData res = (NotificationData) action.execute().getOutput();

        NotificationAttributes updatedNotification = logic.getNotification(res.getNotificationId());

        assertEquals(startTime, updatedNotification.getStartTime().toEpochMilli());
        assertEquals(endTime, updatedNotification.getEndTime().toEpochMilli());
        assertEquals(style, updatedNotification.getStyle());
        assertEquals(targetUser, updatedNotification.getTargetUser());
        assertEquals(title, updatedNotification.getTitle());
        assertEquals(message, updatedNotification.getTitle());

        ______TS("Parameters cannot be null");
        req = getTypicalUpdateRequest();
        req.setStyle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification style cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setTargetUser(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification target user cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setTitle(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification title cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setMessage(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification message cannot be null", ex.getMessage());

        ______TS("Timestamps should be greater than 0");
        req = getTypicalUpdateRequest();
        req.setStartTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setEndTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("End timestamp should be greater than zero", ex.getMessage());

        ______TS("Invalid parameter should throw an error");
        req = getTypicalUpdateRequest();
        req.setTitle(invalidTitle);
        verifyHttpParameterFailure(req, requestParams);

        ______TS("Non-existent notification should throw an error");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, invalidNotificationId,
        };
        req = getTypicalUpdateRequest();
        verifyHttpParameterFailure(req, requestParams);
    }

    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    private NotificationUpdateRequest getTypicalUpdateRequest() {
        NotificationUpdateRequest req = new NotificationUpdateRequest();

        req.setStartTimestamp(Instant.now().toEpochMilli());
        req.setEndTimestamp(Instant.now().plus(5, ChronoUnit.MONTHS).toEpochMilli());
        req.setStyle(NotificationStyle.INFO);
        req.setTargetUser(NotificationTargetUser.GENERAL);
        req.setTitle("New notification title");
        req.setMessage("New notification message");

        return req;
    }
}
