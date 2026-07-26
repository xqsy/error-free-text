package org.example.mapper;

import java.util.ArrayList;
import java.util.List;
import org.example.dto.YandexSpellerError;
import org.example.exception.InvalidSpellerResponseException;
import org.example.model.TextCorrection;

public final class YandexSpellerResponseMapper {

  public List<List<TextCorrection>> map(
      List<String> fragments, List<List<YandexSpellerError>> response) {
    if (response == null) {
      throw new InvalidSpellerResponseException("Response is null");
    }
    if (response.size() != fragments.size()) {
      throw new InvalidSpellerResponseException(
          "Response fragment count does not match request");
    }

    List<List<TextCorrection>> correctionsByFragment =
        new ArrayList<>(fragments.size());
    for (int index = 0; index < fragments.size(); index++) {
      String fragment = fragments.get(index);
      List<YandexSpellerError> errors = response.get(index);
      if (errors == null) {
        throw new InvalidSpellerResponseException("Response error list is null");
      }

      List<TextCorrection> corrections = new ArrayList<>(errors.size());
      for (YandexSpellerError error : errors) {
        corrections.add(toCorrection(fragment, error));
      }
      correctionsByFragment.add(List.copyOf(corrections));
    }
    return List.copyOf(correctionsByFragment);
  }

  private TextCorrection toCorrection(String fragment, YandexSpellerError error) {
    if (error == null || error.position() == null || error.length() == null) {
      throw new InvalidSpellerResponseException("Response error is incomplete");
    }

    TextCorrection correction =
        new TextCorrection(
            error.position(), error.length(), firstSuggestion(error.suggestions()));
    if (!correction.isWithin(fragment.length())) {
      throw new InvalidSpellerResponseException(
          "Response error range is outside fragment");
    }

    return correction;
  }

  private String firstSuggestion(List<String> suggestions) {
    if (suggestions == null || suggestions.isEmpty()) {
      return null;
    }

    String suggestion = suggestions.getFirst();
    if (suggestion == null) {
      throw new InvalidSpellerResponseException("Response suggestion is null");
    }

    String replacement = suggestion.strip();
    return replacement.isEmpty() ? null : replacement;
  }
}
