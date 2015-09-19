package sukretbox;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 Stores data to Amazon S3
 */
public class S3Store implements DataStore {

    private AmazonS3 s3;
    private static final String BUCKET_NAME = "sukretbox-storage";

    public byte[] getData(String userName, String fileName) {
        String key = userName + "/" + fileName;
        try {
            InputStream input = s3.getObject(new GetObjectRequest(BUCKET_NAME, key)).getObjectContent();
            return IOUtils.toByteArray(input);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return null;
    }

    public boolean storeData(String userName, String fileName, byte[] data){
        String key = userName + "/" + fileName;
        try {
            s3.putObject(new PutObjectRequest(BUCKET_NAME, key, new ByteArrayInputStream(data), new ObjectMetadata()));
            return true;
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return false;
    }

    public boolean deleteData(String userName, String fileName) {
        String key = userName + "/" + fileName;
        try {
            s3.deleteObject(BUCKET_NAME, key);
            return true;
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return false;
    }

    public List<File> listFiles(String userName) {
        LinkedList<File> fileList = new LinkedList<File>();

        try {
            ObjectListing objectListing = s3.listObjects(
                    new ListObjectsRequest().withBucketName(BUCKET_NAME).withPrefix(userName));
            objectListing.getObjectSummaries().forEach(file ->
                    fileList.add(new File(userName, file.getKey().substring(userName.length() + 1),
                            file.getSize(), 0, File.Storage.SUKRETBOX)));


        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return fileList;
    }

    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName) {
        String key = userName + "/" + fileName;
        String newKey = newUserName + "/" + newFileName;
        try {
            // Copying object
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    BUCKET_NAME, key, BUCKET_NAME, newKey);
            s3.copyObject(copyObjRequest);
            return true;
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error " +
                    "response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to " +
                    " communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return false;
    }

    public S3Store() {
        /*
         * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users) 
         * and save the following lines after replacing the underlined values with your own.
         *
         * [default]
         * aws_access_key_id = YOUR_ACCESS_KEY_ID
         * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
         */
        Logger logger = LoggerFactory.getLogger(S3Store.class);

        s3 = new AmazonS3Client();
        Region region = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(region);
        logger.info("s3store initialized");
    }
}
