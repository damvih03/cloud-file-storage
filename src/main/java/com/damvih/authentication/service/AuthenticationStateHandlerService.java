package com.damvih.authentication.service;

import com.damvih.authentication.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationStateHandlerService {

    private final SecurityContextRepository securityContextRepository;

    public UserDto onAuthenticated(Supplier<Authentication> supplier, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = supplier.get();
        perform(authentication, request, response);
        return (UserDto) authentication.getPrincipal();
    }

    protected void perform(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, request, response);
    }

}
