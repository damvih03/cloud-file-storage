package com.damvih.storage.mapper;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.dto.ResourceType;
import com.damvih.storage.service.PathService;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceMapper {

    private final PathService pathService;

    public ResourceResponseDto toResponseDto(String path, StatObjectResponse statObjectResponse) {
        List<String> parts = Arrays.asList(path.split("/"));
        ResourceType type = pathService.isDirectory(path) ? ResourceType.DIRECTORY : ResourceType.FILE;
        return new ResourceResponseDto(
                path.isEmpty() ? "/" : pathService.getParentPath(parts),
                pathService.getResourceName(parts),
                type == ResourceType.DIRECTORY ? null : statObjectResponse.size(),
                type
        );
    }

}
