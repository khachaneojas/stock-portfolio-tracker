package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends ApiException{

    public InvalidDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
