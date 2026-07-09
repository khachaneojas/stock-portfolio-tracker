package com.stocktracker.stock_portfolio_tracker.stock.controller;

import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockRequest;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockResponse;
import com.stocktracker.stock_portfolio_tracker.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createStock(
            @Valid @RequestBody StockRequest request
    ) {
        StockResponse response = stockService.createStock(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStocks() {
        List<StockResponse> response = stockService.getAllStocks();

        return ResponseEntity.ok(
                ApiResponse.success("Stocks fetched successfully.", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> getStockById(
            @PathVariable UUID id
    ) {
        StockResponse response = stockService.getStockById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully.", response)
        );
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<ApiResponse<StockResponse>> getStockBySymbol(
            @PathVariable String symbol
    ) {
        StockResponse response = stockService.getStockBySymbol(symbol);

        return ResponseEntity.ok(
                ApiResponse.success("Stock fetched successfully.", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockRequest request
    ) {
        StockResponse response = stockService.updateStock(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Stock updated successfully.", response)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<StockResponse>> activateStock(
            @PathVariable UUID id
    ) {
        StockResponse response = stockService.activateStock(id);

        return ResponseEntity.ok(
                ApiResponse.success("Stock activated successfully.", response)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<StockResponse>> deactivateStock(
            @PathVariable UUID id
    ) {
        StockResponse response = stockService.deactivateStock(id);

        return ResponseEntity.ok(
                ApiResponse.success("Stock deactivated successfully.", response)
        );
    }

}
