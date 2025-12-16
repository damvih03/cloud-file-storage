package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.exception.ResourceAlreadyExistsException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
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

    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceResponseDto> put(UploadResourceRequestDto request, UserDto user) {
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

    @SneakyThrows(IOException.class)
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

}
