package com.damvih.storage.mapper;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.entity.StorageResponse;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.*;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ResourceMapper {

    default ResourceResponseDto toResponseDto(StorageResponse storageResponse) {
        PathComponents pathComponents = PathComponentsBuilder.buildByFullPath(storageResponse.getFullPath());
        return new ResourceResponseDto(
                pathComponents.getParentDirectory(),
                pathComponents.getResourceName() + (pathComponents.isResourceDirectory() ? "/" : ""),
                pathComponents.isResourceDirectory() ? null : storageResponse.getSize(),
                pathComponents.getResourceType()
        );
    }

    List<ResourceResponseDto> toResponseDto(List<StorageResponse> storageResponses);

}
