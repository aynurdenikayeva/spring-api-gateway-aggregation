package com.aynur.file_service.messaging;

import com.aynur.file_service.entity.FileStatus;

import java.util.UUID;

public class ScanResultEvent {
    private UUID fileId;
    private FileStatus status; // CLEAN / INFECTED / ERROR
    private String message;

    public ScanResultEvent() {}

    public ScanResultEvent(UUID fileId, FileStatus status, String message) {
        this.fileId = fileId;
        this.status = status;
        this.message = message;
    }

    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }

    public FileStatus getStatus() { return status; }
    public void setStatus(FileStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
