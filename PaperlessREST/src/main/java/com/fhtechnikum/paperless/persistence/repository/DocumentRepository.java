package com.fhtechnikum.paperless.persistence.repository;

import com.fhtechnikum.paperless.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByTitleContainingIgnoreCase(String title);
}