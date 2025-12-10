package com.damvih.storage.util;

import com.damvih.storage.exception.InvalidPathException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathValidator {

    public void validate(String path) {
        if (path.isBlank()) {
            throw new InvalidPathException("Path is blank.");
        }

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

}
