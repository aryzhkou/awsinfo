package com.barbariania.awsinfo.controller;

import com.barbariania.awsinfo.exception.FileStorageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javassist.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleException(NotFoundException exception) {
    return formatResponseEntity(exception, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(FileStorageException.class)
  public ResponseEntity<String> handleException(FileStorageException exception) {
    return formatResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<String> handleException(RuntimeException exception) {
    return formatResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<String> formatResponseEntity(Exception exception, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(exception.getMessage(), headers, status);
  }
}
