package com.damvih.storage.util;

import com.damvih.storage.config.MinioClientProperties;
import com.damvih.storage.exception.StorageOperationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
            log.error(exception.getMessage(), exception);
            throw new StorageOperationException("Failed to create bucket with name: " + minioClientProperties.getBucketName() + ".");
        }
    }

}
