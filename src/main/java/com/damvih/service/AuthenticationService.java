package com.damvih.service;

import com.damvih.dto.UserLoginRequestDto;
import com.damvih.dto.UserRegistrationRequestDto;
import com.damvih.entity.User;
import com.damvih.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public Authentication signUp(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userService.create(userRegistrationRequestDto);
        return authenticateCreatedUser(user);
    }

    public Authentication signIn(UserLoginRequestDto userLoginRequestDto) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword()
        );

        return authenticationManager.authenticate(authRequest);
    }

    private Authentication authenticateCreatedUser(User user) {
        UserDetails userDetails = new UserDetailsImpl(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

}
