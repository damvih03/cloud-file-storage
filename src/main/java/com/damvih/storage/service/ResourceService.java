package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
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
    public List<ResourceResponseDto> upload(UploadResourceRequestDto uploadResourceRequestDto, UserDto userDto) {
        PathComponents parent = PathComponentsBuilder.build(uploadResourceRequestDto.getPath(), userDto);

        if (!parent.isResourceDirectory() || !minioRepository.isObjectExists(parent.getFull())) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Parent directory path '%s' not found for UserID '%s'.", parent.getFull(), userDto.getId())
            );
        }

        List<String> addedObjects = new ArrayList<>();
        for (MultipartFile file : uploadResourceRequestDto.getFiles()) {
            String fullFilePath = parent.getFull() + file.getOriginalFilename();
            if (minioRepository.isObjectExists(fullFilePath)) {
                throw new ResourceAlreadyExistsException(
                        String.format("Resource '%s' already exists.", fullFilePath)
                );
            }

            PathComponents objectPathWithoutParentPath = PathComponentsBuilder.build(file.getOriginalFilename(), userDto);
            List<String> directories = objectPathWithoutParentPath.getParentDirectoryNames();
            for (String directory : directories) {
                PathComponents directoryPath =  PathComponentsBuilder.build(parent.getWithoutRootDirectory() + directory, userDto);
                if (!minioRepository.isObjectExists(directoryPath.getFull())) {
                    minioRepository.createDirectory(directoryPath.getFull());
                    log.info("Create empty directory '{}'.", directoryPath.getFull());
                    addedObjects.add(directoryPath.getFull());
                }
            }

            minioRepository.saveObject(fullFilePath, file.getBytes(), file.getSize());
            addedObjects.add(fullFilePath);
            log.info("File '{}' saved to storage.", fullFilePath);
        }

        return resourceMapper.toResponseDto(addedObjects.stream()
                .map(minioRepository::getObjectInformation)
                .toList());
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
