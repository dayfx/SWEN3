package com.fhtechnikum.paperlessservices.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TesseractOcrServiceTest {

    @InjectMocks
    private TesseractOcrService tesseractOcrService;

    @BeforeEach
    void setUp() {
        // Since these are not using Spring Context - manually set the @Value fields that come from application.properties
        ReflectionTestUtils.setField(tesseractOcrService, "tessDataPath", "src/test/resources/tessdata");
        ReflectionTestUtils.setField(tesseractOcrService, "language", "eng");
    }

    @Test
    void performOcr_ShouldReturnStringDirectly_WhenTypeIsText() throws Exception {

        String content = "This is just plain text.";
        byte[] fileData = content.getBytes();
        String mimeType = "text/plain";

        String result = tesseractOcrService.performOcr(fileData, mimeType);

        assertEquals(content, result, "Text files should not trigger Tesseract, just return content");
    }

    @Test
    void performOcr_ShouldThrowException_WhenMimeTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            tesseractOcrService.performOcr(new byte[0], null);
        });
    }

    @Test
    void performOcr_ShouldReturnErrorMessage_WhenMimeTypeNotSupported() throws Exception {

        byte[] fileData = new byte[0];
        String mimeType = "application/json"; // not supported

        String result = tesseractOcrService.performOcr(fileData, mimeType);

        assertTrue(result.contains("OCR not supported"), "Should return fallback message for unknown types");
    }

    @Test
    void performOcr_ShouldHandleExceptionsGracefully() {
        // pass garbage bytes for a PDF so PDFBox fails
        byte[] invalidPdfData = new byte[]{1, 2, 3, 4, 5};
        String mimeType = "application/pdf";

        Exception exception = assertThrows(Exception.class, () -> {
            tesseractOcrService.performOcr(invalidPdfData, mimeType);
        });

        assertTrue(exception.getMessage().contains("OCR processing failed"));
    }
}