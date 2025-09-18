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

    // main mapping

    Document toDto(DocumentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadDate", ignore = true)
    DocumentEntity toEntity(Document dto);

     // LocalDateTime to an OffsetDateTime stuff

    default OffsetDateTime fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}