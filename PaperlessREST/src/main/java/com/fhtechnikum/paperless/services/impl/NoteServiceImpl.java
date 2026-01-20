package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.ElasticSearchRepository;
import com.fhtechnikum.paperless.persistence.repository.NoteRepository;
import com.fhtechnikum.paperless.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final DocumentRepository documentRepository;
    private final ElasticSearchRepository elasticSearchRepository;

    public NoteServiceImpl(NoteRepository noteRepository, DocumentRepository documentRepository,
                          ElasticSearchRepository elasticSearchRepository) {
        this.noteRepository = noteRepository;
        this.documentRepository = documentRepository;
        this.elasticSearchRepository = elasticSearchRepository;
    }

    @Override
    public List<NoteEntity> getNotesForDocument(Long documentId) {
        log.info("Fetching notes for document ID: {}", documentId);
        return noteRepository.findByDocumentIdOrderByCreatedDateDesc(documentId);
    }

    @Override
    @Transactional
    public NoteEntity addNote(Long documentId, String content) {
        log.info("Adding note to document ID: {}", documentId);

        DocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        NoteEntity note = new NoteEntity(content, document);
        NoteEntity savedNote = noteRepository.save(note);

        log.info("Note created successfully with ID: {}", savedNote.getId());

        // Re-index document in Elasticsearch with updated notes
        reindexDocumentNotes(document);

        return savedNote;
    }

    @Override
    @Transactional
    public boolean deleteNote(Long noteId) {
        log.info("Deleting note with ID: {}", noteId);

        return noteRepository.findById(noteId)
                .map(note -> {
                    DocumentEntity document = note.getDocument();
                    noteRepository.deleteById(noteId);
                    log.info("Note deleted successfully: {}", noteId);

                    // Re-index document in Elasticsearch with updated notes
                    reindexDocumentNotes(document);

                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Note not found with ID: {}", noteId);
                    return false;
                });
    }

    private void reindexDocumentNotes(DocumentEntity document) {
        try {
            // Get all notes for this document and concatenate their content
            List<NoteEntity> notes = noteRepository.findByDocumentIdOrderByCreatedDateDesc(document.getId());
            String notesContent = notes.stream()
                    .map(NoteEntity::getContent)
                    .collect(Collectors.joining(" "));

            // Update or create the Elasticsearch document
            ElasticSearchDocument esDoc = new ElasticSearchDocument(
                    document.getId(),
                    document.getContent(),
                    notesContent
            );
            elasticSearchRepository.save(esDoc);

            log.info("Elasticsearch index updated for document ID: {} with {} notes",
                    document.getId(), notes.size());
        } catch (Exception e) {
            log.error("Failed to update Elasticsearch index for document ID: {}", document.getId(), e);
        }
    }
}
