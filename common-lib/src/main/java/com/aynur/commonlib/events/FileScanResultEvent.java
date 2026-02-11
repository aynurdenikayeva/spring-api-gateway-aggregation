package com.aynur.commonlib.events;

public record FileScanResultEvent(Long fileId, String status, String signature) {}