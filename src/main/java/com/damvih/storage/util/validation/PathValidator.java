package com.damvih.storage.util.validation;

import com.damvih.storage.exception.InvalidPathException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class PathValidator {

    public void validate(String path) {
        if (path.matches(".*[|<>*?\\\\].*")) {
            throw new InvalidPathException("Path contains illegal characters.");
        }

        if (path.matches("^/.+")) {
            throw new InvalidPathException("Not empty path can not start with a slash.");
        }

        if (path.contains("//")) {
            throw new InvalidPathException("Path can not contain multiple slashes.");
        }
    }

    public void validateFileNames(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName.isBlank()) {
                throw new InvalidPathException("File name is empty.");
            }
            validate(fileName);
        }
    }

}
