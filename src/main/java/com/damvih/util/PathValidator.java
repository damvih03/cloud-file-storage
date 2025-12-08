package com.damvih.util;

import com.damvih.exception.InvalidPathException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathValidator {

    public void validate(String path) {
        if (path.isBlank()) {
            throw new InvalidPathException("Path is blank.");
        }

        if (path.startsWith("/")) {
            throw new InvalidPathException("Path can not start with a slash.");
        }

        if (path.contains("//")) {
            throw new InvalidPathException("Path can not contain multiple slashes.");
        }
    }

}
