package com.damvih.service;

import com.damvih.dto.UserRegistrationRequestDto;
import com.damvih.exception.UserAlreadyExistsException;
import com.damvih.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.4.7")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.name", mySqlContainer::getDatabaseName);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @Test
    public void testCorrectCreatingUser() {
        UserRegistrationRequestDto  userRegistrationRequestDto = new UserRegistrationRequestDto(
                "user1",
                "pass1"
        );

        userService.create(userRegistrationRequestDto);

        Assertions.assertTrue(userRepository.findByUsername("user1").isPresent());
    }

    @Test
    public void testCreatingExistingUser() {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto(
                "user2",
                "pass2"
        );

        userService.create(userRegistrationRequestDto);

        Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.create(userRegistrationRequestDto)
        );
    }

}
