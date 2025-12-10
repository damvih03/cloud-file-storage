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
    private final PathService pathService;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        String fullPath = pathService.getFull(path, userDto);
        StatObjectResponse statObjectResponse = minioRepository.getObjectStat(fullPath)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("UserID '%s' did not find resource '%s'.", userDto.getId(), fullPath)
                ));
        log.info("UserID '{}' received metadata for resource '{}'.", userDto.getId(), fullPath);
        return resourceMapper.toResponseDto(path, statObjectResponse);
    }

    public void delete(String path, UserDto userDto) {
        String fullPath = pathService.getFull(path, userDto);

        if (!minioRepository.isObjectExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        if (pathService.isDirectory(path)) {
            minioRepository.removeObjects(fullPath);
        } else {
            minioRepository.removeObject(fullPath);
        }
        log.info("Resource '{}' deleted successfully by UserID '{}'.", fullPath, userDto.getId());
    }

}
