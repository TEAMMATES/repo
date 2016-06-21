package teammates.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public final class GoogleCloudStorageHelper {
    
    private static final int MAX_READING_LENGTH = 900000;
    
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
    
    /**
     * This method: <br>
     * 1. makes sure the list file given the blobkey exists in the Google Cloud Storage<br>
     * 2. goes through the list file by splitting the content of the txt file(email addresses separated by comma)
     * into separated email addresses, which makes sure the file content is intact and of correct format
     * <br><br>
     * 
     * @param blobKey
     * @throws IOException
     */
    public static List<List<String>> getGroupReceiverList(BlobKey blobKey) throws IOException {
        Assumption.assertNotNull(blobKey);
        
        // It turns out that error will occur if we read more than around 900000 bytes of data per time
        // from the blobstream, which also brings problems when this large number of emails are all stored in one
        // list. As a result, to prevent unexpected errors, we read the txt file several times and each time
        // at most 900000 bytes are read, after which a new list is created to store all the emails addresses that
        // happen to be in the newly read bytes.
        
        // For email address which happens to be broken according to two consecutive reading, a check will be done
        // before storing all emails separated from the second reading into a new list. Broken email will be fixed by
        // deleting the first item of the email list from current reading  AND
        // appending it to the last item of the email list from last reading
        
        // The email list from each reading is inserted into an upper list (list of list).
        // The structure is as below:
        
        // ListOfList:
        //       ListFromReading_1 :
        //                     [example@email.com]
        //                            ...
        //       ListFromReading_2 :
        //                     [example@email.com]
        //                            ...
        
        // This is the list of list
        List<List<String>> listOfList = new LinkedList<List<String>>();
        
        // Offset is needed for remembering where it stops from last reading
        int offset = 0;
        
        // File size is needed to track the number of unread bytes
        int size = (int) getFileSize(blobKey);
        
        while (size > 0) {
            // Make sure not to over-read
            int bytesToRead = size > MAX_READING_LENGTH ? MAX_READING_LENGTH : size;
            InputStream blobStream = new BlobstoreInputStream(blobKey, offset);
            byte[] array = new byte[bytesToRead];
            
            blobStream.read(array);
            
            // Remember where it stops reading
            offset += MAX_READING_LENGTH;
            // Decrease unread bytes
            size -= MAX_READING_LENGTH;
            
            // Get the read bytes into string and split it by ","
            String readString = new String(array);
            List<String> newList = Arrays.asList(readString.split(","));
            
            if (listOfList.isEmpty()) {
                // This is the first time reading
                listOfList.add(newList);
            } else {
                // Check if the last reading stopped in the middle of a email address string
                List<String> lastAddedList = listOfList.get(listOfList.size() - 1);
                // Get the last item of the list from last reading
                String lastStringOfLastAddedList = lastAddedList.get(lastAddedList.size() - 1);
                // Get the first item of the list from current reading
                String firstStringOfNewList = newList.get(0);
                
                if (lastStringOfLastAddedList.contains("@") && firstStringOfNewList.contains("@")) {
                    // No broken email from last reading found, simply add the list
                    // from current reading into the upper list.
                    listOfList.add(newList);
                } else {
                    // Either the left part or the right part of the broken email string
                    // does not contains a "@".
                    // Simply append the right part to the left part(last item of the list from last reading)
                    listOfList.get(listOfList.size() - 1)
                              .set(lastAddedList.size() - 1, lastStringOfLastAddedList + firstStringOfNewList);
                    
                    // And also needs to delete the right part which is the first item of the list from current reading
                    listOfList.add(newList.subList(1, newList.size() - 1));
                }
            }
            
            blobStream.close();
        }
        
        return listOfList;
    }
    
    private static long getFileSize(BlobKey blobKey) {
        BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
        return blobInfoFactory.loadBlobInfo(blobKey).getSize();
    }
    
}
