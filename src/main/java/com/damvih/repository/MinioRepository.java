package com.damvih.repository;

import com.damvih.config.minio.MinioClientProperties;
import com.damvih.exception.MinioOperationException;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;
    private final MinioClientProperties minioClientProperties;

    public Optional<StatObjectResponse> getObjectStat(String key) {
        try {
            return Optional.of(minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .object(key)
                    .build()));
        } catch (ErrorResponseException exception) {
            String code = exception.errorResponse().code();
            if (code.equals("NoSuchKey")) {
                return Optional.empty();
            }
            throw new MinioOperationException(exception.getMessage());
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

}
