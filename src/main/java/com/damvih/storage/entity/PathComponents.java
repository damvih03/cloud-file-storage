package com.damvih.storage.entity;

import com.damvih.storage.dto.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public boolean isResourceDirectory() {
        return resourceType == ResourceType.DIRECTORY;
    }

}
