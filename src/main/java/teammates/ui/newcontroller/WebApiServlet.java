package teammates.ui.newcontroller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;

/**
 * Servlet that handles all requests from the web application.
 */
@SuppressWarnings("serial")
public class WebApiServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void init() {
        TimeHelper.registerResourceZoneRules();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invokeServlet(req, resp);
    }

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // used as fallback
    private void invokeServlet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Strict-Transport-Security", "max-age=31536000");

        log.info("Request received: [" + req.getMethod() + "] " + req.getRequestURL().toString()
                + ", Params: " + HttpRequestHelper.getRequestParametersAsString(req)
                + ", Headers: " + HttpRequestHelper.getRequestHeadersAsString(req)
                + ", Request ID: " + Config.getRequestId());

        Action action;
        try {
            action = new ActionFactory().getAction(req, req.getMethod(), resp);
        } catch (ActionMappingException e) {
            throwError(resp, e.getStatusCode(), e.getMessage());
            return;
        }

        try {
            action.checkAccessControl();

            ActionResult result = action.execute();
            result.send(resp);
        } catch (InvalidHttpParameterException ihpe) {
            throwError(resp, HttpStatus.SC_BAD_REQUEST, ihpe.getMessage());
        } catch (UnauthorizedAccessException uae) {
            throwError(resp, HttpStatus.SC_FORBIDDEN, uae.getMessage());
        } catch (EntityNotFoundException enfe) {
            throwError(resp, HttpStatus.SC_NOT_FOUND, enfe.getMessage());
        } catch (DeadlineExceededException | DatastoreTimeoutException e) {

            // This exception may not be caught because GAE kills the request soon after throwing it
            // In that case, the error message in the log will be emailed to the admin by a separate cron job

            log.severe(e.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(e));
            throwError(resp, HttpStatus.SC_GATEWAY_TIMEOUT, e.getMessage());

        } catch (Throwable t) {
            log.severe(t.getClass().getSimpleName() + " caught by WebApiServlet: "
                    + TeammatesException.toStringWithStackTrace(t));
            throwError(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    private void throwError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        JsonResult result = new JsonResult(message, statusCode);
        result.send(resp);
    }

}
