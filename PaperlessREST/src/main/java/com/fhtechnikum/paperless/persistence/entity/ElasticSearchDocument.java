package com.fhtechnikum.paperless.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "paperless-index")
public class ElasticSearchDocument {
    @Id
    private Long id;
    private String content;

    public ElasticSearchDocument() {}

    public ElasticSearchDocument(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
