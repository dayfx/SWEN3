package com.fhtechnikum.paperless.services;

import com.fhtechnikum.paperless.services.dto.Document;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document createDocument(Document document);
    List<Document> getAllDocuments();
    Optional<Document> getDocumentById(Long id);
    Document updateDocument(Long id, Document document);
    boolean deleteDocument(Long id);
}

