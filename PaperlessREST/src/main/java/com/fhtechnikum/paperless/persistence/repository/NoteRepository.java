package com.fhtechnikum.paperless.persistence.repository;

import com.fhtechnikum.paperless.persistence.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByDocumentIdOrderByCreatedDateDesc(Long documentId);
}
