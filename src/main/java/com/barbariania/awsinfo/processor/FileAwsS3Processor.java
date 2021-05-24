package com.barbariania.awsinfo.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import static com.barbariania.awsinfo.processor.FileProcessorHelper.getExtension;

@Component
public class FileAwsS3Processor implements FileProcessor
{
    @Value("${aws.bucket.name:testawsfilestorage}")
    private String bucketName;
    @Value("${aws.bucket.region}")
    private String bucketRegion;
    @Value("${aws.bucket.accessKey}")
    private String accessKey;
    @Value("${aws.bucket.secretKey}")
    private String secretKey;

    private AmazonS3 s3Client = null;

    @PostConstruct
    public void finishInit() {
        s3Client = getS3Client();
    }

    private AmazonS3 getS3Client() {
        Regions clientRegion = Regions.fromName(bucketRegion);

        AWSCredentialsProvider awsCredentialsProvider =
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));

        return AmazonS3ClientBuilder.standard()
                                    .withCredentials(awsCredentialsProvider)
                                    //or set credentials with https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html to avoid setting credentials here
                                    .withRegion(clientRegion)
                                    .build();
    }

    @Override
    public String upload(MultipartFile file) throws IOException
    {
        String filename = file.getOriginalFilename();
        try {
            byte[] contents = IOUtils.toByteArray(file.getInputStream());
            try(InputStream stream = new ByteArrayInputStream(contents)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(contents.length);
                metadata.setContentType("image/" + getExtension(filename));
                s3Client.putObject(new PutObjectRequest(bucketName, filename, stream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
            }

            return String.format("https://%s.s3.eu-west-2.amazonaws.com/%s", bucketName, filename); //link to object got from aws object
//            return String.format("https://s3.console.aws.amazon.com/s3/object/%s/%s", bucketName, stringObjKeyName); //link to aws object
        }
        catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            e.printStackTrace();
            throw new IOException("Cannot save file to s3 bucket" + e.getErrorMessage());
        }
        catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw new IOException("Cannot save file to s3 bucket" + e.getLocalizedMessage());
        }
    }

    @Override
    public byte[] download(String filename) throws IOException
    {
        try (InputStream is = getFileInputStream(filename)){
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            //handle errors
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteByName(String filename)
    {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, filename));
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    private InputStream getFileInputStreamByUrl(String filename) throws IOException {
        String fileurl = String.format("https://%s.s3.eu-west-2.amazonaws.com/%s", bucketName, filename);
        URL url = new URL(fileurl);
        return url.openStream();
    }

    private InputStream getFileInputStreamByS3(String filename) {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, filename));
        return s3Object.getObjectContent();
    }

    private InputStream getFileInputStream(String filename) {
        ///** use getFileInputStreamByUrl in case file is public accessible
        return getFileInputStreamByS3(filename);
    }
}
