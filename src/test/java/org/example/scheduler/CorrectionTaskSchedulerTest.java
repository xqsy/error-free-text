package org.example.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import org.example.service.CorrectionTaskService;
import org.example.service.TextCorrectionProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

@ExtendWith(MockitoExtension.class)
class CorrectionTaskSchedulerTest {

  @Mock private CorrectionTaskService taskService;
  @Mock private TextCorrectionProcessor correctionProcessor;

  private CorrectionTaskScheduler scheduler;

  @BeforeEach
  void setUp() {
    scheduler = new CorrectionTaskScheduler(taskService, correctionProcessor);
  }

  @Test
  void processesIdentifierOfOldestNewTask() {
    UUID taskId = UUID.randomUUID();
    when(taskService.findOldestNewTaskId()).thenReturn(Optional.of(taskId));

    scheduler.processNextTask();

    InOrder calls = inOrder(taskService, correctionProcessor);
    calls.verify(taskService).findOldestNewTaskId();
    calls.verify(correctionProcessor).process(taskId);
  }

  @Test
  void doesNotInvokeProcessorWhenQueueIsEmpty() {
    when(taskService.findOldestNewTaskId()).thenReturn(Optional.empty());

    scheduler.processNextTask();

    verify(correctionProcessor, never()).process(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void usesFixedDelaySoNextPollStartsAfterCurrentProcessingReturns()
      throws NoSuchMethodException {
    Method method = CorrectionTaskScheduler.class.getMethod("processNextTask");
    Scheduled schedule = method.getAnnotation(Scheduled.class);

    assertThat(schedule).isNotNull();
    assertThat(schedule.fixedDelayString())
        .isEqualTo("${task.processing.polling-interval}");
    assertThat(schedule.fixedRate()).isEqualTo(-1);
    assertThat(schedule.fixedRateString()).isEmpty();
  }
}
