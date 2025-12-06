package com.damvih.util;

import com.damvih.exception.MinioOperationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketInitializer {

    @Value("${minio.bucket-name}")
    private String name;

    private final MinioClient minioClient;

    @PostConstruct
    public void createIfNotExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(name)
                    .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(name)
                                .build()
                );
            }
        } catch (Exception exception) {
            throw new MinioOperationException("Failed to create bucket with name: " + name + ".");
        }
    }

}
