package com.fhtechnikum.paperlessservices.services;

import com.fhtechnikum.paperlessservices.messaging.dto.DocumentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * OCR Worker Service that consumes document processing messages from RabbitMQ.
 *
 * Sprint 3: This worker only logs received messages.
 */
@Component
public class OcrWorkerService {

    private static final Logger log = LoggerFactory.getLogger(OcrWorkerService.class);

    /**
     * Listens to the OCR queue and processes incoming document messages.
     * Currently just logs the message.
     *
     * @param message The document message containing documentId and filename
     */
    @RabbitListener(queues = "ocr-queue")
    public void processOcrMessage(DocumentMessage message) {
        log.info("OCR Worker - Message Received");
        log.info("Document ID: {}", String.format("%-42s", message.getDocumentId()));
        log.info("Filename:    {}", String.format("%-42s", message.getFilename()));
        log.info("Timestamp:   {}", String.format("%-42s", message.getTimestamp()));
    }
}
