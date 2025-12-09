package com.damvih.authentication.controller;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserResponseDto;
import com.damvih.authentication.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDto> get(@AuthenticationPrincipal UserDto userDto) {
        UserResponseDto userResponseDto = userMapper.toResponseDto(userDto);
        return ResponseEntity.ok(userResponseDto);
    }

}
