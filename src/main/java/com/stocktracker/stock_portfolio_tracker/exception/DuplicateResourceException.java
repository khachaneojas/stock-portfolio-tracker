package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException
{
    public DuplicateResourceException(String message){
        super(message, HttpStatus.CONFLICT);
    }
}
