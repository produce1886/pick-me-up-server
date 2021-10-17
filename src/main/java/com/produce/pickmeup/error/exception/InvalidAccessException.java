package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class InvalidAccessException extends CustomException {
  public InvalidAccessException() {
    super(ErrorCase.INVALID_ACCESS_ERROR);
  }
}
