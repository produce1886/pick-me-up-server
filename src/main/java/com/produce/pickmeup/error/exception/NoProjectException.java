package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class NoProjectException extends CustomException {
    public NoProjectException() {
        super(ErrorCase.NO_SUCH_PROJECT_ERROR);
    }
}
