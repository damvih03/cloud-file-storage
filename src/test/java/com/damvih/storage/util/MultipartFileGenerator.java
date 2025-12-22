package com.damvih.storage.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@UtilityClass
public class MultipartFileGenerator {

    public MultipartFile[] generate(int size) {
        MultipartFile[] files = new MultipartFile[size];

        for (int i = 0; i < size; i++) {
            files[i] = new MockMultipartFile(
                    "object",
                    UUID.randomUUID() + ".txt",
                    "text/plain",
                    UUID.randomUUID().toString().getBytes()
            );
        }

        return files;
    }

    @SneakyThrows
    public MultipartFile[] putEachToUniqueDirectories(MultipartFile[] files) {
        for (int i = 0; i < files.length; i++) {
            files[i] = new MockMultipartFile(
                    "object",
                    UUID.randomUUID() + "/" + files[i].getOriginalFilename(),
                    "text/plain",
                    files[i].getBytes()
            );
        }

        return files;
    }

}
