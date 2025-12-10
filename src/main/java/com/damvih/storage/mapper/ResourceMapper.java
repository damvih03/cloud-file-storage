package com.damvih.storage.mapper;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMapper {

    public ResourceResponseDto toResponseDto(MinioResponse minioResponse) {
        PathComponents pathComponents = minioResponse.getPathComponents();
        return new ResourceResponseDto(
                pathComponents.getParentDirectory().isEmpty() ? "/" : pathComponents.getParentDirectory(),
                pathComponents.getResourceName(),
                pathComponents.isResourceDirectory() ? null : minioResponse.getSize(),
                pathComponents.getResourceType()
        );
    }

}
