package org.example.lifecycle;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.example.service.CorrectionTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
class ProcessingTaskRecoveryTest {

  @Mock private CorrectionTaskService taskService;
  @Mock private ApplicationArguments arguments;

  @Test
  void failsInterruptedTasksAtStartupWithoutStartingCorrection() {
    ProcessingTaskRecovery recovery = new ProcessingTaskRecovery(taskService);

    recovery.run(arguments);

    verify(taskService).failInterruptedTasks();
    verifyNoMoreInteractions(taskService);
  }
}
