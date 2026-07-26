package org.example.service;

import java.util.regex.Pattern;

final class UrlDetector {

  private static final Pattern URL_MARKER =
      Pattern.compile(
          "(?iu)(?<![\\p{L}\\p{N}_@.-])(?:https?://|ftp://|www\\.)(?=\\S)");

  boolean containsUrl(String text) {
    return URL_MARKER.matcher(text).find();
  }
}
