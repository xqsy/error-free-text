package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.dto.YandexSpellerError;
import org.junit.jupiter.api.Test;

class YandexSpellerResponseApplierTest {

  private final YandexSpellerResponseApplier responseApplier =
      new YandexSpellerResponseApplier();

  @Test
  void appliesMultipleLengthChangingCorrectionsUsingOriginalPositions() {
    List<String> fragments = List.of("helo ", "quik fox");
    List<List<YandexSpellerError>> response =
        List.of(
            List.of(error(0, 4, " hello ")),
            List.of(error(5, 3, "wolf"), error(0, 4, "quick")));

    assertThat(responseApplier.apply(fragments, response))
        .isEqualTo("hello quick wolf");
  }

  @Test
  void preservesTextWhenErrorHasNoSuggestion() {
    List<String> fragments = List.of("helo");
    List<List<YandexSpellerError>> response =
        List.of(List.of(new YandexSpellerError(0, 4, List.of())));

    assertThat(responseApplier.apply(fragments, response)).isEqualTo("helo");
  }

  private YandexSpellerError error(
      int position, int length, String suggestion) {
    return new YandexSpellerError(
        position, length, List.of(suggestion));
  }
}
