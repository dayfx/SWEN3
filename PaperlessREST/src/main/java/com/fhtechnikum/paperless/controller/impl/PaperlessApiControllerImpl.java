package com.fhtechnikum.paperless.controller.impl;

import com.fhtechnikum.paperless.controller.PaperlessApi;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class PaperlessApiControllerImpl implements PaperlessApi {

    private static final Logger log = LoggerFactory.getLogger(PaperlessApiControllerImpl.class);

    @Autowired
    private DocumentService documentService;

    @Override
    public ResponseEntity<Document> uploadDocument(MultipartFile file, String title, String author) {
        try {
            log.info("Received upload request - file: {}, title: {}, author: {}",
                file.getOriginalFilename(), title, author);
            Document uploaded = documentService.uploadDocument(file, title, author);
            return ResponseEntity.status(201).body(uploaded);
        } catch (IllegalArgumentException e) {
            log.error("Upload validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Upload IO error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @Override
    public ResponseEntity<Document> getDocument(Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Document> updateDocument(Long id, Document document) {
        Document updated = documentService.updateDocument(id, document);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteDocument(Long id) {
        boolean deleted = documentService.deleteDocument(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}