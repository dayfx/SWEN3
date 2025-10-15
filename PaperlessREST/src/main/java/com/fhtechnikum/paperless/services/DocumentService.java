package com.fhtechnikum.paperless.services;

import com.fhtechnikum.paperless.services.dto.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document createDocument(Document document);
    Document uploadDocument(MultipartFile file, String title, String author) throws IOException;
    List<Document> getAllDocuments();
    Optional<Document> getDocumentById(Long id);
    Document updateDocument(Long id, Document document);
    boolean deleteDocument(Long id);
}

