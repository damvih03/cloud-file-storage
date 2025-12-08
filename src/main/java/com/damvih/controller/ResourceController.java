package com.damvih.controller;

import com.damvih.dto.ResourceResponseDto;
import com.damvih.dto.UserDto;
import com.damvih.service.ResourceService;
import com.damvih.util.PathValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> get(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        ResourceResponseDto resourceResponseDto = resourceService.get(path, userDto);
        return new ResponseEntity<>(resourceResponseDto, HttpStatus.OK);
    }

}
