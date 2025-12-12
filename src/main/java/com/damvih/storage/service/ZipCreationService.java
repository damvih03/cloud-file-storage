package com.damvih.storage.service;

import com.damvih.storage.entity.PathComponents;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ZipCreationService {

    private final MinioRepository minioRepository;

    public byte[] createZip(PathComponents pathComponents, List<String> objectNames) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(stream);

            for (PathComponents objectPathComponents: PathComponentsBuilder.buildByFullPaths(objectNames)) {
                addToZip(objectPathComponents, pathComponents, zipOutputStream);
            }

            zipOutputStream.close();

            return stream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void addToZip(PathComponents objectPathComponents, PathComponents relativePathComponents,ZipOutputStream zipOutputStream) throws Exception {
        String relativePath = buildRelativePath(relativePathComponents, objectPathComponents);
        ZipEntry zipEntry = new ZipEntry(relativePath);
        zipOutputStream.putNextEntry(zipEntry);
        if (!objectPathComponents.isResourceDirectory()) {
            byte[] objectData = minioRepository.getObjectData(objectPathComponents.getFull());
            zipOutputStream.write(objectData);
        }
        zipOutputStream.closeEntry();
    }

    private String buildRelativePath(PathComponents main, PathComponents object) {
        return object.getWithoutRootDirectory()
                .substring(main.getParentDirectory().length());
    }

}
