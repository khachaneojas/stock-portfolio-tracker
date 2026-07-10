package com.stocktracker.stock_portfolio_tracker.price.dto;

import java.math.BigDecimal;

public record FinnhubQuoteResponse(
        BigDecimal c,
        BigDecimal d,
        BigDecimal dp,
        BigDecimal h,
        BigDecimal l,
        BigDecimal o,
        BigDecimal pc,
        Long t
)  {
}
