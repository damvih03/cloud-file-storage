package com.damvih.storage.mapper;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.MinioResponse;
import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.*;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ResourceMapper {

    default ResourceResponseDto toResponseDto(MinioResponse minioResponse) {
        PathComponents pathComponents = PathComponentsBuilder.buildByFullPath(minioResponse.getFullPath());
        return new ResourceResponseDto(
                pathComponents.getParentDirectory().isEmpty() ? "/" : pathComponents.getParentDirectory(),
                pathComponents.getResourceName(),
                pathComponents.isResourceDirectory() ? null : minioResponse.getSize(),
                pathComponents.getResourceType()
        );
    }

    List<ResourceResponseDto> toResponseDto(List<MinioResponse> minioResponses);

}
