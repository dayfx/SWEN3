package com.fhtechnikum.paperless.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "minio_object_key")
    private String minioObjectKey;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteEntity> notes = new ArrayList<>();

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMinioObjectKey() {
        return minioObjectKey;
    }

    public void setMinioObjectKey(String minioObjectKey) {
        this.minioObjectKey = minioObjectKey;
    }

    public List<NoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteEntity> notes) {
        this.notes = notes;
    }

    public void addNote(NoteEntity note) {
        notes.add(note);
        note.setDocument(this);
    }

    public void removeNote(NoteEntity note) {
        notes.remove(note);
        note.setDocument(null);
    }
}
