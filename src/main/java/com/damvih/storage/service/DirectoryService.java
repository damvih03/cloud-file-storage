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

    private final ZipCreationService zipCreationService;
    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public void create(String path, UserDto userDto) {
        String normalizedPath = normalizeName(path);
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

    public List<ResourceResponseDto> get(String path, UserDto userDto) {
        String normalizedPath = normalizeName(path);
        PathComponents pathComponents = PathComponentsBuilder.build(normalizedPath, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource '%s' not found.", fullPath)
            );
        }

        return getObjectsInformation(pathComponents);
    }

    public List<String> getObjectNames(PathComponents pathComponents, boolean recursive) {
        List<String> objectNames = minioRepository.getObjectNames(pathComponents.getFull(), recursive);
        objectNames.remove(pathComponents.getFull());
        return objectNames;
    }

    public byte[] download(PathComponents pathComponents) {
        return zipCreationService.createZip(pathComponents, getObjectNames(pathComponents, true));
    }

    public List<String> copyObjects(PathComponents oldParent, PathComponents newParent) {
        List<String> directoryObjectNames = getObjectNames(oldParent, true);

        for (PathComponents oldObject : PathComponentsBuilder.buildByFullPaths(directoryObjectNames)) {
            PathComponents newObject = changeObjectParentDirectory(oldObject, newParent);
            minioRepository.copyObject(oldObject.getFull(), newObject.getFull());
            log.info("Resource inside directory '{}' changed successfully to '{}'.", oldObject.getFull(), newObject.getFull());
        }

        return directoryObjectNames;
    }

    private PathComponents changeObjectParentDirectory(PathComponents oldObjectPathComponents, PathComponents target) {
        return new PathComponents(
                oldObjectPathComponents.getRootDirectory(),
                target.getWithoutRootDirectory(),
                oldObjectPathComponents.getResourceName(),
                oldObjectPathComponents.getResourceType()
        );
    }

    private String normalizeName(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }

    private List<ResourceResponseDto> getObjectsInformation(PathComponents pathComponents) {
        List<String> objectNames = getObjectNames(pathComponents, false);

        List<MinioResponse> minioResponses = new ArrayList<>();
        for (String objectName : objectNames) {
            minioResponses.add(minioRepository.getObjectInformation(objectName));
        }

        return resourceMapper.toResponseDto(minioResponses);
    }

}
