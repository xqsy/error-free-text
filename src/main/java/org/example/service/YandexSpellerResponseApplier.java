package org.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.example.dto.YandexSpellerError;

final class YandexSpellerResponseApplier {

  String apply(List<String> fragments, List<List<YandexSpellerError>> response) {
    StringBuilder correctedText = new StringBuilder();
    for (int index = 0; index < fragments.size(); index++) {
      correctedText.append(applyToFragment(fragments.get(index), response.get(index)));
    }
    return correctedText.toString();
  }

  private String applyToFragment(String fragment, List<YandexSpellerError> errors) {      
    List<YandexSpellerError> orderedErrors = new ArrayList<>(errors);
    orderedErrors.sort(Comparator.comparingInt(YandexSpellerError::position).reversed());

    StringBuilder correctedFragment = new StringBuilder(fragment);
    for (YandexSpellerError error : orderedErrors) {
      int errorEnd = error.position() + error.length();

      String replacement = firstSuggestion(error.suggestions());
      if (replacement != null) {
        correctedFragment.replace(
            error.position(), errorEnd, replacement);
      }
    }
    return correctedFragment.toString();
  }

  private String firstSuggestion(List<String> suggestions) {
    if (suggestions == null || suggestions.isEmpty()) {
      return null;
    }
    return suggestions.getFirst().strip();
  }
}
