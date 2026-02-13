package com.aynur.file_service.service;

import com.aynur.file_service.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    private final Path root;

    public LocalStorageService(@Value("${app.storage.root:./data/uploads}") String rootDir) {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new StorageException("Cannot create storage directory: " + this.root, e);
        }
    }
    @Override
    public String save(UUID fileId, MultipartFile file) {
        String safeName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        Path dest = root.resolve(fileId.toString() + "-" + safeName);

        try {
            // overwrite etməsin deyə CREATE_NEW
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }
    @Override
    public Resource loadAsResource(String storagePath) {
        Path p = Paths.get(storagePath);
        if (!Files.exists(p)) {
            throw new StorageException("File not found on disk");
        }
        return new FileSystemResource(p);
    }
    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(Paths.get(storagePath));
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }
}
