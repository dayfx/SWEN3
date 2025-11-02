package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.messaging.DocumentMessageProducer;
import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.FileStorage;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/msword",  // .doc
        "text/plain"
    );

    private final DocumentRepository documentRepository;
    private final DocumentMessageProducer messageProducer;
    private final FileStorage fileStorage;

    // constructor injection for all dependencies
    public DocumentServiceImpl(DocumentRepository documentRepository,
                              DocumentMessageProducer messageProducer,
                              FileStorage fileStorage) {
        this.documentRepository = documentRepository;
        this.messageProducer = messageProducer;
        this.fileStorage = fileStorage;
    }

    @Override
    public DocumentEntity createDocument(DocumentEntity entity) {
        // Set upload date (business logic)
        entity.setUploadDate(LocalDateTime.now());
        DocumentEntity saved = documentRepository.save(entity);

        // Send RabbitMQ message for OCR processing
        // Note: For non-upload creates, minioObjectKey might be null
        messageProducer.sendOcrMessage(saved.getId(), saved.getOriginalFilename(), saved.getMinioObjectKey());

        // Return entity
        return saved;
    }

    @Override
    public DocumentEntity uploadDocument(MultipartFile file, String title, String author) throws IOException {
        log.info("Starting file upload - filename: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());

        // 1. Validate file is not empty
        if (file.isEmpty()) {
            log.error("File upload failed: file is empty");
            throw new IllegalArgumentException("File is empty");
        }

        // 2. Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("File upload failed: file too large ({} bytes, max {} bytes)", file.getSize(), MAX_FILE_SIZE);
            throw new IllegalArgumentException("File too large. Maximum size is 10MB");
        }

        // 3. Validate MIME type
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            log.error("File upload failed: unsupported MIME type: {}", mimeType);
            throw new IllegalArgumentException("Unsupported file type: " + mimeType + ". Allowed types: PDF, DOCX, DOC, TXT");
        }

        // 4. Extract metadata
        String originalFilename = file.getOriginalFilename();
        long fileSize = file.getSize();
        byte[] fileData = file.getBytes();

        // 5. Generate unique object key for MinIO (UUID + filename)
        String objectKey = UUID.randomUUID().toString() + "_" + originalFilename;

        // 6. Upload file to MinIO
        fileStorage.upload(objectKey, fileData);
        log.info("File uploaded to MinIO with key: {}", objectKey);

        // 7. Create entity with MinIO reference
        DocumentEntity entity = new DocumentEntity();
        entity.setTitle(title != null && !title.trim().isEmpty() ? title : originalFilename);
        entity.setAuthor(author != null && !author.trim().isEmpty() ? author : "Unknown");
        entity.setOriginalFilename(originalFilename);
        entity.setMimeType(mimeType);
        entity.setFileSize(fileSize);
        entity.setMinioObjectKey(objectKey);  // Store MinIO key instead of file data
        entity.setUploadDate(LocalDateTime.now());

        // 8. Save to database
        DocumentEntity saved = documentRepository.save(entity);
        log.info("Document saved successfully - ID: {}, filename: {}", saved.getId(), saved.getOriginalFilename());

        // 9. Send RabbitMQ message for OCR processing with MinIO key
        messageProducer.sendOcrMessage(saved.getId(), saved.getOriginalFilename(), saved.getMinioObjectKey());

        // 10. Return entity (controller will handle DTO mapping)
        return saved;
    }

    @Override
    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Optional<DocumentEntity> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public DocumentEntity updateDocument(Long id, String title, String author, String content) {
        return documentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(title);
                    existing.setAuthor(author);
                    existing.setContent(content);
                    return documentRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteDocument(Long id) {
        // First, retrieve the document to get MinIO key
        Optional<DocumentEntity> documentOpt = documentRepository.findById(id);

        if (documentOpt.isEmpty()) {
            log.warn("Document with ID {} not found for deletion", id);
            return false;
        }

        DocumentEntity document = documentOpt.get();

        // Delete from MinIO if the file exists
        if (document.getMinioObjectKey() != null && !document.getMinioObjectKey().trim().isEmpty()) {
            try {
                fileStorage.delete(document.getMinioObjectKey());
                log.info("Deleted file from MinIO for document ID {}: {}", id, document.getMinioObjectKey());
            } catch (Exception e) {
                log.error("Failed to delete file from MinIO for document ID {}: {}", id, e.getMessage(), e);
                // Continue with database deletion even if MinIO deletion fails
                // (file might already be deleted or MinIO might be temporarily unavailable)
            }
        } else {
            log.warn("Document ID {} has no MinIO object key, skipping MinIO deletion", id);
        }

        // Delete from database
        documentRepository.deleteById(id);
        log.info("Deleted document from database: ID {}", id);

        return true;
    }
}