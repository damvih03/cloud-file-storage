package com.damvih.authentication.controller;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserRegistrationRequestDto;
import com.damvih.authentication.dto.UserLoginRequestDto;
import com.damvih.authentication.dto.UserResponseDto;
import com.damvih.authentication.mapper.UserMapper;
import com.damvih.authentication.service.AuthenticationService;
import com.damvih.authentication.service.AuthenticationStateHandlerService;
import com.damvih.authentication.docs.SignInDocs;
import com.damvih.authentication.docs.SignOutDocs;
import com.damvih.authentication.docs.SignUpDocs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationStateHandlerService authenticationStateHandlerService;
    private final UserMapper userMapper;

    @SignUpDocs
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody @Valid UserRegistrationRequestDto userRegistrationRequestDto, HttpServletRequest request, HttpServletResponse response) {
        UserDto userDto = authenticationStateHandlerService.onAuthenticated(
                () -> authenticationService.signUp(userRegistrationRequestDto),
                request, response
        );
        return new ResponseEntity<>(userMapper.toResponseDto(userDto), HttpStatus.CREATED);
    }

    @SignInDocs
    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> signIn(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        UserDto userDto = authenticationStateHandlerService.onAuthenticated(
                () -> authenticationService.signIn(userLoginRequestDto),
                request, response
        );
        log.info("UserID '{}' entered.", userDto.getId());
        return new ResponseEntity<>(userMapper.toResponseDto(userDto), HttpStatus.OK);
    }

    @SignOutDocs
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
        return ResponseEntity.noContent().build();
    }

}
