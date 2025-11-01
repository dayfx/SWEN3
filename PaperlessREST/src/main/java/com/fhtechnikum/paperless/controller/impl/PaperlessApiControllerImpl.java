package com.fhtechnikum.paperless.controller.impl;

import com.fhtechnikum.paperless.controller.PaperlessApi;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller implementation.
 * Handles HTTP requests/responses and DTO-Entity mapping.
 * Business logic is delegated to the service layer.
 */
@RestController
public class PaperlessApiControllerImpl implements PaperlessApi {

    private static final Logger log = LoggerFactory.getLogger(PaperlessApiControllerImpl.class);

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public ResponseEntity<Document> uploadDocument(MultipartFile file, String title, String author) {
        try {
            log.info("Received upload request - file: {}, title: {}, author: {}",
                file.getOriginalFilename(), title, author);

            // Service returns entity
            DocumentEntity uploadedEntity = documentService.uploadDocument(file, title, author);

            // Controller handles DTO mapping (presentation layer concern)
            Document uploadedDto = documentMapper.toDto(uploadedEntity);

            return ResponseEntity.status(201).body(uploadedDto);
        } catch (IllegalArgumentException e) {
            log.error("Upload validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Upload IO error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        } catch (DataAccessException e) {
            // Database connection failure or constraint violation
            log.error("Database error during upload: {}", e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
    }

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        try {
            // Service returns entities
            List<DocumentEntity> entities = documentService.getAllDocuments();

            // Controller handles DTO mapping
            List<Document> documents = entities.stream()
                    .map(documentMapper::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(documents);
        } catch (DataAccessException e) {
            log.error("Database error while fetching documents: {}", e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
    }

    @Override
    public ResponseEntity<Document> getDocument(Long id) {
        try {
            // Service returns entity
            Optional<DocumentEntity> entityOptional = documentService.getDocumentById(id);

            // Controller handles DTO mapping
            return entityOptional
                    .map(documentMapper::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (DataAccessException e) {
            log.error("Database error while fetching document {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
    }

    @Override
    public ResponseEntity<Document> updateDocument(Long id, Document document) {
        try {
            // Controller handles DTO-to-Entity mapping (presentation concern)
            // Extract primitive values from DTO to pass to service
            String title = document.getTitle();
            String author = document.getAuthor();
            String content = document.getContent();

            // Service accepts primitives, returns entity
            DocumentEntity updated = documentService.updateDocument(id, title, author, content);

            if (updated != null) {
                // Controller handles Entity-to-DTO mapping
                Document updatedDto = documentMapper.toDto(updated);
                return ResponseEntity.ok(updatedDto);
            }
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e) {
            log.error("Database error while updating document {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
    }

    @Override
    public ResponseEntity<Void> deleteDocument(Long id) {
        try {
            boolean deleted = documentService.deleteDocument(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e) {
            log.error("Database error while deleting document {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        }
    }
}