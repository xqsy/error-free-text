package org.example.service;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.client.YandexSpellerClient;
import org.example.dto.YandexSpellerError;
import org.example.entity.CorrectionTask;
import org.example.exception.InvalidSpellerResponseException;
import org.example.mapper.YandexSpellerResponseMapper;
import org.example.model.TextCorrection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class TextCorrectionProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TextCorrectionProcessor.class);
  private static final String REQUEST_TIMED_OUT =
      "Text correction service request timed out";
  private static final String SERVICE_UNAVAILABLE =
      "Text correction service is unavailable";
  private static final String SERVICE_ERROR =
      "Text correction service returned an error";
  private static final String INVALID_RESPONSE =
      "Text correction service returned an invalid response";
  private static final String RESULT_PROCESSING_FAILED =
      "Failed to process text correction result";
  private static final String UNEXPECTED_ERROR =
      "Unexpected text correction error";

  private final YandexSpellerClient spellerClient;
  private final CorrectionTaskService taskService;
  private final SpellerOptionsCalculator optionsCalculator = new SpellerOptionsCalculator();
  private final TextSplitter textSplitter = new TextSplitter();
  private final YandexSpellerResponseMapper responseMapper = new YandexSpellerResponseMapper();
  private final TextCorrectionApplier correctionApplier = new TextCorrectionApplier();

  public TextCorrectionProcessor(
      YandexSpellerClient spellerClient, CorrectionTaskService taskService) {
    this.spellerClient = spellerClient;
    this.taskService = taskService;
  }

  public void process(UUID taskId) {
    CorrectionTask task = taskService.startProcessing(taskId);
    long startedAt = System.nanoTime();
    try {
      List<String> fragments = textSplitter.split(task.getSourceText());
      int options = optionsCalculator.calculate(task.getSourceText());
      LOGGER.info(
          "Starting text correction: taskId={}, fragmentCount={}, options={}",
          taskId,
          fragments.size(),
          options);
      List<List<YandexSpellerError>> response =
          spellerClient.checkTexts(fragments, task.getLanguage(), options);
      List<List<TextCorrection>> corrections = responseMapper.map(fragments, response);

      List<String> correctedFragments = new ArrayList<>(fragments.size());
      for (int index = 0; index < fragments.size(); index++) {
        correctedFragments.add(
            correctionApplier.apply(fragments.get(index), corrections.get(index)));
      }

      taskService.completeTask(taskId, textSplitter.join(correctedFragments));
      long durationMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
      LOGGER.info(
          "Completed text correction: taskId={}, durationMs={}", taskId, durationMillis);
    } catch (RuntimeException exception) {
      String errorMessage = failureMessage(exception);
      logFailure(taskId, errorMessage, exception);
      taskService.failTask(taskId, errorMessage);
    }
  }

  private void logFailure(UUID taskId, String errorMessage, RuntimeException exception) {
    if (UNEXPECTED_ERROR.equals(errorMessage)) {
      LOGGER.error("Unexpected text correction error: taskId={}", taskId, exception);
      return;
    }
    LOGGER.warn(
        "Text correction failed: taskId={}, reason={}", taskId, errorMessage, exception);
  }

  private String failureMessage(RuntimeException exception) {
    if (hasCause(exception, HttpTimeoutException.class)
        || hasCause(exception, SocketTimeoutException.class)) {
      return REQUEST_TIMED_OUT;
    }
    if (exception instanceof ResourceAccessException) {
      return SERVICE_UNAVAILABLE;
    }
    if (exception instanceof RestClientResponseException) {
      return SERVICE_ERROR;
    }
    if (exception instanceof InvalidSpellerResponseException
        || exception instanceof RestClientException) {
      return INVALID_RESPONSE;
    }
    if (exception instanceof IllegalArgumentException) {
      return RESULT_PROCESSING_FAILED;
    }
    return UNEXPECTED_ERROR;
  }

  private boolean hasCause(Throwable throwable, Class<? extends Throwable> causeType) {
    Throwable current = throwable;
    while (current != null) {
      if (causeType.isInstance(current)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
