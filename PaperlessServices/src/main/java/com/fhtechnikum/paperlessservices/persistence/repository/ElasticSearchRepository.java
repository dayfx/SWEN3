package com.fhtechnikum.paperlessservices.persistence.repository;

import com.fhtechnikum.paperlessservices.persistence.entity.ElasticSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearchDocument, Long> {
}