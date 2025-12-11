package com.damvih.storage.util;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceType;
import com.damvih.storage.entity.PathComponents;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PathComponentsBuilder {

    public PathComponents build(String path, UserDto userDto) {
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

    private String getRootDirectory(UserDto userDto) {
        return String.format("user-%s-files/", userDto.getId());
    }

    private String extractParentDirectory(String path) {
        if (path.equals("/")) {
            return "";
        }

        List<String> pathParts = Arrays.asList(path.split("/"));
        List<String> parentDirectoryParts = pathParts.subList(0, pathParts.size() - 1);

        String parentPath = String.join("/", parentDirectoryParts);

        if (!parentDirectoryParts.isEmpty()) {
            parentPath += "/";
        }

        return parentPath;
    }

    private String extractResourceName(String path) {
        List<String> pathParts = Arrays.asList(path.split("/"));
        if (pathParts.isEmpty()) {
            return "";
        }
        return pathParts.get(pathParts.size() - 1);
    }

    private ResourceType getResourceType(String path) {
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
