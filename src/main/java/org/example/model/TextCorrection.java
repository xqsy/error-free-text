package org.example.model;

public record TextCorrection(int position, int length, String replacement) {

  public boolean isWithin(int textLength) {
    return position >= 0
        && length >= 0
        && position <= textLength
        && length <= textLength - position;
  }
}
