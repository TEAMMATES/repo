package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.RestoreFeedbackSessionAction;

/**
 * SUT: {@link RestoreFeedbackSessionAction}.
 */
public class RestoreFeedbackSessionActionTest extends BaseActionTest<RestoreFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }

}
