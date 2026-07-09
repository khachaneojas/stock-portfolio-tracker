package com.stocktracker.stock_portfolio_tracker.stock.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.StockExchange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockRequest (
        @NotBlank(message = "Stock symbol is required")
        @Size(max = 20, message = "Stock symbol must not exceed 20 characters")
        String symbol,

        @NotBlank(message = "Company name is required")
        @Size(max = 150, message = "Company name must not exceed 150 characters")
        String companyName,

        @NotNull(message = "Stock exchange is required")
        StockExchange exchange,

        @NotBlank(message = "Sector is required")
        @Size(max = 80, message = "Sector must not exceed 80 characters")
        String sector,

        @NotBlank(message = "Currency is required")
        @Size(max = 10, message = "Currency must not exceed 10 characters")
        String currency
){
}
