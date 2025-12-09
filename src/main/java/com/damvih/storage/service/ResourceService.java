package com.damvih.storage.service;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final MinioRepository minioRepository;
    private final PathService pathService;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        String fullPath = pathService.getFull(path, userDto);
        StatObjectResponse statObjectResponse = minioRepository.getObjectStat(fullPath)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Resource not found: %s.", fullPath)
                ));
        return resourceMapper.toResponseDto(path, statObjectResponse);
    }

    public void delete(String path, UserDto userDto) {
        String fullPath = pathService.getFull(path, userDto);

        if (!isExists(fullPath)) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", fullPath)
            );
        }

        if (pathService.isDirectory(path)) {
            minioRepository.removeObjects(fullPath);
        } else {
            minioRepository.removeObject(fullPath);
        }
    }

    public boolean isExists(String fullPath) {
        return minioRepository.getObjectStat(fullPath).isPresent();
    }

}
