package org.example.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.entity.CorrectionTask;
import org.example.entity.TaskStatus;
import org.example.exception.TaskNotFoundException;
import org.example.repository.CorrectionTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CorrectionTaskService {

  private static final String INTERRUPTED_ERROR_MESSAGE = "Text correction was interrupted";
  private static final Logger LOGGER = LoggerFactory.getLogger(CorrectionTaskService.class);

  private final CorrectionTaskRepository repository;

  public CorrectionTaskService(CorrectionTaskRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public UUID createTask(String sourceText, String language) {
    CorrectionTask savedTask = repository.save(CorrectionTask.create(sourceText, language));
    LOGGER.info(
        "Created correction task: taskId={}, language={}, textLength={}",
        savedTask.getId(),
        language,
        sourceText.length());
    return savedTask.getId();
  }

  public CorrectionTask getTask(UUID taskId) {
    return repository
        .findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
  }

  public Optional<UUID> findOldestNewTaskId() {
    return repository
        .findFirstByStatusOrderByCreatedAtAsc(TaskStatus.NEW)
        .map(CorrectionTask::getId);
  }

  @Transactional
  public void failInterruptedTasks() {
    List<CorrectionTask> interruptedTasks =
        repository.findAllByStatus(TaskStatus.PROCESSING);
    interruptedTasks.forEach(task -> task.fail(INTERRUPTED_ERROR_MESSAGE));
    LOGGER.info("Recovered interrupted correction tasks: count={}", interruptedTasks.size());
  }

  @Transactional
  public CorrectionTask startProcessing(UUID taskId) {
    CorrectionTask task = getTask(taskId);
    task.startProcessing();
    return task;
  }

  @Transactional
  public void completeTask(UUID taskId, String correctedText) {
    CorrectionTask task = getTask(taskId);
    task.complete(correctedText);
  }

  @Transactional
  public void failTask(UUID taskId, String errorMessage) {
    CorrectionTask task = getTask(taskId);
    task.fail(errorMessage);
  }
}
