package com.barbariania.awsinfo.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.barbariania.awsinfo.exception.FileStorageException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<List<String>> handleException(MethodArgumentNotValidException exception) {
    final List<String> errors =
        exception.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
    return formatResponseEntity(errors, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  private ResponseEntity<String> formatResponseEntity(Exception exception, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(exception.getMessage(), headers, status);
  }

  private ResponseEntity<List<String>> formatResponseEntity(List<String> errors, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(errors, headers, status);
  }
}
