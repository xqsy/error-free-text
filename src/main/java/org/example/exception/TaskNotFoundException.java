package org.example.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(UUID taskId) {
    super("Task with id: " + taskId + " not found");
  }
}
