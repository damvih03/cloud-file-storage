package com.damvih.controller;

import com.damvih.dto.UserDto;
import com.damvih.mapper.UserMapper;
import com.damvih.security.UserDetailsImpl;
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
    public ResponseEntity<UserDto> get(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDto userDto = userMapper.toDto(userDetails.getUser());
        return ResponseEntity.ok(userDto);
    }

}
