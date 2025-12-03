package com.damvih.service;

import com.damvih.dto.UserDto;
import com.damvih.dto.UserLoginRequestDto;
import com.damvih.dto.UserRegistrationRequestDto;
import com.damvih.mapper.UserMapper;
import com.damvih.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;

    public Authentication signUp(UserRegistrationRequestDto userRegistrationRequestDto) {
        UserDto userDto = userService.create(userRegistrationRequestDto);
        return createAuthentication(userDto);
    }

    public Authentication signIn(UserLoginRequestDto userLoginRequestDto) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authRequest);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        UserDto userDto = userMapper.toDto(userDetailsImpl.getUser());

        return createAuthentication(userDto);
    }

    private Authentication createAuthentication(UserDto userDto) {
        return new UsernamePasswordAuthenticationToken(
                userDto, null, List.of()
        );
    }

}
