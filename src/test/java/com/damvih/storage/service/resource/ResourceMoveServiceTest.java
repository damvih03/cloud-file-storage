package com.damvih.storage.service.resource;

import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.service.ZipCreationService;
import com.damvih.storage.util.MultipartFileGenerator;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Import({ResourceMoveService.class, DirectoryService.class, ResourceUploadService.class})
public class ResourceMoveServiceTest extends ResourceServiceTest {

    @Autowired
    private ResourceMoveService resourceMoveService;

    @MockitoBean
    private ZipCreationService zipCreationService;

    @Autowired
    private ResourceUploadService resourceUploadService;

    @Test
    @DisplayName("Should move directory with all contained resources")
    @SneakyThrows
    public void testCorrectMultipleResourcesMoving() {
        PathComponents oldDirectory = PathComponentsBuilder.build(UUID.randomUUID() + "/", USER_DTO);
        storageRepository.createDirectory(oldDirectory.getFull());

        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                oldDirectory.getWithoutRootDirectory(),
                MultipartFileGenerator.generate(3)
        );
        resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        PathComponents newDirectory = PathComponentsBuilder.build(UUID.randomUUID() + "/", USER_DTO);
        resourceMoveService.execute(
                oldDirectory.getWithoutRootDirectory(),
                newDirectory.getWithoutRootDirectory(),
                USER_DTO);

        Assertions.assertFalse(storageRepository.isObjectExists(oldDirectory.getFull()),
                "Directory with old name still exists");
        Assertions.assertTrue(storageRepository.isObjectExists(newDirectory.getFull()),
                "Directory with new name does not exist");

        for (MultipartFile file : uploadResourceRequestDto.getFiles()) {
            PathComponents newFilePath = PathComponentsBuilder.build(
                    newDirectory.getWithoutRootDirectory() + file.getOriginalFilename(),
                    USER_DTO);

            Assertions.assertFalse(storageRepository.isObjectExists(oldDirectory.getFull() + file.getOriginalFilename()),
                    "File inside old directory still exists");
            Assertions.assertTrue(storageRepository.isObjectExists(newFilePath.getFull()),
                    "File inside moved directory does not exist");
            Assertions.assertArrayEquals(file.getBytes(), storageRepository.getObjectData(newFilePath.getFull()),
                    "Moved file inside moved directory has broken content");
        }
    }


    @Test
    @DisplayName("Should move file to new location")
    public void testCorrectSingleFileMoving() {
        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                "/",
                MultipartFileGenerator.generate(1)
        );
        resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        MultipartFile file = uploadResourceRequestDto.getFiles()[0];
        PathComponents newFilePath = PathComponentsBuilder.build(
                UUID.randomUUID().toString(),
                USER_DTO);
        resourceMoveService.execute(
                file.getOriginalFilename(),
                newFilePath.getWithoutRootDirectory(),
                USER_DTO
        );

        Assertions.assertFalse(storageRepository.isObjectExists(file.getOriginalFilename()),
                "File with name still exists");
        Assertions.assertTrue(storageRepository.isObjectExists(newFilePath.getFull()),
                "File with new name does not exist");
    }

}
