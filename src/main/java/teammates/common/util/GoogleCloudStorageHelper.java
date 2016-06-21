package teammates.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public final class GoogleCloudStorageHelper {
    
    private GoogleCloudStorageHelper() {
        // utility class
    }
    
    public static boolean doesFileExistInGcs(BlobKey fileKey) {
        try {
            BlobstoreServiceFactory.getBlobstoreService().fetchData(fileKey, 0, 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static void deleteFile(BlobKey fileKey) {
        BlobstoreServiceFactory.getBlobstoreService().delete(fileKey);
    }
    
    public static String writeImageDataToGcs(String googleId, byte[] imageData) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(Config.GCS_BUCKETNAME, googleId);
        GcsOutputChannel outputChannel =
                GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance())
                                 .createOrReplace(gcsFilename,
                                                  new GcsFileOptions.Builder().mimeType("image/png").build());

        outputChannel.write(ByteBuffer.wrap(imageData));
        outputChannel.close();
        
        return BlobstoreServiceFactory.getBlobstoreService()
                .createGsBlobKey("/gs/" + Config.GCS_BUCKETNAME + "/" + googleId).getKeyString();
    }
    
    public static String getNewUploadUrl(String uploadUrl) {
        UploadOptions uploadOptions =
                UploadOptions.Builder.withDefaults()
                             .googleStorageBucketName(Config.GCS_BUCKETNAME)
                             .maxUploadSizeBytes(Const.SystemParams.MAX_FILE_LIMIT_FOR_BLOBSTOREAPI);
        
        return BlobstoreServiceFactory.getBlobstoreService()
                                      .createUploadUrl(uploadUrl, uploadOptions);
    }
    
}
