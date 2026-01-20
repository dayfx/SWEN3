package com.fhtechnikum.paperlessservices.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "paperless-index")
public class ElasticSearchDocument {
    @Id
    private Long id;
    private String content;
    private String notes;

    public ElasticSearchDocument() {}

    public ElasticSearchDocument(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public ElasticSearchDocument(Long id, String content, String notes) {
        this.id = id;
        this.content = content;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
