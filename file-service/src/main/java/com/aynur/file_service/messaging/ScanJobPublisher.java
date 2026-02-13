package com.aynur.file_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScanJobPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ScanJobPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishScanJob(UUID fileId, String storagePath, String contentType, long size) {
        ScanJobEvent event = new ScanJobEvent(fileId, storagePath, contentType, size);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SCAN_EXCHANGE,
                RabbitMQConfig.SCAN_ROUTING_KEY,
                event
        );
    }
}
