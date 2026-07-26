package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SpellerOptionsCalculatorTest {

  private final SpellerOptionsCalculator calculator = new SpellerOptionsCalculator();

  @ParameterizedTest
  @MethodSource("optionCases")
  void calculatesExpectedOptions(String text, int expectedOptions) {
    assertThat(calculator.calculate(text)).isEqualTo(expectedOptions);
  }

  @ParameterizedTest
  @ValueSource(strings = {"text 1", "text \u0661"})
  void enablesIgnoreDigitsForAsciiAndUnicodeDigits(String text) {
    assertThat(calculator.calculate(text))
        .isEqualTo(SpellerOptionsCalculator.IGNORE_DIGITS);
  }

  @ParameterizedTest
  @MethodSource("urlMarkerCases")
  void detectsSupportedUrlMarkersInDifferentCaseAndSurroundings(String text) {
    assertThat(calculator.calculate(text))
        .isEqualTo(SpellerOptionsCalculator.IGNORE_URLS);
  }

  @ParameterizedTest
  @MethodSource("nonUrlCases")
  void ignoresMarkersThatDoNotStartASeparateFragment(String text) {
    assertThat(calculator.calculate(text)).isZero();
  }

  private static Stream<Arguments> optionCases() {
    return Stream.of(
        Arguments.of("plain text", 0),
        Arguments.of("text 1", 2),
        Arguments.of("visit https://example.com", 4),
        Arguments.of("visit https://example.com/page1", 6));
  }

  private static Stream<String> urlMarkerCases() {
    return Stream.of(
        "http://example.com",
        "(HTTPS://example.com)",
        "prefix\nFtP://host/path",
        "open WWW.example.com");
  }

  private static Stream<String> nonUrlCases() {
    return Stream.of(
        "abchttp://example.com",
        "mail@www.example.com",
        "name.www.example.com",
        "http:// ");
  }
}
