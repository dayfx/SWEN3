package com.fhtechnikum.paperless.services.mapper;

import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class DocumentMapperTest {

    // We instantiate the implementation that MapStruct generated for us
    private final DocumentMapper mapper = new DocumentMapperImpl();

    @Test
    void toDto_ShouldMapEntityFieldsToDto() {

        DocumentEntity entity = new DocumentEntity();
        entity.setId(123L);
        entity.setTitle("Test Title");
        entity.setAuthor("Test Author");
        entity.setOriginalFilename("file.pdf");

        Document dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(123L, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Author", dto.getAuthor());
        assertEquals("file.pdf", dto.getOriginalFilename());
    }

    @Test
    void fromLocalDateTime_ShouldConvertToUtcOffset() {

        LocalDateTime localTime = LocalDateTime.of(2023, 12, 24, 10, 0);

        OffsetDateTime offsetTime = mapper.fromLocalDateTime(localTime);

        assertNotNull(offsetTime);
        assertEquals(localTime.getYear(), offsetTime.getYear());
        assertEquals(ZoneOffset.UTC, offsetTime.getOffset());
    }

    @Test
    void fromLocalDateTime_ShouldReturnNull_WhenInputIsNull() {

        OffsetDateTime result = mapper.fromLocalDateTime(null);

        assertNull(result);
    }
}