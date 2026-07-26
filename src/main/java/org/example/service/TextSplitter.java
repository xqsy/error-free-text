package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TextSplitter {

  public static final int MAX_FRAGMENT_LENGTH = 10000;

  public List<String> split(String text) {
    Objects.requireNonNull(text, "text");

    List<String> fragments = new ArrayList<>();
    int fragmentStart = 0;

    while (fragmentStart < text.length()) {
      int fragmentEnd = Math.min(fragmentStart + MAX_FRAGMENT_LENGTH, text.length());

      if (fragmentEnd < text.length()) {
        fragmentEnd = adjustFragmentEnd(text, fragmentStart, fragmentEnd);
      }

      fragments.add(text.substring(fragmentStart, fragmentEnd));
      fragmentStart = fragmentEnd;
    }

    return List.copyOf(fragments);
  }

  private int adjustFragmentEnd(String text, int fragmentStart, int maximumEnd) {

    for (int index = maximumEnd - 1; index >= fragmentStart; index--) {
      char character = text.charAt(index);

      if (character == '\n' || character == ' ') {
        return index + 1;
      }
    }

    return adjustEndToAvoidSplittingSurrogatePair(text, maximumEnd);
  }

  private int adjustEndToAvoidSplittingSurrogatePair(String text, int fragmentEnd) {
    if (Character.isHighSurrogate(text.charAt(fragmentEnd - 1))
        && Character.isLowSurrogate(text.charAt(fragmentEnd))) {
      return fragmentEnd - 1;
    }

    return fragmentEnd;
  }
}
