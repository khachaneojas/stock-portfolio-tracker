package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    public ForbiddenException() {
        super("You do not have permission to perform this action.", HttpStatus.FORBIDDEN);
    }
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
