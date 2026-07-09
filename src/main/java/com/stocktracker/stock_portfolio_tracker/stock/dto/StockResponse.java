package com.stocktracker.stock_portfolio_tracker.stock.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.StockExchange;
import java.util.UUID;

public record StockResponse(
        UUID id,
        String symbol,
        String companyName,
        StockExchange exchange,
        String sector,
        String currency,
        boolean active
) {
}
