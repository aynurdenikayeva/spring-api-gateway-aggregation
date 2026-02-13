package com.aynur.file_service.repository;

import com.aynur.file_service.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {
}