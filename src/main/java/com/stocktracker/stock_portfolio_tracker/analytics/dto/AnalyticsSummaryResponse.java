package com.stocktracker.stock_portfolio_tracker.analytics.dto;

import java.math.BigDecimal;

public record AnalyticsSummaryResponse(
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal totalProfitLoss,
        BigDecimal totalProfitLossPercentage,
        String bestPerformingStock,
        String worstPerformingStock
) {
}
