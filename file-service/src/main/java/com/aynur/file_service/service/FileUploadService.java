package com.aynur.file_service.service;

import com.aynur.file_service.entity.FileStatus;
import com.aynur.file_service.entity.StoredFile;
import com.aynur.file_service.exception.NotFoundException;
import com.aynur.file_service.messaging.ScanJobPublisher;
import com.aynur.file_service.repository.StoredFileRepository;
import com.aynur.file_service.security.CurrentUserProvider;
import com.aynur.file_service.validation.FilePolicyValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileUploadService {

    private final StoredFileRepository storedFileRepository;
    private final StorageService storageService;
    private final FilePolicyValidator validator;
    private final ScanJobPublisher scanJobPublisher;
    private final CurrentUserProvider currentUserProvider;

    public FileUploadService(StoredFileRepository storedFileRepository,
                             StorageService storageService,
                             FilePolicyValidator validator,
                             ScanJobPublisher scanJobPublisher,
                             CurrentUserProvider currentUserProvider) {
        this.storedFileRepository = storedFileRepository;
        this.storageService = storageService;
        this.validator = validator;
        this.scanJobPublisher = scanJobPublisher;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public StoredFile upload(MultipartFile file) {
        validator.validate(file);

        StoredFile entity = new StoredFile();

        // ID-ni özün set etmə! Hibernate yaradacaq.
        entity.setOriginalFilename(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        entity.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        entity.setSize(file.getSize());
        entity.setStatus(FileStatus.PENDING);

        Long userId = currentUserProvider.getCurrentUserIdOrNull();
        entity.setOwnerId(userId);

        // 1) Əvvəl DB-yə save et ki, UUID yaransın
        storedFileRepository.saveAndFlush(entity);

        UUID id = entity.getId(); // artıq var ✅

        // 2) Diskə yaz
        String storagePath = storageService.save(id, file);
        entity.setStoragePath(storagePath);

        // 3) DB-də storagePath update
        storedFileRepository.save(entity);

        // 4) Scan job publish
        scanJobPublisher.publishScanJob(entity.getId(), entity.getStoragePath(), entity.getContentType(), entity.getSize());

        return entity;
    }

    @Transactional(readOnly = true)
    public StoredFile get(UUID id) {
        return storedFileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File not found: " + id));
    }
}
