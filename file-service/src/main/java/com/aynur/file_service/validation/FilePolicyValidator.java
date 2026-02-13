package com.aynur.file_service.validation;

import com.aynur.file_service.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FilePolicyValidator {

    @Value("${app.files.max-size-bytes:10485760}") // default 10MB
    private long maxSizeBytes;

    // sadə allow-list (istəsən application.yml-dən oxudarıq)
    private final Set<String> allowedTypes = Set.of(
            "image/png", "image/jpeg", "application/pdf", "text/plain"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new BadRequestException("File is too large. Max bytes: " + maxSizeBytes);
        }
        String type = file.getContentType();
        if (type == null || !allowedTypes.contains(type)) {
            throw new BadRequestException("File type not allowed: " + type);
        }
    }
}