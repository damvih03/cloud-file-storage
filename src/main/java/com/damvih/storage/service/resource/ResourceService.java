package com.damvih.storage.service.resource;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceUploadService resourceUploadService;
    private final ResourceMoveService resourceMoveService;
    private final ResourceDownloadService resourceDownloadService;
    private final ResourceSearchService resourceSearchService;
    private final ResourceInformationService resourceInformationService;
    private final ResourceDeleteService resourceDeleteService;

    public ResourceResponseDto get(String path, UserDto userDto) {
        return resourceInformationService.get(path, userDto);
    }

    public void delete(String path, UserDto userDto) {
        resourceDeleteService.execute(path, userDto);
    }

    public byte[] download(String path, UserDto userDto) {
        return resourceDownloadService.execute(path, userDto);
    }

    public ResourceResponseDto move(String from, String to, UserDto userDto) {
        return resourceMoveService.execute(from, to, userDto);
    }

    public List<ResourceResponseDto> find(String query, UserDto userDto) {
        return resourceSearchService.execute(query, userDto);
    }

    public List<ResourceResponseDto> upload(UploadResourceRequestDto uploadResourceRequestDto, UserDto userDto) {
        return resourceUploadService.execute(uploadResourceRequestDto, userDto);
    }

}
