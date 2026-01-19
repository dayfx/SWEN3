package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.NoteRepository;
import com.fhtechnikum.paperless.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final DocumentRepository documentRepository;

    public NoteServiceImpl(NoteRepository noteRepository, DocumentRepository documentRepository) {
        this.noteRepository = noteRepository;
        this.documentRepository = documentRepository;
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
        return savedNote;
    }

    @Override
    @Transactional
    public boolean deleteNote(Long noteId) {
        log.info("Deleting note with ID: {}", noteId);

        if (noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            log.info("Note deleted successfully: {}", noteId);
            return true;
        }

        log.warn("Note not found with ID: {}", noteId);
        return false;
    }
}
