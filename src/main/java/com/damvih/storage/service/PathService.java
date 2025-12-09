package com.damvih.storage.service;

import com.damvih.authentication.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {

    public String getResourceName(List<String> parts) {
        return parts.get(parts.size() - 1);
    }

    public String getParentPath(List<String> parts) {
        int pathPartsSize = parts.size() - 1;
        List<String> pathParts = parts.subList(0, pathPartsSize);

        String parentPath = String.join("/", pathParts);
        if (pathPartsSize <= 1) {
            parentPath += "/";
        }

        return parentPath;
    }

    public String getFull(String path, UserDto userDto) {
        return getRoot(userDto) + "/" + path;
    }

    public boolean isDirectory(String path) {
        return path.endsWith("/");
    }

    private String getRoot(UserDto userDto) {
        return String.format("user-%s-files", userDto.getId());
    }

}
