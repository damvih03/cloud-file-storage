package com.damvih.util;

import com.damvih.config.minio.MinioClientProperties;
import com.damvih.exception.MinioOperationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketInitializer {

    private final MinioClient minioClient;
    private final MinioClientProperties minioClientProperties;

    @PostConstruct
    public void createIfNotExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioClientProperties.getBucketName())
                                .build()
                );
            }
        } catch (Exception exception) {
            throw new MinioOperationException("Failed to create bucket with name: " + minioClientProperties.getBucketName() + ".");
        }
    }

}
