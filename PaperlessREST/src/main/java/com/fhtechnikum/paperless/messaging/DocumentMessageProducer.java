package com.fhtechnikum.paperless.messaging;

import com.fhtechnikum.paperless.config.RabbitMQConfig;
import com.fhtechnikum.paperless.messaging.dto.DocumentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer service for sending document processing messages to RabbitMQ.
 * This service sends messages to the OCR queue when documents are uploaded.
 */
@Service
public class DocumentMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public DocumentMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Send a message to the OCR queue for document processing.
     *
     * @param documentId The ID of the uploaded document
     * @param filename   The filename of the uploaded document
     */
    public void sendOcrMessage(Long documentId, String filename) {
        DocumentMessage message = new DocumentMessage(documentId, filename);

        rabbitTemplate.convertAndSend(RabbitMQConfig.OCR_QUEUE_NAME, message);

        log.info("Sent OCR message to queue for document ID: {} (filename: {})", documentId, filename);
    }
}
