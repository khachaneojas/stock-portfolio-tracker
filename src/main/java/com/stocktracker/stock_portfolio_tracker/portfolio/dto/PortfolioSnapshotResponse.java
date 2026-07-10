package com.stocktracker.stock_portfolio_tracker.portfolio.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PortfolioSnapshotResponse(
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal profitLossPercentage,
        Instant recordedAt
) {
}
