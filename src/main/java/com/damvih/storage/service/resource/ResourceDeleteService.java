package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.storage.repository.StorageRepository;
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
public class ResourceDeleteService {

    private final DirectoryService directoryService;
    private final StorageRepository storageRepository;

    public void execute(String path, UserDto userDto) {
        PathComponents pathComponents = PathComponentsBuilder.build(path, userDto);
        String fullPath = pathComponents.getFull();

        if (!storageRepository.isObjectExists(fullPath)) {
            log.info("Resource '{}' not found for deleting.", fullPath);
            throw new ResourceNotFoundException("Resource not found.");
        }

        List<String> objectNames = new ArrayList<>(List.of(fullPath));

        if (pathComponents.isDirectory()) {
            objectNames.addAll(directoryService.getObjectNames(pathComponents, true));
        }

        storageRepository.removeObjects(objectNames);
        log.info("Resource '{}' deleted successfully by UserID '{}'.", fullPath, userDto.getId());
    }

}
