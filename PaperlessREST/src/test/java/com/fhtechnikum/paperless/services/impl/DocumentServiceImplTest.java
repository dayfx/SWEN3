package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.messaging.DocumentMessageProducer;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import com.fhtechnikum.paperless.persistence.repository.DocumentRepository;
import com.fhtechnikum.paperless.persistence.repository.ElasticSearchRepository;
import com.fhtechnikum.paperless.services.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentMessageProducer messageProducer;
    @Mock
    private FileStorage fileStorage;
    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void uploadDocument_ShouldThrowException_WhenFileIsEmpty() {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadDocument(file, "Test", "Author");
        });

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenFileIsTooLarge() {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(11 * 1024 * 1024L); // 11MB (Limit is 10MB)

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadDocument(file, "Test", "Author");
        });
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenMimeTypeNotAllowed() {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1000L);
        when(file.getContentType()).thenReturn("image/png"); // PNG is not allowed

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadDocument(file, "Test", "Author");
        });

        assertTrue(exception.getMessage().contains("Unsupported file type"));
    }

    @Test
    void uploadDocument_ShouldSuccess_WhenValidPDF() throws IOException {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1000L);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getBytes()).thenReturn(new byte[10]);

        DocumentEntity fakeEntity = new DocumentEntity();
        fakeEntity.setId(1L);
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(fakeEntity);

        DocumentEntity result = documentService.uploadDocument(file, "Title", "Author");

        assertNotNull(result);
        verify(fileStorage, times(1)).upload(anyString(), any()); // Verify MinIO upload was called
        verify(documentRepository, times(1)).save(any(DocumentEntity.class)); // Verify DB save was called
    }
}