package teammates.common.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import teammates.common.exception.TeammatesException;

/**
 * Used to handle the verification of the user's reCAPTCHA response.
 *
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">reCAPTCHA user response verification API</a>
 */
public class RecaptchaVerifier {

    /** The Google reCAPTCHA API URL to verify the response token. */
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    /** The shared secret key between the TEAMMATES site and reCAPTCHA. */
    private static final String SECRET_KEY = Config.CAPTCHA_SECRET_KEY;

    private static final Logger log = Logger.getLogger();

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully.
     * @param captchaResponse The user's captcha response from the client side
     * @return true if the {@code captchaResponse} is verified successfully
     */
    public boolean isVerificationSuccessful(String captchaResponse) {
        return isVerificationSuccessful(captchaResponse, SECRET_KEY);
    }

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully or {@code secretKey} is null.
     * @param captchaResponse The user's captcha response from the client side
     * @param secretKey The secret CAPTCHA site key
     * @return true if the {@code captchaResponse} is verified successfully or {@code secretKey} is null, and false if a
     *         exception occurs or if the API request fails
     */
    public boolean isVerificationSuccessful(String captchaResponse, String secretKey) {
        if (secretKey == null) {
            return true;
        } else if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }

        try {
            String response = getApiResponse(captchaResponse, secretKey);
            JsonObject responseInJson = JsonUtils.parse(response).getAsJsonObject();

            if (responseInJson.has("error-codes")) {
                JsonArray errorCodes = responseInJson.get("error-codes").getAsJsonArray();
                log.warning("Error codes during reCAPTCHA verification: " + errorCodes.toString());
            }

            return Boolean.parseBoolean(responseInJson.get("success").toString());
        } catch (Exception e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }

    protected String getApiResponse(String captchaResponse, String secretKey) throws URISyntaxException, IOException {
        URIBuilder urlb = new URIBuilder(VERIFY_URL);
        urlb.setParameter("secret", secretKey);
        urlb.setParameter("response", captchaResponse);

        return HttpRequest.executeGetRequest(urlb.build());
    }

}
