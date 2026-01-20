package com.fhtechnikum.paperless.persistence.repository;

import com.fhtechnikum.paperless.persistence.entity.ElasticSearchDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearchDocument, Long> {
    // Use Elasticsearch multi_match query to search both content and notes fields
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content\", \"notes\"], \"operator\": \"and\"}}")
    List<ElasticSearchDocument> searchByContent(String query);
}