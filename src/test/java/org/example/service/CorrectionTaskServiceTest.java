package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.entity.CorrectionTask;
import org.example.entity.TaskStatus;
import org.example.exception.TaskNotFoundException;
import org.example.repository.CorrectionTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CorrectionTaskServiceTest {

  @Mock private CorrectionTaskRepository repository;

  private CorrectionTaskService taskService;

  @BeforeEach
  void setUp() {
    taskService = new CorrectionTaskService(repository);
  }

  @Test
  void createsNewTaskAndReturnsSavedIdentifier() {
    when(repository.save(any(CorrectionTask.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    ArgumentCaptor<CorrectionTask> taskCaptor =
        ArgumentCaptor.forClass(CorrectionTask.class);

    UUID taskId = taskService.createTask(" source text ", "en");

    verify(repository).save(taskCaptor.capture());
    CorrectionTask savedTask = taskCaptor.getValue();
    assertThat(taskId).isEqualTo(savedTask.getId());
    assertThat(savedTask.getSourceText()).isEqualTo(" source text ");
    assertThat(savedTask.getLanguage()).isEqualTo("en");
    assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.NEW);
  }

  @Test
  void returnsExistingTask() {
    CorrectionTask task = CorrectionTask.create("text", "en");
    when(repository.findById(task.getId())).thenReturn(Optional.of(task));

    assertThat(taskService.getTask(task.getId())).isSameAs(task);
  }

  @Test
  void throwsDomainExceptionWhenTaskDoesNotExist() {
    UUID taskId = UUID.randomUUID();
    when(repository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTask(taskId))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessage("Task with id: " + taskId + " not found");
  }

  @Test
  void returnsIdentifierOfOldestNewTaskSelectedByRepository() {
    CorrectionTask task = CorrectionTask.create("text", "ru");
    when(repository.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.NEW))
        .thenReturn(Optional.of(task));

    assertThat(taskService.findOldestNewTaskId()).contains(task.getId());
  }

  @Test
  void processesTaskThroughCompletion() {
    CorrectionTask task = CorrectionTask.create("text", "en");
    when(repository.findById(task.getId())).thenReturn(Optional.of(task));

    assertThat(taskService.startProcessing(task.getId()).getStatus())
        .isEqualTo(TaskStatus.PROCESSING);
    taskService.completeTask(task.getId(), "corrected");

    assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    assertThat(task.getCorrectedText()).isEqualTo("corrected");
  }

  @Test
  void failsProcessingTaskWithSafeMessage() {
    CorrectionTask task = CorrectionTask.create("text", "en");
    task.startProcessing();
    when(repository.findById(task.getId())).thenReturn(Optional.of(task));

    taskService.failTask(task.getId(), "safe message");

    assertThat(task.getStatus()).isEqualTo(TaskStatus.FAILED);
    assertThat(task.getErrorMessage()).isEqualTo("safe message");
  }

  @Test
  void marksAllInterruptedProcessingTasksAsFailed() {
    CorrectionTask firstTask = processingTask("first");
    CorrectionTask secondTask = processingTask("second");
    when(repository.findAllByStatus(TaskStatus.PROCESSING))
        .thenReturn(List.of(firstTask, secondTask));

    taskService.failInterruptedTasks();

    assertThat(firstTask.getStatus()).isEqualTo(TaskStatus.FAILED);
    assertThat(secondTask.getStatus()).isEqualTo(TaskStatus.FAILED);
    assertThat(firstTask.getErrorMessage()).isEqualTo("Text correction was interrupted");
    assertThat(secondTask.getErrorMessage()).isEqualTo("Text correction was interrupted");
  }

  private CorrectionTask processingTask(String text) {
    CorrectionTask task = CorrectionTask.create(text, "en");
    task.startProcessing();
    return task;
  }
}
