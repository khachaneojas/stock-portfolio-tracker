package com.stocktracker.stock_portfolio_tracker.price.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StockPriceResponse(
        String symbol,
        String companyName,
        BigDecimal price,
        String currency,
        Instant recordedAt
) {
}
