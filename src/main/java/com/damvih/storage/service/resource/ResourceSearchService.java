package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.mapper.ResourceMapper;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceSearchService {

    private final DirectoryService directoryService;
    private final MinioRepository minioRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceResponseDto> find(String query, UserDto userDto) {
        PathComponents root = PathComponentsBuilder.build("", userDto);
        List<String> objectNames = directoryService.getObjectNames(root, true)
                .stream()
                .toList();

        List<String> filteredObjectNames = filterByQuery(query, objectNames);

        return resourceMapper.toResponseDto(
                filteredObjectNames.stream()
                        .map(minioRepository::getObjectInformation).toList()
        );
    }

    private List<String> filterByQuery(String query, List<String> objectNames) {
        query = query.toLowerCase();
        List<String> filteredObjectNames = new ArrayList<>();
        for (PathComponents objectPathComponents : PathComponentsBuilder.buildByFullPaths(objectNames)) {
            String resourceNameInLowerCase = objectPathComponents.getResourceName().toLowerCase();
            if (resourceNameInLowerCase.contains(query)) {
                filteredObjectNames.add(objectPathComponents.getFull());
            }
        }
        return filteredObjectNames;
    }

}
