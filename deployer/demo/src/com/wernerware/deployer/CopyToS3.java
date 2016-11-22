package com.wernerware.deployer;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.transfer.TransferManager;

public class CopyToS3 {

    static AmazonS3       s3;

    private static void init() throws Exception {

        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("fancypants").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Daniel\\.aws\\credentials), and is in valid format.",
                    e);
        }
        s3  = new AmazonS3Client(credentials);
    }


    public static void main(String[] args) throws Exception {

        System.out.println("===========================================");
        System.out.println("Copying web application to S3");
        System.out.println("===========================================");

        init();

        try {
        	String bucketName = "wernerware-test-bucket-" + Math.random();
        	bucketName = bucketName.replace('.', '0');
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName,Region.US_Standard);
			Bucket buck = s3.createBucket(createBucketRequest);
			
			System.out.println("Just created " + buck.getName() + "; now adding files to it");
			
			TransferManager tm = new TransferManager(s3);
			File webAppDir = new File("C:\\Users\\Daniel\\Documents\\aws-experimentation\\angular_template\\app");
			tm.uploadDirectory(bucketName, null, webAppDir, true);
        } catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
