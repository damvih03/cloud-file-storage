package com.damvih.storage.service.resource;

import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.service.ZipCreationService;
import com.damvih.storage.util.MultipartFileGenerator;
import com.damvih.storage.util.PathComponentsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Import({ResourceDeleteService.class, ResourceUploadService.class, DirectoryService.class})
public class ResourceDeleteServiceTest extends ResourceServiceTest {

    @Autowired
    private ResourceDeleteService resourceDeleteService;

    @Autowired
    private ResourceUploadService resourceUploadService;

    @MockitoBean
    private ZipCreationService zipCreationService;

    @Test
    @DisplayName("Should delete directory with all contained files")
    public void testCorrectDeletingMultipleResources() {
        PathComponents directory = PathComponentsBuilder.build(UUID.randomUUID() + "/", USER_DTO);
        storageRepository.createDirectory(directory.getFull());

        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                directory.getWithoutRootDirectory(),
                MultipartFileGenerator.generate(3)
        );
        resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        resourceDeleteService.execute(directory.getWithoutRootDirectory(), USER_DTO);

        Assertions.assertFalse(storageRepository.isObjectExists(directory.getFull()),
                "Directory still exists");

        for (MultipartFile file : uploadResourceRequestDto.getFiles()) {
            Assertions.assertFalse(storageRepository.isObjectExists(
                    directory.getFull() + file.getOriginalFilename()),
                    "File inside deleted directory still exists");
        }
    }

    @Test
    @DisplayName("Should successfully delete a file")
    public void testDeletingSingleFile() {
        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                "/",
                MultipartFileGenerator.generate(1)
        );
        resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        MultipartFile file = uploadResourceRequestDto.getFiles()[0];
        PathComponents filePath = PathComponentsBuilder.build(file.getOriginalFilename(), USER_DTO);

        Assertions.assertTrue(storageRepository.isObjectExists(filePath.getFull()));

        resourceDeleteService.execute(file.getOriginalFilename(), USER_DTO);

        Assertions.assertFalse(storageRepository.isObjectExists(filePath.getFull()),
                "File still exists");
    }

}
