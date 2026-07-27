package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.client.YandexSpellerClient;
import org.example.dto.YandexSpellerError;
import org.example.entity.CorrectionTask;
import org.example.mapper.YandexSpellerResponseMapper;
import org.example.model.TextCorrection;
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
  private final YandexSpellerResponseMapper responseMapper = new YandexSpellerResponseMapper();
  private final TextCorrectionApplier correctionApplier = new TextCorrectionApplier();

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
      List<List<TextCorrection>> corrections = responseMapper.map(fragments, response);

      List<String> correctedFragments = new ArrayList<>(fragments.size());
      for (int index = 0; index < fragments.size(); index++) {
        correctedFragments.add(
            correctionApplier.apply(fragments.get(index), corrections.get(index)));
      }

      taskService.completeTask(taskId, String.join("", correctedFragments));
      LOGGER.info("Completed text correction: taskId={}", taskId);
    } catch (RuntimeException exception) {
      LOGGER.error("Text correction failed: taskId={}", taskId, exception);
      taskService.failTask(taskId, PROCESSING_FAILED);
    }
  }
}
