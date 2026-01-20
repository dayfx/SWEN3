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
 * Note
 */

@JsonTypeName("note")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-19T16:22:53.698489191Z[Etc/UTC]", comments = "Generator version: 7.19.0-SNAPSHOT")
public class Note {

  private @Nullable Long id;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdDate;

  private @Nullable Long documentId;

  public Note() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Note(String content) {
    this.content = content;
  }

  public Note id(@Nullable Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique id of the note
   * @return id
   */
  
  @Schema(name = "id", description = "The unique id of the note", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable Long getId() {
    return id;
  }

  public void setId(@Nullable Long id) {
    this.id = id;
  }

  public Note content(String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of the note
   * @return content
   */
  @NotNull 
  @Schema(name = "content", description = "The content of the note", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Note createdDate(@Nullable OffsetDateTime createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  /**
   * When the note was created
   * @return createdDate
   */
  @Valid 
  @Schema(name = "createdDate", description = "When the note was created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdDate")
  public @Nullable OffsetDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(@Nullable OffsetDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public Note documentId(@Nullable Long documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * The id of the document this note belongs to
   * @return documentId
   */
  
  @Schema(name = "documentId", description = "The id of the document this note belongs to", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("documentId")
  public @Nullable Long getDocumentId() {
    return documentId;
  }

  public void setDocumentId(@Nullable Long documentId) {
    this.documentId = documentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Note note = (Note) o;
    return Objects.equals(this.id, note.id) &&
        Objects.equals(this.content, note.content) &&
        Objects.equals(this.createdDate, note.createdDate) &&
        Objects.equals(this.documentId, note.documentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content, createdDate, documentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Note {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    createdDate: ").append(toIndentedString(createdDate)).append("\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
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

