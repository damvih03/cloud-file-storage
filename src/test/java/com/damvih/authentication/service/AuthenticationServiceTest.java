package com.damvih.authentication.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserRegistrationRequestDto;
import com.damvih.authentication.exception.UserAlreadyExistsException;
import com.damvih.authentication.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.4.7")
            .withUsername("test")
            .withPassword("test");

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:8.4.0-alpine")
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass test");

    @DynamicPropertySource
    public static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.name", mySqlContainer::getDatabaseName);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @DynamicPropertySource
    public static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.password", () -> "test");
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    @DisplayName("Should create user successfully")
    public void testCorrectCreatingUser() {
        UserRegistrationRequestDto  userRegistrationRequestDto = new UserRegistrationRequestDto(
                "user1",
                "pass1"
        );

        Authentication authentication = authenticationService.signUp(userRegistrationRequestDto);

        Assertions.assertTrue(userRepository.findByUsername("user1").isPresent());
        Assertions.assertTrue(authentication.isAuthenticated());

        Assertions.assertInstanceOf(UserDto.class, authentication.getPrincipal());

        UserDto userDto = (UserDto) authentication.getPrincipal();
        Assertions.assertEquals(userRegistrationRequestDto.username(), userDto.username());
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    public void testCreatingExistingUser() {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto(
                "user2",
                "pass2"
        );

        authenticationService.signUp(userRegistrationRequestDto);

        Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> authenticationService.signUp(userRegistrationRequestDto)
        );
    }

}
