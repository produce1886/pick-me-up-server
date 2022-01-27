package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class InvalidFileException extends CustomException {
    public InvalidFileException() {
        super(ErrorCase.INVALID_FILE_TYPE_ERROR);
    }
}
