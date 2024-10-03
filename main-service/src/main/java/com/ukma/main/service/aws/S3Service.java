package com.ukma.main.service.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;

@Service
public class S3Service {

    private final S3Client s3Client;

    private final String bucketName = "book-social-network";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public InputStream downloadImage(String imageName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("images/"+imageName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    public void uploadImage(String imageName, InputStream inputStream, Long contentLength) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("images/"+imageName)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, contentLength));
    }
}
