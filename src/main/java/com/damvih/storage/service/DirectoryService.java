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
    private final ResourceService resourceService;

    public void create(String path, UserDto userDto) {
        if (!pathService.isDirectory(path)) {
            path += "/";
        }

        String fullPath = pathService.getFull(path, userDto);

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

}
