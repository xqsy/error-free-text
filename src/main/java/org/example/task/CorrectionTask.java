package org.example.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "correction_tasks")
public class CorrectionTask {
  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "source_text", nullable = false, columnDefinition = "TEXT")
  private String sourceText;

  @Column(nullable = false, length = 2)
  private String language;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TaskStatus status;

  @Column(name = "corrected_text", columnDefinition = "TEXT")
  private String correctedText;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected CorrectionTask() {}

  public UUID getId() {
    return id;
  }

  public String getSourceText() {
    return sourceText;
  }

  public String getLanguage() {
    return language;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public String getCorrectedText() {
    return correctedText;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
