package com.damvih.storage.dto;

import org.springframework.web.multipart.MultipartFile;

public record UploadResourceRequestDto(

        String path,
        MultipartFile[] files

) {
}
