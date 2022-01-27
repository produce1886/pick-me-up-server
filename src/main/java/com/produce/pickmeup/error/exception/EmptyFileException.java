package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class EmptyFileException extends CustomException {
    public EmptyFileException() {
        super(ErrorCase.EMPTY_FILE_ERROR);
    }
}
