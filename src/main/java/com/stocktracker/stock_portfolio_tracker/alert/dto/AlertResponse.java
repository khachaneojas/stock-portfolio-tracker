package com.stocktracker.stock_portfolio_tracker.alert.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.AlertType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        String symbol,
        AlertType alertType,
        BigDecimal targetPrice,
        BigDecimal targetPortfolioValue,
        boolean active,
        boolean triggered,
        Instant triggeredAt
) {
}
