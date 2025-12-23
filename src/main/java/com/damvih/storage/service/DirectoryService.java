package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.StorageResponse;
import com.damvih.storage.exception.ResourceAlreadyExistsException;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.StorageRepository;
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
    private final StorageRepository storageRepository;
    private final ResourceMapper resourceMapper;

    public void create(String path, UserDto userDto) {
        String normalizedPath = normalizeName(path);
        PathComponents pathComponents = PathComponentsBuilder.build(normalizedPath, userDto);
        String fullPath = pathComponents.getFull();

        if (storageRepository.isObjectExists(fullPath)) {
            log.info("Resource '{}' already exists.", fullPath);
            throw new ResourceAlreadyExistsException("Resource already exists.");
        }

        String fullParentDirectory = pathComponents.getFullParentDirectory();
        if (!storageRepository.isObjectExists(fullParentDirectory)) {
            log.info("Parent directory '{}' not found for UserID '{}'", fullParentDirectory, userDto.getId());
            throw new ResourceNotFoundException("Parent directory not found.");
        }

        storageRepository.createDirectory(pathComponents.getFull());
        log.info("UserID '{}' created directory '{}'.", userDto.getId(), pathComponents.getFull());
    }

    public List<ResourceResponseDto> get(String path, UserDto userDto) {
        String normalizedPath = normalizeName(path);
        PathComponents pathComponents = PathComponentsBuilder.build(normalizedPath, userDto);
        String fullPath = pathComponents.getFull();

        if (!storageRepository.isObjectExists(fullPath)) {
            log.info("Directory '{}' not found.", fullPath);
            throw new ResourceNotFoundException("Resource not found.");
        }

        return getObjectsInformation(pathComponents);
    }

    public List<String> getObjectNames(PathComponents pathComponents, boolean recursive) {
        List<String> objectNames = storageRepository.getObjectNames(pathComponents.getFull(), recursive);
        objectNames.remove(pathComponents.getFull());
        return objectNames;
    }

    public byte[] download(PathComponents pathComponents) {
        return zipCreationService.createZip(pathComponents, getObjectNames(pathComponents, true));
    }

    public List<String> copyObjects(PathComponents oldParent, PathComponents newParent) {
        List<String> directoryObjectNames = getObjectNames(oldParent, true);

        for (PathComponents oldObject : PathComponentsBuilder.buildByFullPaths(directoryObjectNames)) {
            PathComponents newObject = changeObjectParentDirectory(oldObject, oldParent, newParent);
            storageRepository.copyObject(oldObject.getFull(), newObject.getFull());
            log.info("Resource inside directory '{}' changed successfully to '{}'.", oldObject.getFull(), newObject.getFull());
        }

        return directoryObjectNames;
    }

    private PathComponents changeObjectParentDirectory(PathComponents oldObjectPathComponents, PathComponents source, PathComponents target) {
        String sourceParent = source.getWithoutRootDirectory();
        String targetParent = target.getWithoutRootDirectory();
        String parentDirectory = oldObjectPathComponents.getParentDirectory();

        String newParentDirectory = targetParent + parentDirectory.substring(sourceParent.length());

        return new PathComponents(
                oldObjectPathComponents.getRootDirectory(),
                newParentDirectory,
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

        List<StorageResponse> storageResponses = new ArrayList<>();
        for (String objectName : objectNames) {
            storageResponses.add(storageRepository.getObjectInformation(objectName));
        }

        return resourceMapper.toResponseDto(storageResponses);
    }

}
