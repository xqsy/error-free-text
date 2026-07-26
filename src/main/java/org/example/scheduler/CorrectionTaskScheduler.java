package org.example.scheduler;

import org.example.lifecycle.ProcessingTaskRecovery;
import org.example.service.CorrectionTaskService;
import org.example.service.TextCorrectionProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CorrectionTaskScheduler {

  private final CorrectionTaskService taskService;
  private final TextCorrectionProcessor correctionProcessor;
  private final ProcessingTaskRecovery processingTaskRecovery;

  public CorrectionTaskScheduler(
      CorrectionTaskService taskService,
      TextCorrectionProcessor correctionProcessor,
      ProcessingTaskRecovery processingTaskRecovery) {
    this.taskService = taskService;
    this.correctionProcessor = correctionProcessor;
    this.processingTaskRecovery = processingTaskRecovery;
  }

  @Scheduled(fixedDelayString = "${task.processing.polling-interval}")
  public void processNextTask() {
    if (!processingTaskRecovery.isPollingAllowed()) {
      return;
    }
    taskService.findOldestNewTaskId().ifPresent(correctionProcessor::process);
  }
}
