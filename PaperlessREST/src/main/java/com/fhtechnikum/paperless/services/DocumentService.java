package com.fhtechnikum.paperless.services;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.services.dto.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for document business logic.
 * Note: Service layer works with Entities (domain objects), not DTOs.
 * DTO conversion should be handled in the controller layer.
 */
public interface DocumentService {
    DocumentEntity createDocument(DocumentEntity entity);
    DocumentEntity uploadDocument(MultipartFile file, String title, String author) throws IOException;
    List<DocumentEntity> getAllDocuments();
    Optional<DocumentEntity> getDocumentById(Long id);
    DocumentEntity updateDocument(Long id, String title, String author, String content);
    boolean deleteDocument(Long id);
    List<DocumentEntity> searchDocuments(String query);
}

