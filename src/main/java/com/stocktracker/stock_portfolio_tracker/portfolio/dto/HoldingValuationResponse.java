package com.stocktracker.stock_portfolio_tracker.portfolio.dto;

import java.math.BigDecimal;

public record HoldingValuationResponse(
        String symbol,
        String companyName,
        BigDecimal quantity,
        BigDecimal averageBuyPrice,
        BigDecimal currentPrice,
        BigDecimal investedAmount,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal profitLossPercentage
) {
}

