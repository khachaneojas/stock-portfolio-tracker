package com.stocktracker.stock_portfolio_tracker.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioValuationResponse(
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal totalProfitLoss,
        BigDecimal totalProfitLossPercentage,
        List<HoldingValuationResponse> holdings
) {
}
