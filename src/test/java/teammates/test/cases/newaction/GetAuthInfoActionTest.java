package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.ui.newcontroller.GetAuthInfoAction;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.webapi.output.AuthInfo;

/**
 * SUT: {@link GetAuthInfoAction}.
 */
public class GetAuthInfoActionTest extends BaseActionTest<GetAuthInfoAction> {

    private GateKeeper gateKeeper = new GateKeeper();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {

        ______TS("Normal case: No logged in user");

        gaeSimulation.logoutUser();

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        AuthInfo output = (AuthInfo) r.getOutput();
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.STUDENT_HOME_PAGE), output.getStudentLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE), output.getInstructorLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.ADMIN_HOME_PAGE), output.getAdminLoginUrl());
        assertNull(output.getUser());
        assertFalse(output.isMasquerade());
        assertNull(output.getLogoutUrl());

        ______TS("Normal case: With logged in user");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction();
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertFalse(output.isMasquerade());
        assertEquals(gateKeeper.getLogoutUrl("/web"), output.getLogoutUrl());

        UserInfo user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        ______TS("Normal case: Admin masquerading as user");

        loginAsAdmin();

        a = getAction(new String[] {
                Const.ParamsNames.USER_ID, "idOfInstructor1OfCourse1",
        });
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertTrue(output.isMasquerade());
        assertEquals(gateKeeper.getLogoutUrl("/web"), output.getLogoutUrl());

        user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        ______TS("Failure case: Non-admin cannot masquerade");

        loginAsInstructor("idOfInstructor1OfCourse1");

        assertThrows(UnauthorizedAccessException.class, () -> getAction(new String[] {
                Const.ParamsNames.USER_ID, "idOfAnotherInstructor",
        }));

        // TODO test CSRF token cookies

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
