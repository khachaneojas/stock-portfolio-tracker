package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException{


    public ResourceNotFoundException() {
        super("Requested resource not found.", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message,  HttpStatus.NOT_FOUND);
    }
}
