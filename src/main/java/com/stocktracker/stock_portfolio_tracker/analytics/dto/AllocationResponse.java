package com.stocktracker.stock_portfolio_tracker.analytics.dto;

import java.math.BigDecimal;

public record AllocationResponse(
        String name,
        BigDecimal value,
        BigDecimal percentage
) {
}
