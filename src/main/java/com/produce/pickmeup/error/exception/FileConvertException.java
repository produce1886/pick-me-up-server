package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class FileConvertException extends CustomException {
  public FileConvertException() {
    super(ErrorCase.FAIL_FILE_CONVERT_ERROR);
  }
}
