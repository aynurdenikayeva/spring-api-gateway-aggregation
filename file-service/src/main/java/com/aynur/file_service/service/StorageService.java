package com.aynur.file_service.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {
    String save(UUID fileId, MultipartFile file);     // returns storagePath
    Resource loadAsResource(String storagePath);
    void delete(String storagePath);
}