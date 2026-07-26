package org.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.example.model.TextCorrection;

final class TextCorrectionApplier {

  String apply(String text, List<TextCorrection> corrections) {
    List<TextCorrection> orderedCorrections = new ArrayList<>(corrections);
    orderedCorrections.sort(
        Comparator.comparingInt(TextCorrection::position).reversed());

    StringBuilder correctedText = new StringBuilder(text);
    int nextCorrectionStart = text.length();
    for (TextCorrection correction : orderedCorrections) {
      int correctionEnd = validateRange(correction, nextCorrectionStart);
      if (correction.replacement() != null) {
        correctedText.replace(
            correction.position(), correctionEnd, correction.replacement());
      }
      nextCorrectionStart = correction.position();
    }

    return correctedText.toString();
  }

  private int validateRange(TextCorrection correction, int nextCorrectionStart) {
    int correctionEnd = correction.position() + correction.length();
    
    if (correctionEnd > nextCorrectionStart) {
      throw new IllegalArgumentException("Correction ranges overlap");
    }

    return correctionEnd;
  }
}
