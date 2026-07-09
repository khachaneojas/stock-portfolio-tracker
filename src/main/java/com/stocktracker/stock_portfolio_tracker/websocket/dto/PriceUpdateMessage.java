package com.stocktracker.stock_portfolio_tracker.websocket.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdateMessage(
        String symbol,
        String companyName,
        BigDecimal price,
        String currency,
        Instant updatedAt
) {
}
