package com.fhtechnikum.paperless.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // Default constructor
    public DocumentEntity() {
    }

    // Constructor with required parameters
    public DocumentEntity(String title, String author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
    }

    // Full constructor
    public DocumentEntity(Long id, String title, String author, String content, LocalDateTime uploadDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.uploadDate = uploadDate;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
