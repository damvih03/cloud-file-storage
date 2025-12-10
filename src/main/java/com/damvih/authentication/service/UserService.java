package com.damvih.authentication.service;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserRegistrationRequestDto;
import com.damvih.authentication.entity.User;
import com.damvih.authentication.event.UserCreatedEvent;
import com.damvih.authentication.exception.UserAlreadyExistsException;
import com.damvih.authentication.mapper.UserMapper;
import com.damvih.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserDto create(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = new User();
        user.setUsername(userRegistrationRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));

        try {
            UserDto userDto = userMapper.toDto(userRepository.save(user));
            log.info("UserID '{}' with username '{}' created.", userDto.getId(), userDto.getUsername());
            applicationEventPublisher.publishEvent(new UserCreatedEvent(this, userDto));
            return userDto;
        } catch (DataIntegrityViolationException exception) {
            throw new UserAlreadyExistsException(
                    String.format("Attempt to create user with an existing username %s.", userRegistrationRequestDto.getUsername())
            );
        }
    }

}
