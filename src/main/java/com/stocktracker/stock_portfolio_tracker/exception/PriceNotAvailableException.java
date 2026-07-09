package com.stocktracker.stock_portfolio_tracker.exception;

import org.springframework.http.HttpStatus;

public class PriceNotAvailableException extends ApiException {
    public PriceNotAvailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
