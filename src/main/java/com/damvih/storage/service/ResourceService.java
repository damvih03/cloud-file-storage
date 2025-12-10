package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        PathComponents pathComponents = new PathComponents(path, userDto);
        String fullPath = pathComponents.getFull();

        MinioResponse minioResponse = minioRepository.getObjectInformation(pathComponents);
        log.info("UserID '{}' received metadata for resource '{}'.", userDto.getId(), fullPath);
        return resourceMapper.toResponseDto(minioResponse);
    }

    public void delete(String path, UserDto userDto) {
        PathComponents pathComponents = new PathComponents(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        List<String> objectNames;
        if (pathComponents.isResourceDirectory()) {
            objectNames = minioRepository.getObjectNames(fullPath, true);
        } else {
            objectNames = List.of(fullPath);
        }

        minioRepository.removeObjects(objectNames);
        log.info("Resource '{}' deleted successfully by UserID '{}'.", fullPath, userDto.getId());
    }

}
