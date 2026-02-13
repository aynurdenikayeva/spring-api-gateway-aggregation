package com.aynur.file_service.service;

import com.aynur.file_service.entity.FileStatus;
import com.aynur.file_service.entity.StoredFile;
import com.aynur.file_service.exception.ForbiddenException;
import com.aynur.file_service.repository.StoredFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DownloadService {
    private final StoredFileRepository storedFileRepository;
    private final StorageService storageService;

    public DownloadService(StoredFileRepository storedFileRepository,
                           StorageService storageService) {
        this.storedFileRepository = storedFileRepository;
        this.storageService = storageService;
    }
    @Transactional(readOnly = true)
    public Resource downloadIfClean(UUID id) {
        StoredFile f = storedFileRepository.findById(id)
                .orElseThrow(() -> new com.aynur.file_service.exception.NotFoundException("File not found: " + id));

        if (f.getStatus() != FileStatus.CLEAN) {
            throw new ForbiddenException("File is not CLEAN. Current status: " + f.getStatus());
        }
        return storageService.loadAsResource(f.getStoragePath());
    }
}