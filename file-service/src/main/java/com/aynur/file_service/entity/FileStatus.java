package com.aynur.file_service.entity;

public enum FileStatus {
    PENDING,     // upload olundu, scan gözləyir
    SCANNING,    // scanner işləyir
    CLEAN,       // təhlükəsiz
    INFECTED,    // virus tapılıb
    ERROR        // scan/storage problemi
}