package com.fhtechnikum.paperless.services.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NoteRequest
 */

@JsonTypeName("noteRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-19T16:22:53.698489191Z[Etc/UTC]", comments = "Generator version: 7.19.0-SNAPSHOT")
public class NoteRequest {

  private String content;

  public NoteRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NoteRequest(String content) {
    this.content = content;
  }

  public NoteRequest content(String content) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NoteRequest noteRequest = (NoteRequest) o;
    return Objects.equals(this.content, noteRequest.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NoteRequest {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

