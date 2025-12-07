package com.damvih.repository;

import com.damvih.exception.MinioOperationException;
import com.damvih.exception.ResourceNotFoundException;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;

    public StatObjectResponse getObjectStat(String key) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket("user-files")
                    .object(key)
                    .build());
        } catch (ErrorResponseException exception) {
            String code = exception.errorResponse().code();
            if (code.equals("NoSuchKey")) {
                throw new ResourceNotFoundException("Object (" + key + ") not found.");
            }
            throw new MinioOperationException(exception.getMessage());
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

}
