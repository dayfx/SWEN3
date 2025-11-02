package com.fhtechnikum.paperless.messaging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for document processing messages sent via RabbitMQ.
 * This message is sent when a document is uploaded and needs OCR processing.
 */
public class DocumentMessage {

    private Long documentId;
    private String filename;
    private String minioObjectKey;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public DocumentMessage() {
    }

    public DocumentMessage(Long documentId, String filename, String minioObjectKey) {
        this.documentId = documentId;
        this.filename = filename;
        this.minioObjectKey = minioObjectKey;
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

    public String getMinioObjectKey() {
        return minioObjectKey;
    }

    public void setMinioObjectKey(String minioObjectKey) {
        this.minioObjectKey = minioObjectKey;
    }

    @Override
    public String toString() {
        return "DocumentMessage{" +
                "documentId=" + documentId +
                ", filename='" + filename + '\'' +
                ", minioObjectKey='" + minioObjectKey + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
