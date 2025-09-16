package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Document createDocument(Document document) {
        DocumentEntity entity = dtoToEntity(document);
        entity.setUploadDate(LocalDateTime.now());
        DocumentEntity saved = documentRepository.save(entity);
        return entityToDto(saved);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::entityToDto);
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        return documentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(document.getTitle());
                    existing.setAuthor(document.getAuthor());
                    existing.setContent(document.getContent());
                    DocumentEntity updated = documentRepository.save(existing);
                    return entityToDto(updated);
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

    // Simple conversion methods (business logic layer)
    private Document entityToDto(DocumentEntity entity) {
        Document dto = new Document();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setContent(entity.getContent());
        if (entity.getUploadDate() != null) {
            dto.setUploadDate(entity.getUploadDate().atOffset(ZoneOffset.UTC));
        }
        return dto;
    }

    private DocumentEntity dtoToEntity(Document dto) {
        DocumentEntity entity = new DocumentEntity();
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setContent(dto.getContent());
        return entity;
    }
}