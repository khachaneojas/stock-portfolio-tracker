package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class JwtException extends ApiException {

    public JwtException(String message, HttpStatus status) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
