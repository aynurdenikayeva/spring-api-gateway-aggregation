package com.aynur.file_service.repository;


import com.aynur.file_service.entity.ScanLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanLogRepository extends JpaRepository<ScanLog, Long> {
}