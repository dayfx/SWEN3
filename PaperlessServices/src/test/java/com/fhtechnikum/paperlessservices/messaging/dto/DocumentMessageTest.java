package com.fhtechnikum.paperlessservices.messaging.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DocumentMessageTest {

    @Test
    void constructor_ShouldInitializeFieldsAndTimestamp() {

        Long id = 100L;
        String filename = "test.pdf";
        String key = "bucket/test.pdf";

        DocumentMessage message = new DocumentMessage(id, filename, key);

        assertEquals(id, message.getDocumentId());
        assertEquals(filename, message.getFilename());
        assertEquals(key, message.getMinioObjectKey());
        assertNotNull(message.getTimestamp(), "Timestamp should be auto-generated in constructor");
    }

    @Test
    void setters_ShouldUpdateValues() {

        DocumentMessage message = new DocumentMessage();
        LocalDateTime now = LocalDateTime.now();

        message.setDocumentId(55L);
        message.setFilename("new.docx");
        message.setMinioObjectKey("new/path");
        message.setTimestamp(now);

        assertEquals(55L, message.getDocumentId());
        assertEquals("new.docx", message.getFilename());
        assertEquals("new/path", message.getMinioObjectKey());
        assertEquals(now, message.getTimestamp());
    }

    @Test
    void toString_ShouldContainFieldValues() {

        DocumentMessage message = new DocumentMessage(1L, "file.txt", "key");

        String result = message.toString();

        assertTrue(result.contains("documentId=1"));
        assertTrue(result.contains("filename='file.txt'"));
        assertTrue(result.contains("minioObjectKey='key'"));
    }
}