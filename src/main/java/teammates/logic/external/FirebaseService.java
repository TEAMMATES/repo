package teammates.logic.external;

import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import teammates.common.exception.AuthException;
import teammates.common.util.Logger;

/**
 * Provides Firebase Admin SDK authentication services.
 * <p>The FirebaseApp instance is initialized here.</p>
 * @see <a href="https://firebase.google.com/docs/reference/admin">Firebase Admin SDK</a>
 */
public class FirebaseService implements AuthService {

    private static final Logger log = Logger.getLogger();

    public FirebaseService() throws AuthException {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Initialized FirebaseApp instance of name " + FirebaseApp.getInstance().getName());
        } catch (IOException | IllegalStateException e) {
            log.severe("Cannot initialize FirebaseApp: " + e.getMessage());
            throw new AuthException(e);
        }
    }

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(continueUrl)
                .setHandleCodeInApp(true)
                .build();
        try {
            return FirebaseAuth.getInstance().generateSignInWithEmailLink(userEmail, actionCodeSettings);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            return null;
        }
    }

    @Override
    public void deleteUser(String userEmail) throws AuthException {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userEmail);
            FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
        } catch (IllegalArgumentException e) {
            throw new AuthException(e);
        } catch (FirebaseAuthException e) {
            throw new AuthException(e, e.getAuthErrorCode().toString());
        }
    }

}
