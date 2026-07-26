package org.example.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.example.dto.CreateTaskRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CreateTaskRequestValidationTest {

  private static Validator validator;

  @BeforeAll
  static void createValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @ParameterizedTest
  @ValueSource(strings = {"en", "ru"})
  void acceptsSupportedLanguages(String language) {
    assertThat(violationsFor("valid text", language, "language")).isEmpty();
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "EN", "de", " en "})
  void rejectsUnsupportedLanguages(String language) {
    assertThat(violationsFor("valid text", language, "language")).isNotEmpty();
  }

  @ParameterizedTest
  @MethodSource("validTexts")
  void acceptsTextWithAtLeastThreeCodePointsAndALetter(String text) {
    assertThat(violationsFor(text, "en", "text")).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidTexts")
  void rejectsTextThatDoesNotMeetContentRules(String text) {
    assertThat(violationsFor(text, "en", "text")).isNotEmpty();
  }

  private static Stream<String> validTexts() {
    return Stream.of("abc", "  abc  ", "\uD801\uDC00ab", "я12");
  }

  private static Stream<String> invalidTexts() {
    return Stream.of("", "  ab  ", "\uD83D\uDE00a", "12!", " _! ");
  }

  private Set<ConstraintViolation<CreateTaskRequest>> violationsFor(
      String text, String language, String property) {
    return validator.validate(new CreateTaskRequest(text, language)).stream()
        .filter(violation -> violation.getPropertyPath().toString().equals(property))
        .collect(java.util.stream.Collectors.toSet());
  }
}
