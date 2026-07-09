package com.stocktracker.stock_portfolio_tracker.portfolio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddHoldingRequest(
        @NotBlank(message = "Stock symbol is required")
        String symbol,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
        BigDecimal quantity,

        @NotNull(message = "Average buy price is required")
        @DecimalMin(value = "0.01", message = "Average buy price must be greater than zero")
        BigDecimal averageBuyPrice
) {
}
