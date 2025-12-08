package com.damvih.repository;

import com.damvih.config.minio.MinioClientProperties;
import com.damvih.exception.MinioOperationException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
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

    public void removeObject(String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .object(key)
                    .build());
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    public void removeObjects(String prefix) {
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .prefix(prefix)
                    .recursive(true)
                    .build()
            );

            List<DeleteObject> objectsToDelete = new ArrayList<>();
            for (Result<Item> item : items) {
                objectsToDelete.add(new DeleteObject(item.get().objectName()));
            }

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .objects(objectsToDelete)
                    .build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
            }

        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

}
