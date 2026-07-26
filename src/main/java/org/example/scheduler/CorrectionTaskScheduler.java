package org.example.scheduler;

import org.example.service.CorrectionTaskService;
import org.example.service.TextCorrectionProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CorrectionTaskScheduler {

  private final CorrectionTaskService taskService;
  private final TextCorrectionProcessor correctionProcessor;

  public CorrectionTaskScheduler(
      CorrectionTaskService taskService, TextCorrectionProcessor correctionProcessor) {
    this.taskService = taskService;
    this.correctionProcessor = correctionProcessor;
  }

  @Scheduled(fixedDelayString = "${task.processing.polling-interval}")
  public void processNextTask() {
    taskService.findOldestNewTaskId().ifPresent(correctionProcessor::process);
  }
}
