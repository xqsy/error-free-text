package org.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
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

    assertThat(recovery.isPollingAllowed()).isFalse();

    recovery.run(arguments);

    assertThat(recovery.isPollingAllowed()).isTrue();
    verify(taskService).failInterruptedTasks();
    verifyNoMoreInteractions(taskService);
  }

  @Test
  void keepsPollingBlockedWhenRecoveryFails() {
    ProcessingTaskRecovery recovery = new ProcessingTaskRecovery(taskService);
    RuntimeException recoveryFailure = new RuntimeException("Database unavailable");
    doThrow(recoveryFailure).when(taskService).failInterruptedTasks();

    assertThatThrownBy(() -> recovery.run(arguments)).isSameAs(recoveryFailure);

    assertThat(recovery.isPollingAllowed()).isFalse();
  }
}
