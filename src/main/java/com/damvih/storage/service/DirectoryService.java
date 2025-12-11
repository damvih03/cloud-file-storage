package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.exception.DirectoryAlreadyExistsException;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryService {

    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public void create(String path, UserDto userDto) {
        String normalizedPath = normalizeDirectoryName(path);
        PathComponents pathComponents = PathComponentsBuilder.build(normalizedPath, userDto);
        String fullPath = pathComponents.getFull();

        if (minioRepository.isObjectExists(fullPath)) {
            throw new DirectoryAlreadyExistsException(
                    String.format("Directory with name '%s' already exists.", fullPath)
            );
        }

        String fullParentDirectory = pathComponents.getFullParentDirectory();
        if (!minioRepository.isObjectExists(fullParentDirectory)) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Parent directory path '%s' not found for UserID '%s'.", fullParentDirectory, userDto.getId())
            );
        }

        minioRepository.createDirectory(pathComponents.getFull());
        log.info("UserID '{}' created directory '{}'.", userDto.getId(), pathComponents.getFull());
    }

    public List<ResourceResponseDto> list(String path, UserDto userDto) {
        String normalizedPath = normalizeDirectoryName(path);
        PathComponents pathComponents = PathComponentsBuilder.build(normalizedPath, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource '%s' not found.", fullPath)
            );
        }

        List<MinioResponse> minioResponses = new ArrayList<>();
        List<String> objectNames = minioRepository.getObjectNames(fullPath, false);
        objectNames.remove(fullPath);
        for (String objectName : objectNames) {
            PathComponents objectPathComponents = PathComponentsBuilder.buildByFullPath(objectName);
            minioResponses.add(minioRepository.getObjectInformation(objectPathComponents));
            log.debug("Object name '{}' is changed to '{}'", objectName, pathComponents.getFull());
        }
        return minioResponses.stream()
                .map(resourceMapper::toResponseDto)
                .toList();
    }

    private String normalizeDirectoryName(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }

}
