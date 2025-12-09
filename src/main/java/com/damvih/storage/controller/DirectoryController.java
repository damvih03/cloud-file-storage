package com.damvih.storage.controller;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.util.PathValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        directoryService.create(path, userDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
