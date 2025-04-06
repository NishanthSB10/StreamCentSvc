package com.stream.cent.service;

import com.stream.cent.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.time.Instant;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final AppConfig appConfig;
    @Autowired
    private FileEventProducer fileEventProducer;

    @Autowired
    public S3StorageService(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.s3Client = S3Client.builder()
                .region(Region.of(appConfig.getAwsRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(appConfig.getAwsAccessKey(), appConfig.getAwsSecretKey())))
                .build();
    }

    public String uploadFile(File file, String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(appConfig.getAwsS3BucketName())
                .key(key).expires(Instant.now().plusSeconds(60))
                .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, Paths.get(file.getAbsolutePath()));
//        fileEventProducer.sendFileUploadedEvent(key);

        return key;
    }

    public HeadObjectResponse getFileMetadata(String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(appConfig.getAwsS3BucketName())
                .key(key)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        return headObjectResponse;
    }

    public byte[] downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(appConfig.getAwsS3BucketName())
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }

    public ResponseInputStream<GetObjectResponse> getObject(String key, long rangeStart, long rangeEnd) {

        String range = "bytes=" + rangeStart + "-" + rangeEnd;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(appConfig.getAwsS3BucketName())
                .key(key)
                .range(range)
                .build();

        ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(getObjectRequest);
        return s3Stream;
    }

    public void uploadVideo(String key, String bucketName, Path file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ) // make public
                .contentType(Files.probeContentType(file))
                .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(request, RequestBody.fromFile(file));
        System.out.println(putObjectResponse);
    }

    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", appConfig.getAwsS3BucketName(), key);
    }
}

