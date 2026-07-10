package com.stocktracker.stock_portfolio_tracker.websocket.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.AlertType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AlertMessage(
        UUID alertId,
        UUID userId,
        String symbol,
        AlertType alertType,
        BigDecimal currentValue,
        String message,
        Instant triggeredAt
) {
}
