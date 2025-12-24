package com.damvih.storage.util;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceType;
import com.damvih.storage.service.PathComponents;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PathComponentsBuilder {

    public PathComponents build(String path, UserDto userDto) {
        path = path.trim();
        return new PathComponents(
                getRootDirectory(userDto),
                extractParentDirectory(path),
                extractResourceName(path),
                getResourceType(path)
        );
    }

    public PathComponents buildByFullPath(String fullPath) {
        return new PathComponents(
                extractRootDirectoryFromFullPath(fullPath),
                extractParentDirectoryFromFullPath(fullPath),
                extractResourceName(fullPath),
                getResourceType(fullPath)
        );
    }

    public List<PathComponents> buildByFullPaths(List<String> fullPaths) {
        return fullPaths.stream()
                .map(PathComponentsBuilder::buildByFullPath)
                .toList();
    }

    private String getRootDirectory(UserDto userDto) {
        return String.format("user-%s-files/", userDto.getId());
    }

    private String extractParentDirectory(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        int lastSlash = path.lastIndexOf("/");
        if (lastSlash <= 0) {
            return "";
        }
        return path.substring(0, lastSlash + 1);
    }

    private String extractResourceName(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        int lastSlash = path.lastIndexOf("/");
        if (lastSlash <= 0) {
            return path;
        }
        return path.substring(lastSlash + 1);
    }

    private ResourceType getResourceType(String path) {
        if (path.isBlank()) {
            return ResourceType.DIRECTORY;
        }
        return path.endsWith("/") ? ResourceType.DIRECTORY : ResourceType.FILE;
    }

    private String extractParentDirectoryFromFullPath(String fullPath) {
        String pathWithoutRootDirectory = fullPath.substring(fullPath.indexOf("/") + 1);
        return extractParentDirectory(pathWithoutRootDirectory);
    }

    private String extractRootDirectoryFromFullPath(String fullPath) {
        return fullPath.substring(0, fullPath.indexOf("/") + 1);
    }

}
