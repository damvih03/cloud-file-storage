package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.StorageResponse;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceInformationService {

    private final StorageRepository storageRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto get(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        StorageResponse storageResponse = storageRepository.getObjectInformation(fullPath);
        log.info("UserID '{}' received metadata for resource '{}'.", userDto.id(), fullPath);
        return resourceMapper.toResponseDto(storageResponse);
    }

}
