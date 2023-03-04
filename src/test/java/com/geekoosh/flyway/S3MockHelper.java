package com.geekoosh.flyway;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.testcontainers.containers.localstack.LocalStackContainer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

public class S3MockHelper {
    private AmazonS3 s3;

    public S3MockHelper(AmazonS3 s3) {
        this.s3 = s3;
    }

    public S3MockHelper(LocalStackContainer localstack) {
        this.s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString(),
                                localstack.getRegion()
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .build();
    }

    public AmazonS3 getS3() {
        return s3;
    }

    public void uploadConfig(Properties confProps, String bucket, String key) throws IOException {
        StringWriter writer = new StringWriter();
        confProps.store(writer, "flyway");
        s3.putObject(bucket, key, writer.toString());
    }
    public void upload(File file, String bucket, String key) {
        s3.putObject(bucket, key, file);
    }
}
