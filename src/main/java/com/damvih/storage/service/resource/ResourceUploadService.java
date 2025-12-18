package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.exception.ResourceAlreadyExistsException;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceUploadService {

    private final StorageRepository storageRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceResponseDto> execute(UploadResourceRequestDto request, UserDto user) {
        PathComponents parent = PathComponentsBuilder.build(request.getPath(), user);

        if (!parent.isResourceDirectory() || !storageRepository.isObjectExists(parent.getFull())) {
            log.info("Parent directory '{}' not found.", parent.getFull());
            throw new ResourceNotFoundException("Parent directory not found.");
        }

        List<String> addedObjects = Arrays.stream(request.getFiles())
                .flatMap(file -> uploadFile(file, parent, user).stream())
                .toList();

        return addedObjects.stream()
                .map(storageRepository::getObjectInformation)
                .map(resourceMapper::toResponseDto)
                .toList();
    }

    @SneakyThrows(IOException.class)
    private List<String> uploadFile(MultipartFile file, PathComponents parent, UserDto user) {
        String fullFilePath = parent.getFull() + file.getOriginalFilename();

        if (storageRepository.isObjectExists(fullFilePath)) {
            log.info("Resource '{}' already exists.", fullFilePath);
            throw new ResourceAlreadyExistsException("Resource already exists.");
        }

        List<String> addedObjects = new ArrayList<>(
                createMissingDirectories(
                        file.getOriginalFilename(),
                        parent,
                        user)
        );

        storageRepository.saveObject(fullFilePath, file.getInputStream(), file.getSize());
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

            if (!storageRepository.isObjectExists(fullDirectoryPath.getFull())) {
                storageRepository.createDirectory(fullDirectoryPath.getFull());
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

}
