package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceDownloadService {

    private final DirectoryService directoryService;
    private final MinioRepository minioRepository;

    public byte[] execute(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            log.info("Resource '{}' not found.", fullPath);
            throw new ResourceNotFoundException("Resource not found.");
        }

        if (pathComponents.isResourceDirectory()) {
            return directoryService.download(pathComponents);
        }

        return minioRepository.getObjectData(fullPath);
    }

}
