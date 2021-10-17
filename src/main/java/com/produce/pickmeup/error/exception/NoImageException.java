package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class NoImageException extends CustomException {
  public NoImageException() {
    super(ErrorCase.NO_SUCH_IMAGE_ERROR);
  }
}
