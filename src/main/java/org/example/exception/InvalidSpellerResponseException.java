package org.example.exception;

public class InvalidSpellerResponseException extends RuntimeException {

  public InvalidSpellerResponseException(String message) {
    super(message);
  }
}
