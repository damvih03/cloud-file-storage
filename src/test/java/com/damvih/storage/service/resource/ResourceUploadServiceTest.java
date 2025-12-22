package com.damvih.storage.service.resource;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.MultipartFileGenerator;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Import({ResourceUploadService.class})
public class ResourceUploadServiceTest extends ResourceServiceTest {

    @Autowired
    private ResourceUploadService resourceUploadService;

    @Test
    @DisplayName("Should upload single resource without subdirectory successfully")
    public void testUploadingSingleResourceNotInDirectory() {
        MultipartFile file = MultipartFileGenerator.generate(1)[0];

        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                "/",
                new MultipartFile[]{file}
        );

        List<ResourceResponseDto> uploaded = resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        String fullPath = PathComponentsBuilder.build(file.getOriginalFilename(), USER_DTO).getFull();

        Assertions.assertEquals(1, uploaded.size());
        Assertions.assertTrue(storageRepository.isObjectExists(fullPath));
    }

    @Test
    @DisplayName("Should upload single resource into subdirectory")
    @SneakyThrows
    public void testUploadingSingleResourceInDirectory() {
        MultipartFile file = MultipartFileGenerator.putEachToUniqueDirectories(
                MultipartFileGenerator.generate(1)
        )[0];

        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                "/",
                new MultipartFile[]{file}
        );

        List<ResourceResponseDto> uploaded = resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);

        String fullPath = PathComponentsBuilder.build(file.getOriginalFilename(), USER_DTO).getFull();

        Assertions.assertEquals(2, uploaded.size());
        Assertions.assertTrue(storageRepository.isObjectExists(fullPath));

        Assertions.assertArrayEquals(file.getBytes(), storageRepository.getObjectData(fullPath));
    }

    @Test
    @DisplayName("Should upload multiple resources with subdirectories successfuly")
    @SneakyThrows
    public void testUploadingMultipleResources() {
        int filesAmount = 3;

        PathComponents path = PathComponentsBuilder.build(UUID.randomUUID() + "/", USER_DTO);
        storageRepository.createDirectory(path.getFull());

        MultipartFile[] files = MultipartFileGenerator.putEachToUniqueDirectories(
                MultipartFileGenerator.generate(filesAmount)
        );

        UploadResourceRequestDto uploadResourceRequestDto = new UploadResourceRequestDto(
                path.getWithoutRootDirectory(),
                files
        );

        List<ResourceResponseDto> uploaded = resourceUploadService.execute(uploadResourceRequestDto, USER_DTO);
        Assertions.assertEquals(filesAmount * 2, uploaded.size());

        for (MultipartFile file : files) {
            String fullPath = PathComponentsBuilder.build(path.getWithoutRootDirectory() + file.getOriginalFilename(), USER_DTO).getFull();

            Assertions.assertTrue(storageRepository.isObjectExists(fullPath));
            Assertions.assertArrayEquals(file.getBytes(), storageRepository.getObjectData(fullPath));
        }
    }

}
