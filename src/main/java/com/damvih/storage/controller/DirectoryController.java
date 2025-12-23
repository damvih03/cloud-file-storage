package com.damvih.storage.controller;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.docs.CreationDirectoryDocs;
import com.damvih.storage.docs.GettingDirectoryDocs;
import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.storage.service.DirectoryService;
import com.damvih.storage.util.validation.PathValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @CreationDirectoryDocs
    @PostMapping
    public ResponseEntity<ResourceResponseDto> create(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directoryService.create(path, userDto));
    }

    @GettingDirectoryDocs
    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> get(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        return new ResponseEntity<>(directoryService.get(path, userDto), HttpStatus.OK);
    }

}
