package com.damvih.storage.listener;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserRegistrationRequestDto;
import com.damvih.authentication.entity.User;
import com.damvih.authentication.mapper.UserMapper;
import com.damvih.authentication.repository.UserRepository;
import com.damvih.authentication.service.UserService;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import com.damvih.storage.util.StorageTestcontainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import({UserCreatedListener.class, UserService.class, StorageRepository.class})
@Slf4j
public class UserCreatedListenerTest extends StorageTestcontainer {

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    @DisplayName("Should create root directory for user after creation")
    public void testCorrectCreationUserRootDirectory() {
        UserDto userDto = new UserDto(2L, "CreatedUser");
        PathComponents path = PathComponentsBuilder.build("", userDto);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());
        Mockito.when(userMapper.toDto(Mockito.any(User.class))).thenReturn(userDto);

        userService.create(new UserRegistrationRequestDto());

        Assertions.assertEquals(path.getRootDirectory(), path.getFull());
        Assertions.assertTrue(storageRepository.isObjectExists(path.getRootDirectory()));
    }

}
