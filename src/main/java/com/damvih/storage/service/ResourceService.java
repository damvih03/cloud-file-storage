package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.exception.*;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final DirectoryService directoryService;
    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        MinioResponse minioResponse = minioRepository.getObjectInformation(fullPath);
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

        List<String> objectNames = new ArrayList<>(List.of(fullPath));

        if (pathComponents.isResourceDirectory()) {
            objectNames.addAll(directoryService.getObjectNames(pathComponents, true));
        }

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
            return directoryService.download(pathComponents);
        }

        return minioRepository.getObjectData(fullPath);
    }

    public ResourceResponseDto move(String from, String to, UserDto userDto) {
        PathComponents source = PathComponentsBuilder.build(from, userDto);
        PathComponents target = PathComponentsBuilder.build(to, userDto);

        validateMove(source, target);

        List<String> objectNames = new ArrayList<>(List.of(source.getFull()));
        if (source.isResourceDirectory()) {
            objectNames.addAll(directoryService.copyObjects(source, target));
        }

        MinioResponse minioResponse = minioRepository.copyObject(source.getFull(), target.getFull());
        minioRepository.removeObjects(objectNames);
        log.info("Resource '{}' changed successfully to '{}' by UserID '{}'.", source.getFull(), target.getFull(), userDto.getId());

        return resourceMapper.toResponseDto(minioResponse);
    }

    public List<ResourceResponseDto> find(String query, UserDto userDto) {
        PathComponents root = PathComponentsBuilder.build("", userDto);
        List<String> objectNames = directoryService.getObjectNames(root, true)
                .stream()
                .toList();

        List<String> filteredObjectNames = filterByQuery(query, objectNames);

        return resourceMapper.toResponseDto(
                filteredObjectNames.stream()
                        .map(minioRepository::getObjectInformation).toList()
        );

    }

    @SneakyThrows
    public List<ResourceResponseDto> upload(UploadResourceRequestDto request, UserDto user) {
        PathComponents parent = PathComponentsBuilder.build(request.getPath(), user);

        if (!parent.isResourceDirectory() || !minioRepository.isObjectExists(parent.getFull())) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Parent directory path '%s' not found.", parent.getFull())
            );
        }

        List<String> addedObjects = Arrays.stream(request.getFiles())
                .flatMap(file -> uploadFile(file, parent, user).stream())
                .toList();

        return addedObjects.stream()
                .map(minioRepository::getObjectInformation)
                .map(resourceMapper::toResponseDto)
                .toList();
    }

    @SneakyThrows
    private List<String> uploadFile(MultipartFile file, PathComponents parent, UserDto user) {
        String fullFilePath = parent.getFull() + file.getOriginalFilename();

        if (minioRepository.isObjectExists(fullFilePath)) {
            throw new ResourceAlreadyExistsException(
                    String.format("Resource '%s' already exists.", fullFilePath)
            );
        }

        List<String> addedObjects = new ArrayList<>(
                createMissingDirectories(
                        file.getOriginalFilename(),
                        parent,
                        user)
        );

        minioRepository.saveObject(fullFilePath, file.getInputStream(), file.getSize());
        log.info("File '{}' saved to storage.", fullFilePath);
        addedObjects.add(fullFilePath);

        return addedObjects;
    }

    private List<String> createMissingDirectories(String fileName, PathComponents parent, UserDto userDto) {
        List<String> missingDirectories = new ArrayList<>();

        List<String> directoryNames = getDirectoryNamesWithoutParent(fileName, userDto);
        for (String directoryName : directoryNames) {
            PathComponents fullDirectoryPath =  PathComponentsBuilder.build(
                    parent.getWithoutRootDirectory() + directoryName,
                    userDto);

            if (!minioRepository.isObjectExists(fullDirectoryPath.getFull())) {
                minioRepository.createDirectory(fullDirectoryPath.getFull());
                log.info("Create empty directory '{}'.", fullDirectoryPath.getFull());
                missingDirectories.add(fullDirectoryPath.getFull());
            }

        }
        return missingDirectories;
    }

    private List<String> getDirectoryNamesWithoutParent(String fileName, UserDto userDto) {
        PathComponents objectPathWithoutParentPath = PathComponentsBuilder.build(fileName, userDto);
        return objectPathWithoutParentPath.getParentDirectoryNames();
    }

    private List<String> filterByQuery(String query, List<String> objectNames) {
        query = query.toLowerCase();
        List<String> filteredObjectNames = new ArrayList<>();
        for (PathComponents objectPathComponents : PathComponentsBuilder.buildByFullPaths(objectNames)) {
            String resourceNameInLowerCase = objectPathComponents.getResourceName().toLowerCase();
            if (resourceNameInLowerCase.contains(query)) {
                filteredObjectNames.add(objectPathComponents.getFull());
            }
        }
        return filteredObjectNames;
    }

    private void validateMove(PathComponents source, PathComponents target) {
        if (!source.getResourceType().equals(target.getResourceType())) {
            throw new ResourceTypesNotMatchesException("Source and target resource types do not match.");
        }

        if (!minioRepository.isObjectExists(source.getFull())) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", source.getFull())
            );
        }

        if (minioRepository.isObjectExists(target.getFull())) {
            throw new TargetResourceAlreadyExistsException("Target resource already exists.");
        }

        if (!minioRepository.isObjectExists(target.getFullParentDirectory())) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Target parent directory not found: %s.", target.getFullParentDirectory())
            );
        }
    }

}
