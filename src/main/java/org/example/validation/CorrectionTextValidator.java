package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CorrectionTextValidator implements ConstraintValidator<ValidCorrectionText, String> {

  @Override
  public boolean isValid(String text, ConstraintValidatorContext context) {
    if (text == null) {
      return true;
    }

    String strippedText = text.strip();
    int codePointCount = strippedText.codePointCount(0, strippedText.length());
    return codePointCount >= 3 && strippedText.codePoints().anyMatch(Character::isLetter);
  }
}
