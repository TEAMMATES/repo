package teammates.it.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.CreateAccountRequestAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        // This is separated into different test methods.
    }

    @Test
    void testExecute_nullEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("email cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("name cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("institute cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_typicalCase_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertEquals("My road leads into the desert. I can see it.", output.getComments());
        assertNull(output.getRegisteredAt());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNoEmailsSent();
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_leadingAndTrailingSpacesAndNullComments_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail(" kwisatz.haderach@atreides.org   ");
        request.setInstructorName("  Paul Atreides ");
        request.setInstructorInstitution("   House Atreides  ");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertNull(output.getComments());
        assertNull(output.getRegisteredAt());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertNull(accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNoEmailsSent();
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_accountRequestWithSameEmailAddressAndInstituteAlreadyExists_createsSuccessfully()
            throws InvalidParametersException {
        AccountRequest existingAccountRequest = logic.createAccountRequest("Paul Atreides", "kwisatz.haderach@atreides.org",
                "House Atreides", AccountRequestStatus.PENDING, "My road leads into the desert. I can see it.");
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertEquals("My road leads into the desert. I can see it.", output.getComments());
        assertNull(output.getRegisteredAt());
        assertNotEquals(output.getRegistrationKey(), existingAccountRequest.getRegistrationKey());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNoEmailsSent();
        verifyNoTasksAdded();
    }

    @Override
    protected void testAccessControl() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAccessControl'");
    }

}
