package com.stocktracker.stock_portfolio_tracker.alert.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.AlertType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAlertRequest(

        String symbol,

        @NotNull(message = "Alert type is required")
        AlertType alertType,

        @DecimalMin(value = "0.01", message = "Target price must be greater than zero")
        BigDecimal targetPrice,

        @DecimalMin(value = "0.01", message = "Target portfolio value must be greater than zero")
        BigDecimal targetPortfolioValue
) {
}
