package com.fhtechnikum.paperlessservices.services;

import com.fhtechnikum.paperlessservices.messaging.dto.DocumentMessage;
import com.fhtechnikum.paperlessservices.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperlessservices.persistence.entity.ElasticSearchDocument;
import com.fhtechnikum.paperlessservices.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperlessservices.persistence.repository.ElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrWorkerServiceTest {

    @Mock
    private MinIOService minioService;
    @Mock
    private TesseractOcrService ocrService;
    @Mock
    private GenAIService genAIService;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @InjectMocks
    private OcrWorkerService ocrWorkerService;

    @Test
    void processOcrMessage_ShouldRunFullPipeline_WhenSuccessful() throws Exception {
        // Arrange
        Long docId = 1L;
        DocumentMessage message = new DocumentMessage(docId, "test.pdf", "bucket/test.pdf");

        DocumentEntity document = new DocumentEntity();
        document.setId(docId);
        document.setTitle("Test Doc");
        document.setMimeType("application/pdf");

        // 1. Mock DB find
        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));

        // 2. Mock MinIO download
        byte[] fakeFileContent = "Fake PDF Content".getBytes();
        when(minioService.downloadFile("bucket/test.pdf")).thenReturn(fakeFileContent);

        // 3. Mock OCR result
        String ocrResult = "Extracted Text Content";
        when(ocrService.performOcr(fakeFileContent, "application/pdf")).thenReturn(ocrResult);

        // 4. Mock AI Summary
        when(genAIService.generateSummary(ocrResult)).thenReturn("Cool Summary");

        // Act
        ocrWorkerService.processOcrMessage(message);

        // Assert
        // Verify Postgres Update
        ArgumentCaptor<DocumentEntity> docCaptor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, atLeastOnce()).save(docCaptor.capture());

        DocumentEntity savedDoc = docCaptor.getValue();
        assertEquals("Extracted Text Content", savedDoc.getContent());
        assertEquals("Cool Summary", savedDoc.getSummary());

        // Verify Elasticsearch Indexing (Sprint 6 Requirement)
        verify(elasticSearchRepository, times(1)).save(any(ElasticSearchDocument.class));
    }

    @Test
    void processOcrMessage_ShouldHandleAiFailure_ButStillSaveContent() throws Exception {
        // Arrange
        Long docId = 1L;
        DocumentMessage message = new DocumentMessage(docId, "test.pdf", "bucket/test.pdf");
        DocumentEntity document = new DocumentEntity();
        document.setId(docId);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));
        when(minioService.downloadFile(anyString())).thenReturn(new byte[0]);
        when(ocrService.performOcr(any(), any())).thenReturn("Some text");

        // Force AI Service to fail
        when(genAIService.generateSummary(anyString())).thenThrow(new RuntimeException("AI Service Down"));

        // Act
        ocrWorkerService.processOcrMessage(message);

        // Assert
        ArgumentCaptor<DocumentEntity> docCaptor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, atLeastOnce()).save(docCaptor.capture());

        DocumentEntity savedDoc = docCaptor.getValue();
        assertEquals("Some text", savedDoc.getContent(), "OCR text should still be saved");
        assertTrue(savedDoc.getSummary().contains("AI Summary Failed"), "Summary should contain error message");

        // Elastic should STILL be called because OCR succeeded
        verify(elasticSearchRepository, times(1)).save(any(ElasticSearchDocument.class));
    }

    @Test
    void processOcrMessage_ShouldAbort_WhenDocumentNotFound() throws Exception {
        // Arrange
        DocumentMessage message = new DocumentMessage(99L, "ghost.pdf", "ghost/key");

        // Mock DB returning Empty
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ocrWorkerService.processOcrMessage(message);

        // Assert
        // Should NOT attempt download or OCR
        verify(minioService, never()).downloadFile(any());
        verify(ocrService, never()).performOcr(any(), any());
    }
}