package com.damvih.service;

import com.damvih.dto.UserDto;
import com.damvih.mapper.UserMapper;
import com.damvih.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationStateHandlerService {

    private final UserMapper userMapper;

    public UserDto onAuthenticated(Supplier<Authentication> supplier, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = supplier.get();
        perform(authentication, request, response);
        return (UserDto) authentication.getPrincipal();
    }

    protected void perform(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

}
