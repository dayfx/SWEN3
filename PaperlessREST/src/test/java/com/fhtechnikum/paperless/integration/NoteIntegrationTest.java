package com.fhtechnikum.paperless.integration;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.ElasticSearchRepository;
import com.fhtechnikum.paperless.persistence.repository.NoteRepository;
import com.fhtechnikum.paperless.services.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Notes REST API endpoints.
 * Tests the full flow: Controller -> Service -> Repository (H2)
 * External services (Elasticsearch) are mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private NoteRepository noteRepository;

    // Mock external services
    @MockitoBean
    private FileStorage fileStorage;

    @MockitoBean
    private ElasticSearchRepository elasticSearchRepository;

    private DocumentEntity testDocument;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        documentRepository.deleteAll();

        // Create a test document for notes
        testDocument = new DocumentEntity();
        testDocument.setTitle("Test Document for Notes");
        testDocument.setAuthor("Test Author");
        testDocument.setOriginalFilename("test.pdf");
        testDocument.setUploadDate(LocalDateTime.now());
        testDocument.setContent("Document content for testing");
        testDocument = documentRepository.save(testDocument);
    }

    // ==================== GET /api/documents/{id}/notes ====================

    @Test
    void getDocumentNotes_shouldReturnEmptyList_whenNoNotes() throws Exception {
        mockMvc.perform(get("/api/documents/{id}/notes", testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getDocumentNotes_shouldReturnAllNotes() throws Exception {
        // Arrange - create test notes
        NoteEntity note1 = new NoteEntity("First note content", testDocument);
        NoteEntity note2 = new NoteEntity("Second note content", testDocument);
        noteRepository.save(note1);
        noteRepository.save(note2);

        // Act & Assert
        mockMvc.perform(get("/api/documents/{id}/notes", testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ==================== POST /api/documents/{id}/notes ====================

    @Test
    void addDocumentNote_shouldCreateNote() throws Exception {
        String noteJson = """
                {
                    "content": "This is a new note"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/documents/{id}/notes", testDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("This is a new note")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.documentId", is(testDocument.getId().intValue())));

        // Verify note is persisted
        assert noteRepository.count() == 1;
    }

    @Test
    void addDocumentNote_shouldReturn404_whenDocumentNotExists() throws Exception {
        String noteJson = """
                {
                    "content": "Note for non-existent document"
                }
                """;

        mockMvc.perform(post("/api/documents/{id}/notes", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMultipleNotes_shouldAllBePersisted() throws Exception {
        String noteJson1 = """
                {
                    "content": "First note"
                }
                """;
        String noteJson2 = """
                {
                    "content": "Second note"
                }
                """;

        // Add first note
        mockMvc.perform(post("/api/documents/{id}/notes", testDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson1))
                .andExpect(status().isCreated());

        // Add second note
        mockMvc.perform(post("/api/documents/{id}/notes", testDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson2))
                .andExpect(status().isCreated());

        // Verify both notes exist
        mockMvc.perform(get("/api/documents/{id}/notes", testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ==================== DELETE /api/notes/{id} ====================

    @Test
    void deleteNote_shouldDeleteAndReturn204() throws Exception {
        // Arrange - create a note
        NoteEntity note = new NoteEntity("Note to delete", testDocument);
        NoteEntity saved = noteRepository.save(note);

        // Act & Assert
        mockMvc.perform(delete("/api/notes/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assert noteRepository.findById(saved.getId()).isEmpty();
    }

    @Test
    void deleteNote_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // ==================== End-to-End Flow Test ====================

    @Test
    void fullNotesFlow_createReadDelete() throws Exception {
        // 1. Create a note
        String noteJson = """
                {
                    "content": "Complete flow test note"
                }
                """;

        String response = mockMvc.perform(post("/api/documents/{id}/notes", testDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract note ID from response
        Long noteId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(response)
                .get("id")
                .asLong();

        // 2. Read notes - should contain the created note
        mockMvc.perform(get("/api/documents/{id}/notes", testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is("Complete flow test note")));

        // 3. Delete the note
        mockMvc.perform(delete("/api/notes/{id}", noteId))
                .andExpect(status().isNoContent());

        // 4. Verify note is deleted
        mockMvc.perform(get("/api/documents/{id}/notes", testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
