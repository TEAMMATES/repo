package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.util.Config;
import teammates.common.util.Const;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class ImageUploadAction extends Action {

    FileUploadPageData data;

    @Override
    protected ActionResult execute() {
        data = new FileUploadPageData(account);
        BlobInfo blobInfo = extractImageKey(Const.ParamsNames.IMAGE_TO_UPLOAD);

        if (blobInfo == null) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;
            log.info("Image Upload Failed");
            statusToAdmin = "Image Upload Failed";

            return createAjaxResult(data);
        }

        BlobKey blobKey = blobInfo.getBlobKey();

        data.isFileUploaded = true;
        data.fileSrcUrl = Const.ActionURIs.PUBLIC_FILE_SERVE + "?blob-key=" + blobKey.getKeyString();
        String absoluteFileSrcUrl = Config.getAppUrl(data.fileSrcUrl).toAbsoluteString();

        log.info("New Image Uploaded : " + absoluteFileSrcUrl);
        statusToAdmin = "New Image Uploaded : " + "<a href="
                + data.fileSrcUrl + " target=blank>" + absoluteFileSrcUrl + "</a>";
        data.ajaxStatus = "Image Successfully Uploaded to Google Cloud Storage";

        return createAjaxResult(data);
    }

    protected BlobInfo extractImageKey(String param) {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(param);

            if (blobs == null || blobs.isEmpty()) {
                data.ajaxStatus = Const.StatusMessages.NO_IMAGE_GIVEN;
                isError = true;
                return null;
            }
            
            BlobInfo image = blobs.get(0);
            return validateImage(image);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    protected BlobInfo validateImage(BlobInfo image) {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.IMAGE_TOO_LARGE;
            return null;
        } else if (!image.getContentType().contains("image/")) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.FILE_NOT_A_PICTURE;
            return null;
        }
           
        return image;
    }

    protected void deleteImage(BlobKey blobKey) {
        if (blobKey.equals(new BlobKey(""))) {
            return;
        }
        
        try {
            deleteUploadedFile(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                    + " : Unable to delete picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }

    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteUploadedFile(blobKey);
    }
}
