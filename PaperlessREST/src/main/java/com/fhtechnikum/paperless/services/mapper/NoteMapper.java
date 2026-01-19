package com.fhtechnikum.paperless.services.mapper;

import com.fhtechnikum.paperless.services.dto.Note;
import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(source = "document.id", target = "documentId")
    Note toDto(NoteEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "document", ignore = true)
    NoteEntity toEntity(Note dto);

    // LocalDateTime to OffsetDateTime
    default OffsetDateTime fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
