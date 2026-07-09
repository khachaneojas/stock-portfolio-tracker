package com.stocktracker.stock_portfolio_tracker.portfolio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HoldingResponse(
        UUID id,
        String symbol,
        String companyName,
        String exchange,
        String sector,
        String currency,
        BigDecimal quantity,
        BigDecimal averageBuyPrice,
        BigDecimal investedAmount
) {
}
