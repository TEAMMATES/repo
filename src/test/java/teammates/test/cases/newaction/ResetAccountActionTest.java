package teammates.test.cases.newaction;

import org.apache.http.client.methods.HttpPut;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.ResetAccountAction;

/**
 * SUT: {@link ResetAccountAction}.
 */
public class ResetAccountActionTest extends BaseActionTest<ResetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS_DOWNGRADE;
    }

    @Override
    protected String getRequestMethod() {
        return HttpPut.METHOD_NAME;
    }

    @Override
    @Test
    protected void testExecute() {
        // TODO
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
