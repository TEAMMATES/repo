package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobKey;

public class AdminEmailImageUploadAction extends ImageUploadAction {

    @Override
    protected ActionResult execute() {
        GateKeeper.inst().verifyAdminPrivileges(account);

        FileUploadPageData uploadPageData = prepareData();
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        data.isFileUploaded = uploadPageData.isFileUploaded;
        data.fileSrcUrl = uploadPageData.fileSrcUrl;
        data.ajaxStatus = uploadPageData.ajaxStatus;

        return createAjaxResult(data);
    }

    @Override
    protected String getImageKeyParam() {
        return Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD;
    }

    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteAdminEmailUploadedFile(blobKey);
    }
}
