package com.stocktracker.stock_portfolio_tracker.analytics.service;

import com.stocktracker.stock_portfolio_tracker.analytics.dto.AllocationResponse;
import com.stocktracker.stock_portfolio_tracker.analytics.dto.AnalyticsSummaryResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.HoldingValuationResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.PortfolioValuationResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PortfolioService portfolioService;

    public AnalyticsSummaryResponse getSummary() {

        PortfolioValuationResponse valuation =
                portfolioService.getMyPortfolioValuation();

        List<HoldingValuationResponse> holdings = valuation.holdings();

        String bestPerformingStock = holdings.stream()
                .max(Comparator.comparing(HoldingValuationResponse::profitLossPercentage))
                .map(HoldingValuationResponse::symbol)
                .orElse(null);

        String worstPerformingStock = holdings.stream()
                .min(Comparator.comparing(HoldingValuationResponse::profitLossPercentage))
                .map(HoldingValuationResponse::symbol)
                .orElse(null);

        return new AnalyticsSummaryResponse(
                valuation.totalInvested(),
                valuation.currentValue(),
                valuation.totalProfitLoss(),
                valuation.totalProfitLossPercentage(),
                bestPerformingStock,
                worstPerformingStock
        );
    }

    public List<AllocationResponse> getStockAllocation() {

        PortfolioValuationResponse valuation =
                portfolioService.getMyPortfolioValuation();

        BigDecimal totalValue = valuation.currentValue();

        return valuation.holdings()
                .stream()
                .map(holding -> new AllocationResponse(
                        holding.symbol(),
                        holding.currentValue(),
                        calculatePercentage(holding.currentValue(), totalValue)
                ))
                .toList();
    }

    public List<AllocationResponse> getSectorAllocation() {

        PortfolioValuationResponse valuation =
                portfolioService.getMyPortfolioValuation();

        BigDecimal totalValue = valuation.currentValue();

        Map<String, BigDecimal> sectorTotals = valuation.holdings()
                .stream()
                .collect(Collectors.groupingBy(
                        HoldingValuationResponse::sector,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                HoldingValuationResponse::currentValue,
                                BigDecimal::add
                        )
                ));

        return sectorTotals.entrySet()
                .stream()
                .map(entry -> new AllocationResponse(
                        entry.getKey(),
                        entry.getValue(),
                        calculatePercentage(entry.getValue(), totalValue)
                ))
                .toList();
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {

        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

}
