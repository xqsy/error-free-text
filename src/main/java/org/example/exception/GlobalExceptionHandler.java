package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.example.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final int VALIDATION_ERROR_CODE = 40001;
  private static final int MALFORMED_REQUEST_ERROR_CODE = 40002;
  private static final int TASK_NOT_FOUND_ERROR_CODE = 40401;
  private static final int METHOD_NOT_SUPPORTED_ERROR_CODE = 40501;
  private static final int MEDIA_TYPE_NOT_SUPPORTED_ERROR_CODE = 41501;
  private static final int INTERNAL_ERROR_CODE = 50001;
  private static final String VALIDATION_ERROR_MESSAGE = "Request validation failed";
  private static final String MALFORMED_REQUEST_ERROR_MESSAGE = "Request has an invalid format";
  private static final String METHOD_NOT_SUPPORTED_ERROR_MESSAGE = "HTTP method is not supported";
  private static final String MEDIA_TYPE_NOT_SUPPORTED_ERROR_MESSAGE = "Content type is not supported";
  private static final String INTERNAL_ERROR_MESSAGE = "Unexpected internal error";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(HttpServletRequest request) {
    return createResponse(
        HttpStatus.BAD_REQUEST,
        VALIDATION_ERROR_MESSAGE,
        VALIDATION_ERROR_CODE,
        request);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpServletRequest request) {
    return createResponse(
        HttpStatus.BAD_REQUEST,
        MALFORMED_REQUEST_ERROR_MESSAGE,
        MALFORMED_REQUEST_ERROR_CODE,
        request);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(
      TaskNotFoundException exception, HttpServletRequest request) {
    LOGGER.warn(
        "HTTP request failed: errorCode={}, path={}, taskId={}",
        TASK_NOT_FOUND_ERROR_CODE,
        request.getRequestURI(),
        exception.getTaskId());
    return createResponse(
        HttpStatus.NOT_FOUND,
        exception.getMessage(),
        TASK_NOT_FOUND_ERROR_CODE,
        request);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpServletRequest request) {
    LOGGER.warn(
        "HTTP request failed: errorCode={}, path={}",
        METHOD_NOT_SUPPORTED_ERROR_CODE,
        request.getRequestURI());
    return createResponse(
        HttpStatus.METHOD_NOT_ALLOWED,
        METHOD_NOT_SUPPORTED_ERROR_MESSAGE,
        METHOD_NOT_SUPPORTED_ERROR_CODE,
        request);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpServletRequest request) {
    LOGGER.warn(
        "HTTP request failed: errorCode={}, path={}",
        MEDIA_TYPE_NOT_SUPPORTED_ERROR_CODE,
        request.getRequestURI());
    return createResponse(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        MEDIA_TYPE_NOT_SUPPORTED_ERROR_MESSAGE,
        MEDIA_TYPE_NOT_SUPPORTED_ERROR_CODE,
        request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedError(
      Exception exception, HttpServletRequest request) {
    LOGGER.error(
        "Unexpected HTTP request processing error: errorCode={}, path={}",
        INTERNAL_ERROR_CODE,
        request.getRequestURI(),
        exception);
    return createResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        INTERNAL_ERROR_MESSAGE,
        INTERNAL_ERROR_CODE,
        request);
  }

  private ResponseEntity<ErrorResponse> createResponse(
      HttpStatus status, String message, int errorCode, HttpServletRequest request) {
    ErrorResponse response =
        new ErrorResponse(message, errorCode, Instant.now(), request.getRequestURI());
    return ResponseEntity.status(status).body(response);
  }
}
