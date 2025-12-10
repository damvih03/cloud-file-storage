package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.exception.DirectoryAlreadyExistsException;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.repository.MinioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryService {

    private final MinioRepository minioRepository;

    public void create(String path, UserDto userDto) {
        String normalizedPath = normalize(path);
        PathComponents pathComponents = new PathComponents(normalizedPath, userDto);
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

    private String normalize(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }

}
