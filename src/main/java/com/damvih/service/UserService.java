package com.damvih.service;

import com.damvih.dto.UserDto;
import com.damvih.dto.UserRegistrationRequestDto;
import com.damvih.entity.User;
import com.damvih.exception.UserAlreadyExistsException;
import com.damvih.mapper.UserMapper;
import com.damvih.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto create(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = new User();
        user.setUsername(userRegistrationRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));

        try {
            return userMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException exception) {
            throw new UserAlreadyExistsException("User (" + userRegistrationRequestDto.getUsername() + ") already exists.");
        }
    }

}
