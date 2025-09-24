package com.fhtechnikum.paperless.controller.impl;

import com.fhtechnikum.paperless.controller.PaperlessApi;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PaperlessApiControllerImpl implements PaperlessApi {

    @Autowired
    private DocumentService documentService;

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @Override
    public ResponseEntity<Document> createDocument(Document document) {
        Document created = documentService.createDocument(document);
        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<Document> getDocument(Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(doc -> ResponseEntity.ok(doc))
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