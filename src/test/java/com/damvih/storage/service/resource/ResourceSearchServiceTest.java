package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.service.ZipCreationService;
import com.damvih.storage.util.PathComponentsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

@Import({ResourceSearchService.class, DirectoryService.class})
public class ResourceSearchServiceTest extends ResourceServiceTest {

    @Autowired
    private ResourceSearchService resourceSearchService;

    @MockitoBean
    private ZipCreationService zipCreationService;

    @Test
    @DisplayName("Should find resources recursively")
    public void testSearchExistingResources() {
        int amount = 3;

        List<String> searchedQueryParts = List.of("searched", "Resource", "Name");

        StringBuilder directoryName = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            directoryName
                    .append(String.join("", searchedQueryParts))
                    .append("/");

            PathComponents path = PathComponentsBuilder.build(directoryName.toString(), USER_DTO);
            storageRepository.createDirectory(path.getFull());
        }

        for (String searchedQueryPart : searchedQueryParts) {
            Assertions.assertEquals(amount, resourceSearchService.execute(searchedQueryPart, USER_DTO).size());
        }
    }

    @Test
    @DisplayName("Should return empty result for non-existent resource")
    public void testSearchNonExistingResource() {
        List<ResourceResponseDto> founded = resourceSearchService.execute("non-existing-resource", USER_DTO);

        Assertions.assertTrue(founded.isEmpty());
    }

    @Test
    @DisplayName("Should return no resources when searching another user's files")
    public void testSearchResourceOfAnotherUser(TestReporter reporter) {
        String directoryName = UUID.randomUUID() + "/";

        UserDto otherUser = new UserDto(3L, "otherUser");
        PathComponents userOneDirectoryPath = PathComponentsBuilder.build(directoryName, USER_DTO);
        PathComponents otherUserDirectoryPath = PathComponentsBuilder.build(directoryName, otherUser);

        storageRepository.createDirectory(userOneDirectoryPath.getFull());

        reporter.publishEntry("Check", "Starting verification of resource existence in each user root directories");
        Assertions.assertTrue(storageRepository.isObjectExists(userOneDirectoryPath.getFull()));
        Assertions.assertFalse(storageRepository.isObjectExists(otherUserDirectoryPath.getFull()));

        reporter.publishEntry("Check", "Starting verification of search results visibility");
        List<ResourceResponseDto> foundForUserOne = resourceSearchService.execute(userOneDirectoryPath.getResourceName(), USER_DTO);
        List<ResourceResponseDto> foundForOtherUser = resourceSearchService.execute(userOneDirectoryPath.getResourceName(), otherUser);
        Assertions.assertTrue(foundForOtherUser.isEmpty());
        Assertions.assertFalse(foundForUserOne.isEmpty());
    }

}
