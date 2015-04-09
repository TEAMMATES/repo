package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailCreateGroupReceiverListUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData data = new AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(account);
        
        data.nextUploadUrl = getNewUploadUrl();
        
        if(data.nextUploadUrl == null){
            data.nextUploadUrl = getNewUploadUrl();
        }
        
        //re-try creating upload url
        if(data.nextUploadUrl == null){
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        } else {
            isError = false;
            data.ajaxStatus = "Group receiver list upload url created, proceed to uploading";
        }
          
        return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
        
    }
    
    
    public String getNewUploadUrl() throws EntityDoesNotExistException {     
        try {
            return generateNewUploadUrl();
        } catch(BlobstoreFailureException e) {
            return null;
        } 
    }

    private String generateNewUploadUrl() {
        UploadOptions uploadOptions = generateUploadOptions();
   
        String formPostUrl = BlobstoreServiceFactory
                             .getBlobstoreService()
                             .createUploadUrl(Const.ActionURIs.ADMIN_EMAIL_GROUP_RECEIVER_LIST_UPLOAD, uploadOptions);
        return formPostUrl;
    }
    
    private UploadOptions generateUploadOptions() {
        UploadOptions uploadOptions = UploadOptions.Builder
                                      .withGoogleStorageBucketName(Config.GCS_BUCKETNAME);
        return uploadOptions;
    }

}
