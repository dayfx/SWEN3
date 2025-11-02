package com.fhtechnikum.paperlessservices.services;

import com.fhtechnikum.paperlessservices.messaging.dto.DocumentMessage;
import com.fhtechnikum.paperlessservices.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperlessservices.persistence.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OCR Worker Service that consumes document processing messages from RabbitMQ.
 *
 * Sprint 4: Performs actual OCR processing using Tesseract and updates the database.
 */
@Component
public class OcrWorkerService {

    private static final Logger log = LoggerFactory.getLogger(OcrWorkerService.class);

    private final MinIOService minioService;
    private final TesseractOcrService ocrService;
    private final DocumentRepository documentRepository;

    public OcrWorkerService(MinIOService minioService,
                           TesseractOcrService ocrService,
                           DocumentRepository documentRepository) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.documentRepository = documentRepository;
    }

    /**
     * Listens to the OCR queue and processes incoming document messages.
     * Downloads file from MinIO, performs OCR, and updates database with extracted text.
     *
     * @param message The document message containing documentId, filename, and minioObjectKey
     */
    @RabbitListener(queues = "ocr-queue")
    @Transactional
    public void processOcrMessage(DocumentMessage message) {
        log.info("=== OCR Worker - Message Received ===");
        log.info("Document ID: {}", message.getDocumentId());
        log.info("Filename:    {}", message.getFilename());
        log.info("MinIO Key:   {}", message.getMinioObjectKey());
        log.info("Timestamp:   {}", message.getTimestamp());

        try {
            // 1. Fetch document from database
            DocumentEntity document = documentRepository.findById(message.getDocumentId())
                    .orElseThrow(() -> new RuntimeException("Document not found: " + message.getDocumentId()));

            log.info("Found document in database: ID={}, Title={}", document.getId(), document.getTitle());

            // 2. Download file from MinIO
            log.info("Downloading file from MinIO...");
            byte[] fileData = minioService.downloadFile(message.getMinioObjectKey());
            log.info("Downloaded {} bytes from MinIO", fileData.length);

            // 3. Perform OCR
            log.info("Starting OCR processing for MIME type: {}", document.getMimeType());
            String extractedText = ocrService.performOcr(fileData, document.getMimeType());
            log.info("OCR completed. Extracted {} characters", extractedText.length());

            // 4. Update document with extracted text
            document.setContent(extractedText);
            documentRepository.save(document);
            log.info("Document content updated in database");

            log.info("=== OCR Processing Complete ===");
            log.info("Document ID: {}", document.getId());
            log.info("Text Preview: {}", extractedText.length() > 100
                    ? extractedText.substring(0, 100) + "..."
                    : extractedText);

        } catch (Exception e) {
            log.error("OCR processing failed for document ID: {}", message.getDocumentId(), e);
            log.error("Error details: {}", e.getMessage());

            // Update document with error message
            try {
                documentRepository.findById(message.getDocumentId()).ifPresent(doc -> {
                    doc.setContent("[OCR Processing Failed: " + e.getMessage() + "]");
                    documentRepository.save(doc);
                });
            } catch (Exception dbError) {
                log.error("Failed to update document with error message", dbError);
            }
        }
    }
}
