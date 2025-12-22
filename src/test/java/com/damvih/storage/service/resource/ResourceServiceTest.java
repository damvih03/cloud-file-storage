package com.damvih.storage.service.resource;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import com.damvih.storage.util.StorageTestcontainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(StorageRepository.class)
@ComponentScan(basePackages = {"com.damvih.storage.mapper"})
public class ResourceServiceTest extends StorageTestcontainer {

    protected static final UserDto USER_DTO = new UserDto(1L, "test");

    @Autowired
    protected StorageRepository storageRepository;

    @BeforeAll
    public void setup() {
        PathComponents path = PathComponentsBuilder.build("", USER_DTO);
        storageRepository.createDirectory(path.getFull());
    }

}
