package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class NoCommentException extends CustomException {
  public NoCommentException() {
    super(ErrorCase.NO_SUCH_COMMENT_ERROR);
  }
}
