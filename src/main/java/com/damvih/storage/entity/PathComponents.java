package com.damvih.storage.entity;

import com.damvih.storage.dto.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PathComponents {

    private String rootDirectory;
    private String parentDirectory;
    private String resourceName;
    private ResourceType resourceType;

    public String getFull() {
        String fullPath = getRootDirectory() + getParentDirectory() + getResourceName();
        if (parentDirectory.isBlank() && resourceName.isBlank()) {
            return fullPath;
        }

        if (isResourceDirectory()) {
            return fullPath + "/";
        }

        return fullPath;
    }

    public String getFullParentDirectory() {
        return getRootDirectory() + getParentDirectory();
    }

    public List<String> getParentDirectoryNames() {
        if (parentDirectory.isBlank()) {
            return List.of();
        }

        List<String> names = new ArrayList<>();
        StringBuilder chain = new StringBuilder();

        for (String directory : parentDirectory.split("/")) {
            chain.append(directory).append("/");
            names.add(chain.toString());
        }

        return names;
    }

    public String getWithoutRootDirectory() {
        String pathWithoutRootDirectory = parentDirectory + resourceName;

        if (pathWithoutRootDirectory.isBlank()) {
            return pathWithoutRootDirectory;
        }

        return isResourceDirectory() ? pathWithoutRootDirectory + "/" : pathWithoutRootDirectory;
    }

    public boolean isResourceDirectory() {
        return resourceType == ResourceType.DIRECTORY;
    }

}
