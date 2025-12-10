package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.DirectoryAlreadyExistsException;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.repository.MinioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryService {

    private final MinioRepository minioRepository;
    private final PathService pathService;

    public void create(String path, UserDto userDto) {
        String normalizedPath = normalizePath(path);
        String fullPath = pathService.getFull(normalizedPath, userDto);

        if (minioRepository.isObjectExists(fullPath)) {
            throw new DirectoryAlreadyExistsException(
                    String.format("Directory with name '%s' already exists.", fullPath)
            );
        }

        String fullParentPath = pathService.getParentPath(Arrays.asList(fullPath.split("/")));

        if (!minioRepository.isObjectExists(fullParentPath)) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Parent directory path '%s' not found for UserID '%s'.", fullParentPath, userDto.getId())
            );
        }

        minioRepository.createDirectory(fullPath);
        log.info("UserID '{}' created directory '{}'.", userDto.getId(), fullPath);
    }

    private String normalizePath(String path) {
        if (!pathService.isDirectory(path)) {
            path += "/";
        }
        return path;
    }

}
