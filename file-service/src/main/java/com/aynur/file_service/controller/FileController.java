package com.aynur.file_service.controller;

import com.aynur.file_service.entity.StoredFile;
import com.aynur.file_service.service.DownloadService;
import com.aynur.file_service.service.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileUploadService fileUploadService;
    private final DownloadService downloadService;

    public FileController(FileUploadService fileUploadService,
                          DownloadService downloadService) {
        this.fileUploadService = fileUploadService;
        this.downloadService = downloadService;
    }

    // 1) Upload
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> upload(@RequestPart("file") MultipartFile file) {
        StoredFile saved = fileUploadService.upload(file);
        return ResponseEntity.ok(FileResponse.from(saved));
    }

    // 2) Status
    @GetMapping("/{id}/status")
    public ResponseEntity<FileResponse> status(@PathVariable UUID id) {
        StoredFile f = fileUploadService.get(id);
        return ResponseEntity.ok(FileResponse.from(f));
    }

    // 3) Download (yalnız CLEAN)
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        StoredFile meta = fileUploadService.get(id);
        Resource resource = downloadService.downloadIfClean(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + meta.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .contentLength(meta.getSize())
                .body(resource);
    }

    // Sadə response DTO (package-ləri pozmamaq üçün burda saxladım)
    public static class FileResponse {
        private String id;
        private String originalFilename;
        private String contentType;
        private long size;
        private String status;

        public static FileResponse from(StoredFile f) {
            FileResponse r = new FileResponse();
            r.id = f.getId().toString();
            r.originalFilename = f.getOriginalFilename();
            r.contentType = f.getContentType();
            r.size = f.getSize();
            r.status = f.getStatus().name();
            return r;
        }

        public String getId() { return id; }
        public String getOriginalFilename() { return originalFilename; }
        public String getContentType() { return contentType; }
        public long getSize() { return size; }
        public String getStatus() { return status; }
    }
}
