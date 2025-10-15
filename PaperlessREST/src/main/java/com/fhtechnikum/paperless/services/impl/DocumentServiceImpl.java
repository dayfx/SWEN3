package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.messaging.DocumentMessageProducer;
import com.fhtechnikum.paperless.services.mapper.DocumentMapper;
import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.dto.Document;
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
import java.util.stream.Collectors;

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
    private final DocumentMapper documentMapper;
    private final DocumentMessageProducer messageProducer;

    // constructor injection for all dependencies
    public DocumentServiceImpl(DocumentRepository documentRepository,
                              DocumentMapper documentMapper,
                              DocumentMessageProducer messageProducer) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
        this.messageProducer = messageProducer;
    }

    @Override
    public Document createDocument(Document document) {
        // mapper to convert DTO to Entity
        DocumentEntity entity = documentMapper.toEntity(document);
        entity.setUploadDate(LocalDateTime.now());
        DocumentEntity saved = documentRepository.save(entity);

        // Send RabbitMQ message for OCR processing
        messageProducer.sendOcrMessage(saved.getId(), saved.getOriginalFilename());

        // mapper to convert the saved Entity back to a DTO
        return documentMapper.toDto(saved);
    }

    @Override
    public Document uploadDocument(MultipartFile file, String title, String author) throws IOException {
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

        // 5. Create entity
        DocumentEntity entity = new DocumentEntity();
        entity.setTitle(title != null && !title.trim().isEmpty() ? title : originalFilename);
        entity.setAuthor(author != null && !author.trim().isEmpty() ? author : "Unknown");
        entity.setOriginalFilename(originalFilename);
        entity.setMimeType(mimeType);
        entity.setFileSize(fileSize);
        entity.setFileData(fileData);
        entity.setUploadDate(LocalDateTime.now());

        // 6. Save to database
        DocumentEntity saved = documentRepository.save(entity);
        log.info("Document saved successfully - ID: {}, filename: {}", saved.getId(), saved.getOriginalFilename());

        // 7. Send RabbitMQ message for OCR processing
        messageProducer.sendOcrMessage(saved.getId(), saved.getOriginalFilename());

        // 8. Return DTO
        return documentMapper.toDto(saved);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(documentMapper::toDto);
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        return documentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(document.getTitle());
                    existing.setAuthor(document.getAuthor());
                    existing.setContent(document.getContent());
                    DocumentEntity updated = documentRepository.save(existing);
                    return documentMapper.toDto(updated); // Use the mapper
                })
                .orElse(null);
    }

    @Override
    public boolean deleteDocument(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}