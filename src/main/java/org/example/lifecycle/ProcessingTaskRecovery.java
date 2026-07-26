package org.example.lifecycle;

import java.util.concurrent.atomic.AtomicBoolean;
import org.example.service.CorrectionTaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ProcessingTaskRecovery implements ApplicationRunner {

  private final CorrectionTaskService taskService;
  private final AtomicBoolean pollingAllowed = new AtomicBoolean();

  public ProcessingTaskRecovery(CorrectionTaskService taskService) {
    this.taskService = taskService;
  }

  @Override
  public void run(ApplicationArguments arguments) {
    taskService.failInterruptedTasks();
    pollingAllowed.set(true);
  }

  public boolean isPollingAllowed() {
    return pollingAllowed.get();
  }
}
