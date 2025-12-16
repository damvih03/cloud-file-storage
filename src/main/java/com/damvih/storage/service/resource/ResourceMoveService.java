package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.exception.ResourceTypesNotMatchesException;
import com.damvih.storage.exception.TargetResourceAlreadyExistsException;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceMoveService {

    private final DirectoryService directoryService;
    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto execute(String from, String to, UserDto userDto) {
        PathComponents source = PathComponentsBuilder.build(from, userDto);
        PathComponents target = PathComponentsBuilder.build(to, userDto);

        validateMove(source, target);

        List<String> objectNames = new ArrayList<>(List.of(source.getFull()));
        if (source.isResourceDirectory()) {
            objectNames.addAll(directoryService.copyObjects(source, target));
        }

        MinioResponse minioResponse = minioRepository.copyObject(source.getFull(), target.getFull());
        minioRepository.removeObjects(objectNames);
        log.info("Resource '{}' changed successfully to '{}' by UserID '{}'.", source.getFull(), target.getFull(), userDto.getId());

        return resourceMapper.toResponseDto(minioResponse);
    }

    private void validateMove(PathComponents source, PathComponents target) {
        if (!source.getResourceType().equals(target.getResourceType())) {
            throw new ResourceTypesNotMatchesException("Source and target resource types do not match.");
        }

        if (!minioRepository.isObjectExists(source.getFull())) {
            throw new ResourceNotFoundException(
                    String.format("Resource not found: %s.", source.getFull())
            );
        }

        if (minioRepository.isObjectExists(target.getFull())) {
            throw new TargetResourceAlreadyExistsException("Target resource already exists.");
        }

        if (!minioRepository.isObjectExists(target.getFullParentDirectory())) {
            throw new ParentDirectoryNotFoundException(
                    String.format("Target parent directory not found: %s.", target.getFullParentDirectory())
            );
        }
    }

}
