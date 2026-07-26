package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.model.TextCorrection;
import org.junit.jupiter.api.Test;

class TextCorrectionApplierTest {

  private final TextCorrectionApplier applier = new TextCorrectionApplier();

  @Test
  void appliesMultipleLengthChangingCorrectionsUsingOriginalPositions() {
    String text = "helo quik fox";
    List<TextCorrection> corrections =
        List.of(
            new TextCorrection(10, 3, "wolf"),
            new TextCorrection(0, 4, "hello"),
            new TextCorrection(5, 4, "quick"));

    assertThat(applier.apply(text, corrections)).isEqualTo("hello quick wolf");
  }
}
