package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class InvalidFieldException extends CustomException {
  public InvalidFieldException() {
    super(ErrorCase.INVALID_FIELD_ERROR);
  }
}
