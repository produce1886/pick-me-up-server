package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;
import com.produce.pickmeup.error.ErrorMessage;

public abstract class CustomException extends RuntimeException {

    private final ErrorCase errorCase;

    protected CustomException(ErrorCase errorCase) {
        this.errorCase = errorCase;
    }

    public ErrorMessage errorMessage() {
        return ErrorMessage.of(errorCase.name(), errorCase.getMessage());
    }
}

