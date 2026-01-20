package com.fhtechnikum.paperless.persistence.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DocumentEntityTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() { // big document entity test file just cause

        DocumentEntity doc = new DocumentEntity();
        LocalDateTime now = LocalDateTime.now();

        doc.setId(1L);
        doc.setTitle("Test Title");
        doc.setAuthor("Test Author");
        doc.setContent("Test Content");
        doc.setSummary("AI Summary");
        doc.setOriginalFilename("file.pdf");
        doc.setMimeType("application/pdf");
        doc.setFileSize(1024L);
        doc.setMinioObjectKey("uuid_file.pdf");
        doc.setUploadDate(now);

        assertEquals(1L, doc.getId());
        assertEquals("Test Title", doc.getTitle());
        assertEquals("Test Author", doc.getAuthor());
        assertEquals("Test Content", doc.getContent());
        assertEquals("AI Summary", doc.getSummary());
        assertEquals("file.pdf", doc.getOriginalFilename());
        assertEquals("application/pdf", doc.getMimeType());
        assertEquals(1024L, doc.getFileSize());
        assertEquals("uuid_file.pdf", doc.getMinioObjectKey());
        assertEquals(now, doc.getUploadDate());
    }

    @Test
    void constructor_ShouldInitializeRequiredFields() {
        DocumentEntity doc = new DocumentEntity("Title", "Author", "Content");

        assertEquals("Title", doc.getTitle());
        assertEquals("Author", doc.getAuthor());
        assertEquals("Content", doc.getContent());
    }

    @Test
    void fullConstructor_ShouldInitializeAllFields() {

        LocalDateTime now = LocalDateTime.now();

        DocumentEntity doc = new DocumentEntity(10L, "Title", "Author", "Content", now);

        assertEquals(10L, doc.getId());
        assertEquals("Title", doc.getTitle());
        assertEquals(now, doc.getUploadDate());
    }

    @Test
    void addNote_ShouldAddNoteAndSetRelationship() {

        DocumentEntity doc = new DocumentEntity();
        NoteEntity note = new NoteEntity(); // Assuming default constructor exists

        doc.addNote(note);

        assertTrue(doc.getNotes().contains(note), "Note list should contain the added note");
        assertEquals(doc, note.getDocument(), "Note should reference the correct document");
    }

    @Test
    void removeNote_ShouldRemoveNoteAndClearRelationship() {

        DocumentEntity doc = new DocumentEntity();
        NoteEntity note = new NoteEntity();
        doc.addNote(note); // Add it first

        doc.removeNote(note);

        assertFalse(doc.getNotes().contains(note), "Note list should be empty after removal");
        assertNull(note.getDocument(), "Note reference to document should be null");
    }
}