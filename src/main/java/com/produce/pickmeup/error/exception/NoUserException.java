package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class NoUserException extends CustomException {
    public NoUserException() {
        super(ErrorCase.NO_SUCH_USER_ERROR);
    }
}
