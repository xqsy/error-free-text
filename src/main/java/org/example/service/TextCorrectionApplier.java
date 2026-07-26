package org.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.example.model.TextCorrection;

public final class TextCorrectionApplier {

  public String apply(String text, List<TextCorrection> corrections) {
    Objects.requireNonNull(text, "text");
    Objects.requireNonNull(corrections, "corrections");

    List<TextCorrection> orderedCorrections = new ArrayList<>(corrections);
    for (TextCorrection correction : orderedCorrections) {
      Objects.requireNonNull(correction, "correction");
    }
    orderedCorrections.sort(
        Comparator.comparingInt(TextCorrection::position).reversed());

    StringBuilder correctedText = new StringBuilder(text);
    int nextCorrectionStart = text.length();
    for (TextCorrection correction : orderedCorrections) {
      int correctionEnd =
          validateRange(correction, text.length(), nextCorrectionStart);
      if (correction.replacement() != null) {
        correctedText.replace(
            correction.position(), correctionEnd, correction.replacement());
      }
      nextCorrectionStart = correction.position();
    }

    return correctedText.toString();
  }

  private int validateRange(TextCorrection correction, int textLength, int nextCorrectionStart) {

    int position = correction.position();
    int length = correction.length();

    if (!correction.isWithin(textLength)) {
      throw new IllegalArgumentException("Correction range is outside text");
    }

    int correctionEnd = position + length;
    
    if (correctionEnd > nextCorrectionStart) {
      throw new IllegalArgumentException("Correction ranges overlap");
    }

    return correctionEnd;
  }
}
