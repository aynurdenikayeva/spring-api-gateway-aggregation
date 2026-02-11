package com.aynur.commonlib.events;

public record FileUploadedEvent(Long fileId, String storagePath) {}
