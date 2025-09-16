package com.fhtechnikum.paperless.controller.impl;

import com.fhtechnikum.paperless.controller.PaperlessApi;
import com.fhtechnikum.paperless.services.dto.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PaperlessApiControllerImpl implements PaperlessApi {

    // Simple in-memory storage for Sprint 1 testing
    private final List<Document> documents = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        return ResponseEntity.ok(documents);
    }

    @Override
    public ResponseEntity<Document> createDocument(Document document) {
        // Set ID and upload date
        document.setId(idCounter.getAndIncrement());
        document.setUploadDate(OffsetDateTime.from(LocalDateTime.now()));

        // Add to our in-memory list
        documents.add(document);

        return ResponseEntity.status(201).body(document);
    }

    @Override
    public ResponseEntity<Document> getDocument(Long id) {
        Document found = documents.stream()
                .filter(doc -> doc.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (found == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(found);
    }

    @Override
    public ResponseEntity<Document> updateDocument(Long id, Document document) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equals(id)) {
                document.setId(id); // Keep the same ID
                documents.set(i, document);
                return ResponseEntity.ok(document);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteDocument(Long id) {
        boolean removed = documents.removeIf(doc -> doc.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}