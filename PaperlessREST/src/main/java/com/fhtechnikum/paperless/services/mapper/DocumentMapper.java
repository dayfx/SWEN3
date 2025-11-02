package com.fhtechnikum.paperless.services.mapper;

import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // main mapping - Entity to DTO (includes OCR content)
    Document toDto(DocumentEntity entity);

    // DTO to Entity (minioObjectKey will be set manually during upload)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadDate", ignore = true)
    @Mapping(target = "minioObjectKey", ignore = true)
    DocumentEntity toEntity(Document dto);

     // LocalDateTime to an OffsetDateTime
    default OffsetDateTime fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}