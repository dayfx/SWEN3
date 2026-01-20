package com.fhtechnikum.paperless.integration;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.ElasticSearchRepository;
import com.fhtechnikum.paperless.services.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Document REST API endpoints.
 * Tests the full flow: Controller -> Service -> Repository (H2)
 * External services (MinIO, Elasticsearch, RabbitMQ) are mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    // Mock external services
    @MockitoBean
    private FileStorage fileStorage;

    @MockitoBean
    private ElasticSearchRepository elasticSearchRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
    }

    // ==================== GET /api/documents ====================

    @Test
    void getDocuments_shouldReturnEmptyList_whenNoDocuments() throws Exception {
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getDocuments_shouldReturnAllDocuments() throws Exception {
        // Arrange - create test documents
        DocumentEntity doc1 = createTestDocument("Test Doc 1", "Author 1");
        DocumentEntity doc2 = createTestDocument("Test Doc 2", "Author 2");
        documentRepository.save(doc1);
        documentRepository.save(doc2);

        // Act & Assert
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Doc 1")))
                .andExpect(jsonPath("$[1].title", is("Test Doc 2")));
    }

    // ==================== GET /api/documents/{id} ====================

    @Test
    void getDocument_shouldReturnDocument_whenExists() throws Exception {
        // Arrange
        DocumentEntity doc = createTestDocument("My Document", "John Doe");
        doc.setContent("This is the OCR content");
        DocumentEntity saved = documentRepository.save(doc);

        // Act & Assert
        mockMvc.perform(get("/api/documents/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("My Document")))
                .andExpect(jsonPath("$.author", is("John Doe")))
                .andExpect(jsonPath("$.content", is("This is the OCR content")));
    }

    @Test
    void getDocument_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/api/documents/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // ==================== POST /api/documents (Upload) ====================

    @Test
    void uploadDocument_shouldCreateDocument() throws Exception {
        // Arrange - mock file storage (void method)
        doNothing().when(fileStorage).upload(any(), any());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "PDF content here".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/documents")
                        .file(file)
                        .param("title", "Uploaded Document")
                        .param("author", "Test Author"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Uploaded Document")))
                .andExpect(jsonPath("$.author", is("Test Author")))
                .andExpect(jsonPath("$.id", notNullValue()));

        // Verify document is persisted
        assert documentRepository.count() == 1;
    }

    @Test
    void uploadDocument_shouldReturn400_whenFileEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/documents")
                        .file(emptyFile)
                        .param("title", "Empty Doc"))
                .andExpect(status().isBadRequest());
    }

    // ==================== PUT /api/documents/{id} ====================

    @Test
    void updateDocument_shouldUpdateFields() throws Exception {
        // Arrange
        DocumentEntity doc = createTestDocument("Original Title", "Original Author");
        DocumentEntity saved = documentRepository.save(doc);

        String updateJson = """
                {
                    "title": "Updated Title",
                    "author": "Updated Author",
                    "content": "Updated Content"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/documents/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.author", is("Updated Author")))
                .andExpect(jsonPath("$.content", is("Updated Content")));

        // Verify database update
        DocumentEntity updated = documentRepository.findById(saved.getId()).orElseThrow();
        assert updated.getTitle().equals("Updated Title");
    }

    @Test
    void updateDocument_shouldReturn404_whenNotExists() throws Exception {
        String updateJson = """
                {
                    "title": "New Title"
                }
                """;

        mockMvc.perform(put("/api/documents/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE /api/documents/{id} ====================

    @Test
    void deleteDocument_shouldDeleteAndReturn204() throws Exception {
        // Arrange
        DocumentEntity doc = createTestDocument("To Delete", "Author");
        DocumentEntity saved = documentRepository.save(doc);

        // Act & Assert
        mockMvc.perform(delete("/api/documents/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assert documentRepository.findById(saved.getId()).isEmpty();
    }

    @Test
    void deleteDocument_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/documents/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // ==================== GET /api/documents/search ====================

    @Test
    void searchDocuments_shouldReturn400_whenQueryEmpty() throws Exception {
        mockMvc.perform(get("/api/documents/search")
                        .param("query", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchDocuments_shouldReturnResults_whenFound() throws Exception {
        // Arrange - create document and mock Elasticsearch
        DocumentEntity doc = createTestDocument("Searchable Doc", "Author");
        doc.setContent("This contains the keyword invoice");
        DocumentEntity saved = documentRepository.save(doc);

        // Mock Elasticsearch to return this document's ID
        when(elasticSearchRepository.searchByContent("invoice"))
                .thenReturn(Collections.singletonList(
                        new com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument(saved.getId(), "invoice")
                ));

        // Act & Assert
        mockMvc.perform(get("/api/documents/search")
                        .param("query", "invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Searchable Doc")));
    }

    // ==================== Helper Methods ====================

    private DocumentEntity createTestDocument(String title, String author) {
        DocumentEntity doc = new DocumentEntity();
        doc.setTitle(title);
        doc.setAuthor(author);
        doc.setOriginalFilename("test.pdf");
        doc.setUploadDate(LocalDateTime.now());
        return doc;
    }
}
