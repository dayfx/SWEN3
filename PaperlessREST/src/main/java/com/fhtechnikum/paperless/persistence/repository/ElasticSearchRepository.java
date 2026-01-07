package com.fhtechnikum.paperless.persistence.repository;

import com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearchDocument, Long> {
    // Use Elasticsearch match query for proper full-text search with multi-word support
    @Query("{\"match\": {\"content\": {\"query\": \"?0\", \"operator\": \"and\"}}}")
    List<ElasticSearchDocument> searchByContent(String query);
}