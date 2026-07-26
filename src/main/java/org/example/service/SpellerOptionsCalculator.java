package org.example.service;

import java.util.regex.Pattern;

final class SpellerOptionsCalculator {

  static final int IGNORE_DIGITS = 2;
  static final int IGNORE_URLS = 4;
  private static final Pattern URL_MARKER =
      Pattern.compile(
          "(?iu)(?<![\\p{L}\\p{N}_@.-])(?:https?://|ftp://|www\\.)(?=\\S)");

  int calculate(String text) {
    int options = text.codePoints().anyMatch(Character::isDigit) ? IGNORE_DIGITS : 0;
    if (URL_MARKER.matcher(text).find()) {
      options += IGNORE_URLS;
    }
    return options;
  }
}
