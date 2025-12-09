package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.repository.MinioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioRepository minioRepository;
    private final PathService pathService;
    private final ResourceService resourceService;

    public void create(String path, UserDto userDto) {
        if (!pathService.isDirectory(path)) {
            path += "/";
        }

        String fullPath = pathService.getFull(path, userDto);

        if (resourceService.isExists(fullPath)) {
            throw new RuntimeException("Directory already exists.");
        }

        String parentPath = pathService.getParentPath(Arrays.asList(path.split("/")));
        String fullParentPath = pathService.getFull(parentPath, userDto);

        if (!resourceService.isExists(fullParentPath)) {
            throw new ResourceNotFoundException(
                    String.format("Parent path not found: %s.", fullParentPath)
            );
        }

        minioRepository.createDirectory(fullPath);
    }

}
