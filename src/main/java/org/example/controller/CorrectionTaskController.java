package org.example.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import org.example.dto.CreateTaskRequest;
import org.example.dto.CreateTaskResponse;
import org.example.dto.TaskResponse;
import org.example.entity.CorrectionTask;
import org.example.service.CorrectionTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class CorrectionTaskController {

  private final CorrectionTaskService taskService;

  public CorrectionTaskController(CorrectionTaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateTaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
    return new CreateTaskResponse(taskService.createTask(request.text(), request.language()));
  }

  @GetMapping("/{id}")
  public TaskResponse getTask(@PathVariable("id") UUID id) {
    CorrectionTask task = taskService.getTask(id);
    return new TaskResponse(
        task.getStatus(),
        task.getCorrectedText(),
        task.getErrorMessage());
  }
}
