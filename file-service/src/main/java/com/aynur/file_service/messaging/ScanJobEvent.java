package com.aynur.file_service.messaging;

import java.util.UUID;

public record ScanJobEvent(
        UUID fileId,
        String storagePath,
        String contentType,
        long size
) {}
