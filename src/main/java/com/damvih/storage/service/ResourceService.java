package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        MinioResponse minioResponse = minioRepository.getObjectInformation(pathComponents);
        log.info("UserID '{}' received metadata for resource '{}'.", userDto.getId(), fullPath);
        return resourceMapper.toResponseDto(minioResponse);
    }

    public void delete(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        List<String> objectNames = getObjectNames(pathComponents);

        minioRepository.removeObjects(objectNames);
        log.info("Resource '{}' deleted successfully by UserID '{}'.", fullPath, userDto.getId());
    }

    public byte[] download(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        if (pathComponents.isResourceDirectory()) {
            return createZip(pathComponents);
        }

        return minioRepository.getObjectData(fullPath);
    }

    private byte[] createZip(PathComponents pathComponents) {
        List<String> objectNames = getObjectNames(pathComponents);
        objectNames.remove(pathComponents.getFull());

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(stream);

            for (String objectName : objectNames) {
                PathComponents objectPathComponents = PathComponentsBuilder.buildByFullPath(objectName);
                String relativePath = buildRelativePath(pathComponents, objectPathComponents);

                ZipEntry zipEntry = new ZipEntry(relativePath);
                zipOutputStream.putNextEntry(zipEntry);
                if (!objectPathComponents.isResourceDirectory()) {
                    byte[] objectData = minioRepository.getObjectData(objectName);
                    zipOutputStream.write(objectData);
                }
                zipOutputStream.closeEntry();

            }
            zipOutputStream.close();

            return stream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private List<String> getObjectNames(PathComponents pathComponents) {
        if (pathComponents.isResourceDirectory()) {
            return minioRepository.getObjectNames(pathComponents.getFull(), true);
        }
        return List.of(pathComponents.getFull());
    }

    private String buildRelativePath(PathComponents main, PathComponents object) {
        return object.getWithoutRootDirectory()
                .substring(
                        main.getParentDirectory().length()
                );
    }

}
