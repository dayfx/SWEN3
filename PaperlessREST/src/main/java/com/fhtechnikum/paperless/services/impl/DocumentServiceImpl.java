package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.messaging.DocumentMessageProducer;
import com.fhtechnikum.paperless.services.mapper.DocumentMapper; // <-- IMPORT YOUR MAPPER
import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

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
        // 'title' as filename placeholder
        // TODO: update to 'entity.getFilename()' after file handling done
        messageProducer.sendOcrMessage(saved.getId(), saved.getTitle());

        // mapper to convert the saved Entity back to a DTO
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