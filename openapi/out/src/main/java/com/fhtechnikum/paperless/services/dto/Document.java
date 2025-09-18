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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-09-16T14:39:45.142619547Z[Etc/UTC]", comments = "Generator version: 7.16.0-SNAPSHOT")
public class Document {

  private @Nullable Long id;

  private String title;

  private String author;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime uploadDate;

  public Document() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Document(String title, String author, String content) {
    this.title = title;
    this.author = author;
    this.content = content;
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

  public Document author(String author) {
    this.author = author;
    return this;
  }

  /**
   * The author of the document
   * @return author
   */
  @NotNull 
  @Schema(name = "author", description = "The author of the document", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Document content(String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of the document
   * @return content
   */
  @NotNull 
  @Schema(name = "content", description = "The content of the document", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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
        Objects.equals(this.uploadDate, document.uploadDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, author, content, uploadDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Document {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

