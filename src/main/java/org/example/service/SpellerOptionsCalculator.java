package org.example.service;

import java.util.Objects;

public final class SpellerOptionsCalculator {

  public static final int IGNORE_DIGITS = 2;
  public static final int IGNORE_URLS = 4;

  private final UrlDetector urlDetector = new UrlDetector();

  public int calculate(String text) {
    Objects.requireNonNull(text, "text");

    int options = text.codePoints().anyMatch(Character::isDigit) ? IGNORE_DIGITS : 0;
    if (urlDetector.containsUrl(text)) {
      options += IGNORE_URLS;
    }
    return options;
  }
}
