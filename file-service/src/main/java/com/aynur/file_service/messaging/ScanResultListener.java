package com.aynur.file_service.messaging;


import com.aynur.file_service.entity.FileStatus;
import com.aynur.file_service.entity.ScanLog;
import com.aynur.file_service.entity.StoredFile;
import com.aynur.file_service.repository.ScanLogRepository;
import com.aynur.file_service.repository.StoredFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ScanResultListener {
    private final StoredFileRepository storedFileRepository;
    private final ScanLogRepository scanLogRepository;

    public ScanResultListener(StoredFileRepository storedFileRepository,
                              ScanLogRepository scanLogRepository) {
        this.storedFileRepository = storedFileRepository;
        this.scanLogRepository = scanLogRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.SCAN_RESULT_QUEUE)
    @Transactional
    public void onScanResult(ScanResultEvent event) {
        StoredFile f = storedFileRepository.findById(event.getFileId())
                .orElse(null);

        if (f != null) {
            FileStatus st = event.getStatus() == null ? FileStatus.ERROR : event.getStatus();
            f.setStatus(st);
            storedFileRepository.save(f);
        }

        ScanLog log = new ScanLog();
        log.setFileId(event.getFileId());
        log.setResultStatus(event.getStatus() == null ? FileStatus.ERROR : event.getStatus());
        log.setMessage(event.getMessage());
        scanLogRepository.save(log);
    }
}
