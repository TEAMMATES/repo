package teammates.ui.servlets;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Logger;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));

        String idToken = req.getParameter("idToken");
        if (idToken == null) {
            return;
        }
        String googleId = null;
        try {
            googleId = FirebaseAuth.getInstance().verifyIdToken(idToken).getEmail();
            // TODO delete the user immediately as we do not need to keep user info
        } catch (FirebaseAuthException e) {
            log.warning("Invalid user ID token", e);
        }
        Cookie cookie;
        if (googleId == null) {
            // invalid google ID
            req.getSession().invalidate();
            cookie = getLoginInvalidationCookie();
        } else {
            UserInfoCookie uic = new UserInfoCookie(googleId);
            cookie = getLoginCookie(uic);
        }

        log.info("Going to redirect to: " + nextUrl);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Login successful");

        resp.addCookie(cookie);
        resp.sendRedirect(nextUrl);
    }

}
