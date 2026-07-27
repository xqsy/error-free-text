package org.example.service;

import java.util.List;
import java.util.UUID;
import org.example.client.YandexSpellerClient;
import org.example.dto.YandexSpellerError;
import org.example.entity.CorrectionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TextCorrectionProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TextCorrectionProcessor.class);
  private static final String PROCESSING_FAILED = "Text correction failed";

  private final YandexSpellerClient spellerClient;
  private final CorrectionTaskService taskService;
  private final SpellerOptionsCalculator optionsCalculator = new SpellerOptionsCalculator();
  private final TextSplitter textSplitter = new TextSplitter();
  private final YandexSpellerResponseApplier responseApplier =
      new YandexSpellerResponseApplier();

  public TextCorrectionProcessor(
      YandexSpellerClient spellerClient, CorrectionTaskService taskService) {
    this.spellerClient = spellerClient;
    this.taskService = taskService;
  }

  public void process(UUID taskId) {
    CorrectionTask task = taskService.startProcessing(taskId);
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
      String correctedText = responseApplier.apply(fragments, response);
      taskService.completeTask(taskId, correctedText);
      LOGGER.info("Completed text correction: taskId={}", taskId);
    } catch (RuntimeException exception) {
      LOGGER.error("Text correction failed: taskId={}", taskId, exception);
      taskService.failTask(taskId, PROCESSING_FAILED);
    }
  }
}
