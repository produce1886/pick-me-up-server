package com.produce.pickmeup.error;

import com.produce.pickmeup.error.exception.CustomException;
import com.produce.pickmeup.error.exception.EmptyFileException;
import com.produce.pickmeup.error.exception.FileConvertException;
import com.produce.pickmeup.error.exception.InvalidAccessException;
import com.produce.pickmeup.error.exception.InvalidFieldException;
import com.produce.pickmeup.error.exception.InvalidFileException;
import com.produce.pickmeup.error.exception.NoCommentException;
import com.produce.pickmeup.error.exception.NoImageException;
import com.produce.pickmeup.error.exception.NoPortfolioException;
import com.produce.pickmeup.error.exception.NoProjectException;
import com.produce.pickmeup.error.exception.NoUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

  // NOT FOUND
  @ExceptionHandler(NoUserException.class)
  protected ResponseEntity<ErrorMessage> handleNoUserException(NoUserException e) {
    return handle(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(NoPortfolioException.class)
  protected ResponseEntity<ErrorMessage> handleNoPortfolioException(NoPortfolioException e) {
    return handle(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(NoProjectException.class)
  protected ResponseEntity<ErrorMessage> handleNoProjectException(NoProjectException e) {
    return handle(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(NoCommentException.class)
  protected ResponseEntity<ErrorMessage> handleNoCommentException(NoCommentException e) {
    return handle(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(NoImageException.class)
  protected ResponseEntity<ErrorMessage> handleNoImageException(NoImageException e) {
    return handle(HttpStatus.NOT_FOUND, e);
  }

  // FILE
  @ExceptionHandler(EmptyFileException.class)
  protected ResponseEntity<ErrorMessage> handleEmptyFileException(EmptyFileException e) {
    return handle(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(FileConvertException.class)
  protected ResponseEntity<ErrorMessage> handleFileConvertException(FileConvertException e) {
    return handle(HttpStatus.INTERNAL_SERVER_ERROR, e);
  }

  @ExceptionHandler(InvalidFileException.class)
  protected ResponseEntity<ErrorMessage> handleInvalidFileException(InvalidFileException e) {
    return handle(HttpStatus.BAD_REQUEST, e);
  }

  // INVALID REQUEST
  @ExceptionHandler(InvalidAccessException.class)
  protected ResponseEntity<ErrorMessage> handleInvalidAccessException(InvalidAccessException e) {
    return handle(HttpStatus.FORBIDDEN, e);
  }

  @ExceptionHandler(InvalidFieldException.class)
  protected ResponseEntity<ErrorMessage> handleInvalidFieldException(InvalidFieldException e) {
    return handle(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorMessage("UNKNOWN_ERROR", "알 수 없는 에러가 발생하였습니다. "));
  }

  private ResponseEntity<ErrorMessage> handle(HttpStatus status, CustomException e) {
    return ResponseEntity.status(status).body(e.errorMessage());
  }
}

