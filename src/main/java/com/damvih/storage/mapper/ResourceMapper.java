package com.damvih.storage.mapper;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.service.PathComponents;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMapper {

    public ResourceResponseDto toResponseDto(PathComponents pathComponents, StatObjectResponse statObjectResponse) {
        return new ResourceResponseDto(
                pathComponents.getParentDirectory().isEmpty() ? "/" : pathComponents.getParentDirectory(),
                pathComponents.getResourceName(),
                pathComponents.isResourceDirectory() ? null : statObjectResponse.size(),
                pathComponents.getResourceType()
        );
    }

}
