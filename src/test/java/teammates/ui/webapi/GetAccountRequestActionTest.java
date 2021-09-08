package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;

/**
 * SUT: {@link GetAccountRequestAction}.
 */
public class GetAccountRequestActionTest extends BaseActionTest<GetAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountRequestAttributes accountRequest = typicalBundle.accountRequests.get("accountRequest1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("account request does not exist");

        String[] nonExistParams = {
                Const.ParamsNames.EMAIL, "non-existent@email",
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, "non existent institute",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(nonExistParams);
        assertEquals("Account request does not exist.", enfe.getMessage());

        ______TS("typical success case");

        String[] params = new String[] {
                Const.ParamsNames.EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };

        GetAccountRequestAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        AccountRequestData response = (AccountRequestData) r.getOutput();

        assertEquals(response.getName(), accountRequest.getName());
        assertEquals(response.getEmail(), accountRequest.getEmail());
        assertEquals(response.getRegistrationKey(), accountRequest.getRegistrationKey());
        assertEquals(response.getInstitute(), accountRequest.getInstitute());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
