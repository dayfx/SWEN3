package com.fhtechnikum.paperless.controller.impl;

import com.fhtechnikum.paperless.controller.PaperlessApi;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.services.dto.Note;
import com.fhtechnikum.paperless.services.dto.NoteRequest;
import com.fhtechnikum.paperless.services.DocumentService;
import com.fhtechnikum.paperless.services.NoteService;
import com.fhtechnikum.paperless.services.mapper.DocumentMapper;
import com.fhtechnikum.paperless.services.mapper.NoteMapper;
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

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteMapper noteMapper;

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

    @Override
    public ResponseEntity<List<Document>> searchDocuments(String query) {
        try {
            log.info("Received search request - query: {}", query);

            // Validate query
            if (query == null || query.trim().isEmpty()) {
                log.error("Search failed: empty query");
                return ResponseEntity.badRequest().build();
            }

            // Service returns entities
            List<DocumentEntity> entities = documentService.searchDocuments(query);

            // Controller handles DTO mapping
            List<Document> documents = entities.stream()
                    .map(documentMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Search completed - found {} documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (DataAccessException e) {
            log.error("Database error during search: {}", e.getMessage(), e);
            return ResponseEntity.status(503).build(); // Service Unavailable
        } catch (Exception e) {
            log.error("Elasticsearch error during search: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build(); // Internal Server Error
        }
    }

    @Override
    public ResponseEntity<List<Note>> getDocumentNotes(Long id) {
        try {
            log.info("Fetching notes for document ID: {}", id);

            // Service returns entities
            List<NoteEntity> entities = noteService.getNotesForDocument(id);

            // Controller handles DTO mapping
            List<Note> notes = entities.stream()
                    .map(noteMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Found {} notes for document ID: {}", notes.size(), id);
            return ResponseEntity.ok(notes);
        } catch (DataAccessException e) {
            log.error("Database error while fetching notes for document {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(503).build();
        }
    }

    @Override
    public ResponseEntity<Note> addDocumentNote(Long id, NoteRequest noteRequest) {
        try {
            log.info("Adding note to document ID: {}", id);

            // Service returns entity
            NoteEntity entity = noteService.addNote(id, noteRequest.getContent());

            // Controller handles DTO mapping
            Note note = noteMapper.toDto(entity);

            log.info("Note created successfully with ID: {}", note.getId());
            return ResponseEntity.status(201).body(note);
        } catch (DataAccessException e) {
            log.error("Database error while adding note: {}", e.getMessage(), e);
            return ResponseEntity.status(503).build();
        } catch (RuntimeException e) {
            // Document not found or other runtime errors
            log.error("Error adding note to document {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteNote(Long id) {
        try {
            log.info("Deleting note ID: {}", id);

            boolean deleted = noteService.deleteNote(id);
            if (deleted) {
                log.info("Note deleted successfully: {}", id);
                return ResponseEntity.noContent().build();
            }

            log.warn("Note not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e) {
            log.error("Database error while deleting note {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(503).build();
        }
    }
}