package com.fhtechnikum.paperlessservices.messaging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for document processing messages received via RabbitMQ.
 * This message is sent when a document is uploaded and needs OCR processing.
 *
 * This class must match the DocumentMessage in PaperlessREST exactly
 */
public class DocumentMessage {

    private Long documentId;
    private String filename;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public DocumentMessage() {
    }

    public DocumentMessage(Long documentId, String filename) {
        this.documentId = documentId;
        this.filename = filename;
        this.timestamp = LocalDateTime.now();
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DocumentMessage{" +
                "documentId=" + documentId +
                ", filename='" + filename + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
