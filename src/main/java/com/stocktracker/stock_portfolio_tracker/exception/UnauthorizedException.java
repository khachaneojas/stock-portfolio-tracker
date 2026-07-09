package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException() {
        super("Unauthorized access. You do not have the necessary permissions to access this resource.", HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(message,  HttpStatus.UNAUTHORIZED);
    }

}
