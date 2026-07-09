package com.stocktracker.stock_portfolio_tracker.price.controller;

import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import com.stocktracker.stock_portfolio_tracker.price.dto.StockPriceResponse;
import com.stocktracker.stock_portfolio_tracker.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/{symbol}")
    public ResponseEntity<ApiResponse<StockPriceResponse>> getLatestPrice(
            @PathVariable String symbol
    ) {
        StockPriceResponse response = priceService.getLatestPrice(symbol);

        return ResponseEntity.ok(
                ApiResponse.success("Latest stock price fetched successfully.", response)
        );
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<ApiResponse<List<StockPriceResponse>>> getPriceHistory(
            @PathVariable String symbol
    ) {
        List<StockPriceResponse> response = priceService.getPriceHistory(symbol);

        return ResponseEntity.ok(
                ApiResponse.success("Stock price history fetched successfully.", response)
        );
    }
}
