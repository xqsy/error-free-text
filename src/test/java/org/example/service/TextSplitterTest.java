package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TextSplitterTest {

  private final TextSplitter splitter = new TextSplitter();

  @ParameterizedTest
  @CsvSource({
    "9999, 1, 9999",
    "10000, 1, 10000",
    "10001, 2, 10000"
  })
  void splitsAtConfiguredLengthBoundaries(
      int textLength, int expectedFragmentCount, int expectedFirstFragmentLength) {
    String text = "a".repeat(textLength);

    List<String> fragments = splitter.split(text);

    assertThat(fragments).hasSize(expectedFragmentCount);
    assertThat(fragments.getFirst()).hasSize(expectedFirstFragmentLength);
    assertThat(String.join("", fragments)).isEqualTo(text);
  }

  @Test
  void doesNotSplitEmojiSurrogatePair() {
    String text = "a".repeat(9999) + "\uD83D\uDE00" + "b";

    List<String> fragments = splitter.split(text);

    assertThat(fragments).hasSize(2);
    assertThat(fragments.getFirst()).hasSize(9999);
    assertThat(fragments.get(1).codePointAt(0)).isEqualTo(0x1F600);
    assertThat(String.join("", fragments)).isEqualTo(text);
  }
}
