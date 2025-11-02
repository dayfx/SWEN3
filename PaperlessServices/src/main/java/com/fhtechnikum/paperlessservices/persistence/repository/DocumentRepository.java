package com.fhtechnikum.paperlessservices.persistence.repository;

import com.fhtechnikum.paperlessservices.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for DocumentEntity
 * Provides database access for OCR worker to update documents
 */
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}

