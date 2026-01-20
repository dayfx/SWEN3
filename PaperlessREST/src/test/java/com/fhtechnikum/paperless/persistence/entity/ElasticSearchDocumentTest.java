package com.fhtechnikum.paperless.persistence.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElasticSearchDocumentTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        ElasticSearchDocument doc = new ElasticSearchDocument();

        // Act
        doc.setId(1L);
        doc.setContent("Test Content");
        doc.setNotes("Test Note");

        // Assert
        assertEquals(1L, doc.getId());
        assertEquals("Test Content", doc.getContent());
        assertEquals("Test Note", doc.getNotes());
    }

    @Test
    void constructor_ShouldInitializeFields() {
        // Arrange & Act
        ElasticSearchDocument doc = new ElasticSearchDocument(5L, "Content", "Note");

        // Assert
        assertEquals(5L, doc.getId());
        assertEquals("Content", doc.getContent());
        assertEquals("Note", doc.getNotes());
    }

    @Test
    void partialConstructor_ShouldInitializeIdAndContent() {
        // Arrange & Act
        ElasticSearchDocument doc = new ElasticSearchDocument(10L, "Just Content");

        // Assert
        assertEquals(10L, doc.getId());
        assertEquals("Just Content", doc.getContent());
        assertNull(doc.getNotes()); // Notes should be null
    }
}