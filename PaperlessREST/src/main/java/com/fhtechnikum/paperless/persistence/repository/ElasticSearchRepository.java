package com.fhtechnikum.paperless.persistence.repository;

import com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearchDocument, Long> {
    List<ElasticSearchDocument> findByContentContaining(String content);
}