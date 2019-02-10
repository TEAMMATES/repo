package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Url;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: Generates the uploadUrl for pictures given by students.
 *         A dynamic generation is done to circumvent the 10 minute
 *         time limit for such URLs
 */
public class PostStudentProfileFormUrlAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {

        // To cleanup with constants later
        String callbackUrl = Url.addParamToUrl("http://localhost:8080/webapi" + Const.ResourceURIs.STUDENT_PROFILE_PICTURE,
                Const.CsrfConfig.TOKEN_HEADER_NAME, req.getHeader("X-CSRF-TOKEN"));
        try {
            String uploadUrl = GoogleCloudStorageHelper.getNewUploadUrl(callbackUrl);
            StudentProfileFormUrl dataFormat = new StudentProfileFormUrl(uploadUrl);
            return new JsonResult(dataFormat);
        } catch (BlobstoreFailureException | IllegalArgumentException e) {
            return new JsonResult("Failed to create profile picture upload-url: " + e.getMessage(),
                    HttpStatus.SC_BAD_REQUEST);
        }
    }

    /**
     * Data format for {@link PostStudentProfileFormUrlAction}.
     */
    public static class StudentProfileFormUrl extends ApiOutput {
        private final String formUrl;

        public StudentProfileFormUrl(String formUrl) {
            this.formUrl = formUrl;
        }

        public String getFormUrl() {
            return formUrl;
        }
    }
}
