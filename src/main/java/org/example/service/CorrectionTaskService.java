package org.example.service;

import java.util.UUID;
import org.example.entity.CorrectionTask;
import org.example.exception.TaskNotFoundException;
import org.example.repository.CorrectionTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CorrectionTaskService {
  private final CorrectionTaskRepository repository;

  public CorrectionTaskService(CorrectionTaskRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public UUID createTask(String sourceText, String language) {
    CorrectionTask savedTask = repository.save(CorrectionTask.create(sourceText, language));
    return savedTask.getId();
  }

  public CorrectionTask getTask(UUID taskId) {
    return repository
        .findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
  }
}
