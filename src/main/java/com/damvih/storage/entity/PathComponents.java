package com.damvih.storage.entity;

import com.damvih.authentication.dto.UserDto;
import com.damvih.storage.dto.ResourceType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class PathComponents {

    private String rootDirectory;
    private String parentDirectory;
    private String resourceName;
    private ResourceType resourceType;

    public PathComponents(String path, UserDto userDto) {
        List<String> pathParts = Arrays.asList(path.split("/"));

        setResourceType(path);
        setRootDirectory(userDto);
        setParentDirectory(pathParts);
        setResourceName(pathParts);
    }

    public String getFull() {
        String fullPath = getRootDirectory() + getParentDirectory() + getResourceName();
        if (isResourceDirectory()) {
            fullPath += "/";
        }
        return fullPath;
    }

    public String getFullParentDirectory() {
        return getRootDirectory() + getParentDirectory();
    }

    public boolean isResourceDirectory() {
        return resourceType == ResourceType.DIRECTORY;
    }

    private void setRootDirectory(UserDto userDto) {
        this.rootDirectory = String.format("user-%s-files/", userDto.getId());
    }

    private void setParentDirectory(List<String> parts) {
        int pathPartsSize = parts.size() - 1;
        List<String> pathParts = parts.subList(0, pathPartsSize);

        String parentPath = String.join("/", pathParts);

        if (!pathParts.isEmpty()) {
            parentPath += "/";
        }

        this.parentDirectory = parentPath;
    }

    private void setResourceName(List<String> parts) {
        this.resourceName = parts.get(parts.size() - 1);
    }

    private void setResourceType(String path) {
        this.resourceType = path.endsWith("/") ? ResourceType.DIRECTORY : ResourceType.FILE;
    }

}
