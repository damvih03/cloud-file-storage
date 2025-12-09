package com.damvih.storage.repository;

import com.damvih.storage.config.MinioClientProperties;
import com.damvih.storage.exception.MinioOperationException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
            Iterable<Result<Item>> items = getObjects(prefix, true);
            List<DeleteObject> objectsToDelete = createObjectsToDelete(extractItems(items));

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

    public void createDirectory(String key) {
        putObject(key, new ByteArrayInputStream(new byte[]{}), 0L);
    }

    private void putObject(String key, InputStream stream, Long size) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .object(key)
                    .stream(stream, size, -1)
                    .build()
            );
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    private Iterable<Result<Item>> getObjects(String prefix, boolean recursive) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioClientProperties.getBucketName())
                .prefix(prefix)
                .recursive(recursive)
                .build()
        );
    }

    private List<Item> extractItems(Iterable<Result<Item>> results) {
        try {
            List<Item> items = new ArrayList<>();
            for (Result<Item> result : results) {
                items.add(result.get());
            }
            return items;
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    private List<DeleteObject> createObjectsToDelete(List<Item> items) {
        List<DeleteObject> objectsToDelete = new ArrayList<>();

        for (Item item : items) {
            objectsToDelete.add(new DeleteObject(item.objectName()));
        }

        return objectsToDelete;
    }

}
