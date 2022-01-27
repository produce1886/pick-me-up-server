package com.produce.pickmeup.error.exception;

import com.produce.pickmeup.error.ErrorCase;

public class NoPortfolioException extends CustomException {
    public NoPortfolioException() {
        super(ErrorCase.NO_SUCH_PORTFOLIO_ERROR);
    }
}
