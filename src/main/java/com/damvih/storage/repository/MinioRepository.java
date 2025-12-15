package com.damvih.storage.repository;

import com.damvih.storage.config.MinioClientProperties;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.exception.MinioOperationException;
import com.damvih.storage.exception.ResourceNotFoundException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioRepository {

    private final MinioClient minioClient;
    private final MinioClientProperties minioClientProperties;

    public MinioResponse getObjectInformation(String key) {
        StatObjectResponse statObjectResponse = getStatObject(key)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Resource '%s' not found.", key)
                ));
        return new MinioResponse(key, statObjectResponse.size());
    }

    public void removeObjects(List<String> objectNames) {
        try {
            List<DeleteObject> objectsToDelete = createObjectsToDelete(objectNames);

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .objects(objectsToDelete)
                    .build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("During deleting object '{}' an exception occurred '{}'", error.objectName(), error);
            }

        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    public void createDirectory(String key) {
        putObject(key, new ByteArrayInputStream(new byte[]{}), 0L);
    }

    public List<String> getObjectNames(String prefix, boolean recursive) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .prefix(prefix)
                    .recursive(recursive)
                    .build()
            );
            return extractObjectNames(results);
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    public boolean isObjectExists(String key) {
        return getStatObject(key).isPresent();
    }

    public byte[] getObjectData(String key) {
        try (InputStream stream =
                     minioClient.getObject(GetObjectArgs.builder()
                             .bucket(minioClientProperties.getBucketName())
                             .object(key)
                             .build())) {

            return stream.readAllBytes();
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    public MinioResponse copyObject(String oldKey, String newKey) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(minioClientProperties.getBucketName())
                    .object(newKey)
                    .source(CopySource.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(oldKey)
                            .build())
                    .build());

            return getObjectInformation(newKey);
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    public void saveObject(String key, byte[] data, long size) {
        putObject(key, new ByteArrayInputStream(data), size);
    }

    private Optional<StatObjectResponse> getStatObject(String key) {
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

    private List<String> extractObjectNames(Iterable<Result<Item>> results) {
        try {
            List<String> items = new ArrayList<>();
            for (Result<Item> result : results) {
                items.add(result.get().objectName());
            }
            return items;
        } catch (Exception exception) {
            throw new MinioOperationException(exception.getMessage());
        }
    }

    private List<DeleteObject> createObjectsToDelete(List<String> objectNames) {
        List<DeleteObject> objectsToDelete = new ArrayList<>();

        for (String objectName : objectNames) {
            log.debug("Added object '{}' to delete.", objectName);
            objectsToDelete.add(new DeleteObject(objectName));
        }

        return objectsToDelete;
    }

}
