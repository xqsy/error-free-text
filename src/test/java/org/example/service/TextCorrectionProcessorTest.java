package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.example.client.YandexSpellerClient;
import org.example.dto.YandexSpellerError;
import org.example.entity.CorrectionTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

@ExtendWith(MockitoExtension.class)
class TextCorrectionProcessorTest {

  @Mock private YandexSpellerClient spellerClient;
  @Mock private CorrectionTaskService taskService;

  private TextCorrectionProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new TextCorrectionProcessor(spellerClient, taskService);
  }

  @Test
  void sendsAllFragmentsInOneArrayAndOneSpellerCall() {
    UUID taskId = UUID.randomUUID();
    String sourceText = "a".repeat(10001);
    when(taskService.startProcessing(taskId)).thenReturn(processingTask(sourceText));
    when(spellerClient.checkTexts(anyList(), anyString(), anyInt()))
        .thenReturn(List.of(List.of(), List.of()));
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<String>> fragmentsCaptor =
        ArgumentCaptor.forClass(List.class);

    processor.process(taskId);

    verify(spellerClient, times(1))
        .checkTexts(fragmentsCaptor.capture(), eq("en"), eq(0));
    assertThat(fragmentsCaptor.getValue())
        .containsExactly("a".repeat(10000), "a");
    verify(taskService).completeTask(taskId, sourceText);
  }

  @Test
  void preservesFragmentOrderAndSeparatorsWhenApplyingCorrections() {
    UUID taskId = UUID.randomUUID();
    String prefix = "a".repeat(9999) + " ";
    String sourceText = prefix + "helo wrld";
    when(taskService.startProcessing(taskId)).thenReturn(processingTask(sourceText));
    when(spellerClient.checkTexts(anyList(), eq("en"), eq(0)))
        .thenReturn(
            List.of(
                List.of(),
                List.of(
                    spellerError(0, 4, " hello "),
                    spellerError(5, 4, "world"))));

    processor.process(taskId);

    verify(taskService).completeTask(taskId, prefix + "hello world");
  }

  @Test
  void failsTaskAfterSingleExternalApiFailure() {
    UUID taskId = UUID.randomUUID();
    when(taskService.startProcessing(taskId)).thenReturn(processingTask("source text"));
    when(spellerClient.checkTexts(anyList(), anyString(), anyInt()))
        .thenThrow(new ResourceAccessException("unavailable"));

    processor.process(taskId);

    verify(spellerClient, times(1)).checkTexts(anyList(), eq("en"), eq(0));
    verify(taskService)
        .failTask(taskId, "Text correction failed");
    verify(taskService, never()).completeTask(eq(taskId), anyString());
  }

  private CorrectionTask processingTask(String sourceText) {
    CorrectionTask task = CorrectionTask.create(sourceText, "en");
    task.startProcessing();
    return task;
  }

  private YandexSpellerError spellerError(
      int position, int length, String suggestion) {
    return new YandexSpellerError(
        position, length, List.of(suggestion));
  }
}
