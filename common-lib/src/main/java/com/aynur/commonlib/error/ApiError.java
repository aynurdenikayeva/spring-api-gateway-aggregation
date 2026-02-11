package com.aynur.commonlib.error;
import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String message,
        ErrorCode code,
        String path,
        String correlationId
) {
    public static ApiError of(int status, String message, ErrorCode code, String path, String correlationId) {
        return new ApiError(Instant.now(), status, message, code, path, correlationId);
    }
}