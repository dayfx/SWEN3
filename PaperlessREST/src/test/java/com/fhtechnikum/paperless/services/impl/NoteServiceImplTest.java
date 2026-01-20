package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.ElasticSearchRepository;
import com.fhtechnikum.paperless.persistence.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void getNotesForDocument_ShouldReturnList() {

        Long docId = 1L;
        List<NoteEntity> expectedNotes = List.of(new NoteEntity());
        when(noteRepository.findByDocumentIdOrderByCreatedDateDesc(docId)).thenReturn(expectedNotes);

        List<NoteEntity> result = noteService.getNotesForDocument(docId);

        assertEquals(1, result.size());
        verify(noteRepository, times(1)).findByDocumentIdOrderByCreatedDateDesc(docId);
    }

    @Test
    void addNote_ShouldSaveNoteAndTriggerReindex() {

        Long docId = 1L;
        DocumentEntity document = new DocumentEntity();
        document.setId(docId);
        document.setContent("Doc Content");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));

        when(noteRepository.save(any(NoteEntity.class))).thenAnswer(invocation -> {
            NoteEntity n = invocation.getArgument(0);
            n.setId(100L); // Simulate DB ID generation
            return n;
        });

        // Mock finding notes for re-indexing logic
        when(noteRepository.findByDocumentIdOrderByCreatedDateDesc(docId))
                .thenReturn(Collections.singletonList(new NoteEntity("New Note", document)));

        NoteEntity result = noteService.addNote(docId, "New Note");

        assertNotNull(result);
        assertEquals("New Note", result.getContent());

        // verify Postgres Save
        verify(noteRepository, times(1)).save(any(NoteEntity.class));

        // verify Elasticsearch Updat
        verify(elasticSearchRepository, times(1)).save(any(ElasticSearchDocument.class));
    }

    @Test
    void addNote_ShouldThrowException_WhenDocumentNotFound() {

        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            noteService.addNote(99L, "Note");
        });
    }

    @Test
    void deleteNote_ShouldDeleteAndTriggerReindex() {

        Long noteId = 50L;
        Long docId = 1L;

        DocumentEntity document = new DocumentEntity();
        document.setId(docId);

        NoteEntity note = new NoteEntity("Content", document);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        boolean result = noteService.deleteNote(noteId);

        assertTrue(result);

        verify(noteRepository, times(1)).deleteById(noteId);

        verify(elasticSearchRepository, times(1)).save(any(ElasticSearchDocument.class));
    }

    @Test
    void deleteNote_ShouldReturnFalse_WhenNoteNotFound() {

        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = noteService.deleteNote(99L);

        assertFalse(result);
        verify(noteRepository, never()).deleteById(any());
    }
}