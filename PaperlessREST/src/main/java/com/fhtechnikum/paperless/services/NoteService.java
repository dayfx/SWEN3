package com.fhtechnikum.paperless.services;

import com.fhtechnikum.paperless.persistence.entity.NoteEntity;

import java.util.List;

/**
 * Service interface for note business logic.
 */
public interface NoteService {
    List<NoteEntity> getNotesForDocument(Long documentId);
    NoteEntity addNote(Long documentId, String content);
    boolean deleteNote(Long noteId);
}
