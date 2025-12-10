package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        PathComponents pathComponents = new PathComponents(path, userDto);
        String fullPath = pathComponents.getFull();

        StatObjectResponse statObjectResponse = minioRepository.getObjectStat(fullPath)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("UserID '%s' did not find resource '%s'.", userDto.getId(), fullPath)
                ));
        log.info("UserID '{}' received metadata for resource '{}'.", userDto.getId(), fullPath);
        return resourceMapper.toResponseDto(pathComponents, statObjectResponse);
    }

    public void delete(String path, UserDto userDto) {
        PathComponents pathComponents = new PathComponents(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        if (pathComponents.isResourceDirectory()) {
            minioRepository.removeObjects(fullPath);
        } else {
            minioRepository.removeObject(fullPath);
        }
        log.info("Resource '{}' deleted successfully by UserID '{}'.", fullPath, userDto.getId());
    }

}
