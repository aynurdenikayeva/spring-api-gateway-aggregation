package com.aynur.commonlib.error;

public enum ErrorCode {
    VALIDATION_ERROR,
    NOT_FOUND,
    UNAUTHORIZED,
    FORBIDDEN,
    // gateway / network
    SERVICE_UNAVAILABLE,
    BAD_GATEWAY,
    TIMEOUT,
    // file/scan
    FILE_INFECTED,
    SCAN_FAILED,
    STORAGE_FAILED
}