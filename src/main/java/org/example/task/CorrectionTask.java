package org.example.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "correction_tasks",
    indexes = @Index(name = "idx_correction_tasks_status_created_at", columnList = "status, created_at"))
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

  public static CorrectionTask create(String sourceText, String language) {
    CorrectionTask task = new CorrectionTask();
    Instant now = Instant.now();
    task.id = UUID.randomUUID();
    task.sourceText = sourceText;
    task.language = language;
    task.status = TaskStatus.NEW;
    task.createdAt = now;
    task.updatedAt = now;
    return task;
  }

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
