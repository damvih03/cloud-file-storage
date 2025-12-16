package com.damvih.storage.controller;

import com.damvih.storage.dto.ResourceResponseDto;
import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.UploadResourceRequestDto;
import com.damvih.storage.service.resource.ResourceService;
import com.damvih.storage.util.validation.PathValidator;
import com.damvih.storage.util.validation.QueryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        resourceService.delete(path, userDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(name = "path") String path, @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        byte[] data = resourceService.download(path, userDto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/move")
    public ResponseEntity<ResourceResponseDto> move(@RequestParam(name = "from") String from,
                                                    @RequestParam(name = "to") String to,
                                                    @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(from);
        PathValidator.validate(to);

        return new ResponseEntity<>(resourceService.move(from, to, userDto), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> search(@RequestParam(name = "query") String query, @AuthenticationPrincipal UserDto userDto) {
        QueryValidator.validate(query);
        return new ResponseEntity<>(resourceService.find(query, userDto), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceResponseDto>> upload(@RequestParam(name = "path") String path,
                                                            @RequestParam(name = "object") MultipartFile[] files,
                                                            @AuthenticationPrincipal UserDto userDto) {
        PathValidator.validate(path);
        return new ResponseEntity<>(resourceService.upload(new UploadResourceRequestDto(path, files), userDto), HttpStatus.CREATED);
    }

}
