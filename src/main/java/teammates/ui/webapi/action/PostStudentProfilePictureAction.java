package teammates.ui.webapi.action;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: saves the file information of the profile picture
 *         that was just uploaded.
 */
public class PostStudentProfilePictureAction extends Action {
    /*
     * This class is not tested in ActionTests as it is difficult to
     * reproduce the upload action.
     * TODO: To cover it in UiTests.
     */

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
    public ActionResult execute() throws EntityNotFoundException {
        try {
            Part image = extractProfilePicture();
            byte[] imageData = new byte[(int) image.getSize()];
            try (InputStream is = image.getInputStream()) {
                is.read(imageData);
            }
            String pictureKey = GoogleCloudStorageHelper.writeImageDataToGcs(userInfo.id, imageData);
            logic.updateOrCreateStudentProfile(
                    StudentProfileAttributes.updateOptionsBuilder(userInfo.id)
                            .withPictureKey(pictureKey)
                            .build());
            PostStudentProfileResults dataFormat =
                    new PostStudentProfileResults(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED, pictureKey);
            return new JsonResult(dataFormat);
        } catch (InvalidParametersException | IllegalArgumentException | IOException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private Part extractProfilePicture() throws IllegalStateException {
        try {
            Part image = req.getPart("studentprofilephoto");
            if (image == null) {
                throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
            }
            return validateProfilePicture(image);
        } catch (Exception e) {
            return null;
        }
    }

    private Part validateProfilePicture(Part image) throws IllegalArgumentException {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        } else if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
        }
        return image;
    }

    /**
     * Data format for {@link PostStudentProfilePictureAction}.
     */
    public static class PostStudentProfileResults extends ApiOutput {
        private final String message;
        private final String pictureKey;

        public PostStudentProfileResults(String message, String pictureKey) {
            this.message = message;
            this.pictureKey = pictureKey;
        }

        public String getMessage() {
            return message;
        }

        public String getPictureKey() {
            return pictureKey;
        }
    }
}
