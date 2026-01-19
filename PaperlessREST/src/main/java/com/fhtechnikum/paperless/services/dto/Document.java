package com.fhtechnikum.paperless.services.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Document
 */

@JsonTypeName("document")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-19T16:22:53.698489191Z[Etc/UTC]", comments = "Generator version: 7.19.0-SNAPSHOT")
public class Document {

  private @Nullable Long id;

  private String title;

  private @Nullable String author;

  private @Nullable String content;

  private @Nullable String summary;

  private @Nullable String originalFilename;

  private @Nullable String mimeType;

  private @Nullable Long fileSize;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime uploadDate;

  public Document() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Document(String title) {
    this.title = title;
  }

  public Document id(@Nullable Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique id of the document
   * @return id
   */
  
  @Schema(name = "id", description = "The unique id of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable Long getId() {
    return id;
  }

  public void setId(@Nullable Long id) {
    this.id = id;
  }

  public Document title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The title of the document
   * @return title
   */
  @NotNull 
  @Schema(name = "title", description = "The title of the document", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Document author(@Nullable String author) {
    this.author = author;
    return this;
  }

  /**
   * The author of the document
   * @return author
   */
  
  @Schema(name = "author", description = "The author of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("author")
  public @Nullable String getAuthor() {
    return author;
  }

  public void setAuthor(@Nullable String author) {
    this.author = author;
  }

  public Document content(@Nullable String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of the document
   * @return content
   */
  
  @Schema(name = "content", description = "The content of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public @Nullable String getContent() {
    return content;
  }

  public void setContent(@Nullable String content) {
    this.content = content;
  }

  public Document summary(@Nullable String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * AI-generated summary of the document
   * @return summary
   */
  
  @Schema(name = "summary", description = "AI-generated summary of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("summary")
  public @Nullable String getSummary() {
    return summary;
  }

  public void setSummary(@Nullable String summary) {
    this.summary = summary;
  }

  public Document originalFilename(@Nullable String originalFilename) {
    this.originalFilename = originalFilename;
    return this;
  }

  /**
   * The original filename of the uploaded document
   * @return originalFilename
   */
  
  @Schema(name = "originalFilename", description = "The original filename of the uploaded document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalFilename")
  public @Nullable String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(@Nullable String originalFilename) {
    this.originalFilename = originalFilename;
  }

  public Document mimeType(@Nullable String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  /**
   * The MIME type of the document (e.g., application/pdf)
   * @return mimeType
   */
  
  @Schema(name = "mimeType", description = "The MIME type of the document (e.g., application/pdf)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mimeType")
  public @Nullable String getMimeType() {
    return mimeType;
  }

  public void setMimeType(@Nullable String mimeType) {
    this.mimeType = mimeType;
  }

  public Document fileSize(@Nullable Long fileSize) {
    this.fileSize = fileSize;
    return this;
  }

  /**
   * The size of the file in bytes
   * @return fileSize
   */
  
  @Schema(name = "fileSize", description = "The size of the file in bytes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileSize")
  public @Nullable Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(@Nullable Long fileSize) {
    this.fileSize = fileSize;
  }

  public Document uploadDate(@Nullable OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
    return this;
  }

  /**
   * When the document was uploaded
   * @return uploadDate
   */
  @Valid 
  @Schema(name = "uploadDate", description = "When the document was uploaded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uploadDate")
  public @Nullable OffsetDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(@Nullable OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Document document = (Document) o;
    return Objects.equals(this.id, document.id) &&
        Objects.equals(this.title, document.title) &&
        Objects.equals(this.author, document.author) &&
        Objects.equals(this.content, document.content) &&
        Objects.equals(this.summary, document.summary) &&
        Objects.equals(this.originalFilename, document.originalFilename) &&
        Objects.equals(this.mimeType, document.mimeType) &&
        Objects.equals(this.fileSize, document.fileSize) &&
        Objects.equals(this.uploadDate, document.uploadDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, author, content, summary, originalFilename, mimeType, fileSize, uploadDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Document {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    originalFilename: ").append(toIndentedString(originalFilename)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    fileSize: ").append(toIndentedString(fileSize)).append("\n");
    sb.append("    uploadDate: ").append(toIndentedString(uploadDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

