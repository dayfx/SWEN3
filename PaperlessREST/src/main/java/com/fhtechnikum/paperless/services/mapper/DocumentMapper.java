package com.fhtechnikum.paperless.services.mapper;

import com.fhtechnikum.paperless.services.dto.Document;
import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Tells MapStruct to generate a Spring Component
public interface DocumentMapper {

    /**
     * Converts a DocumentEntity to a DocumentDto.
     * Field names are the same, so no extra configuration is needed.
     */
    Document toDto(DocumentEntity entity);

    /**
     * Converts a DocumentDto to a DocumentEntity.
     * We ignore the 'id' and 'uploadDate' fields because they are
     * controlled by the server (database and business logic), not the client.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadDate", ignore = true)
    DocumentEntity toEntity(Document dto);
}