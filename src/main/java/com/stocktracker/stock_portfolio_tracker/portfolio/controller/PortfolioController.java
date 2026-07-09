package com.stocktracker.stock_portfolio_tracker.portfolio.controller;

import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.AddHoldingRequest;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.HoldingResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.PortfolioValuationResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.UpdateHoldingRequest;
import com.stocktracker.stock_portfolio_tracker.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @PostMapping("/holdings")
    public ResponseEntity<ApiResponse<HoldingResponse>> addHolding(
            @Valid @RequestBody AddHoldingRequest request
    ) {
        HoldingResponse response = portfolioService.addHolding(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Holding added successfully.", response));
    }

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getMyHoldings() {

        List<HoldingResponse> response = portfolioService.getMyHoldings();

        return ResponseEntity.ok(
                ApiResponse.success("Portfolio holdings fetched successfully.", response)
        );
    }

    @GetMapping("/holdings/{holdingId}")
    public ResponseEntity<ApiResponse<HoldingResponse>> getHoldingById(
            @PathVariable UUID holdingId
    ) {
        HoldingResponse response = portfolioService.getHoldingById(holdingId);

        return ResponseEntity.ok(
                ApiResponse.success("Holding fetched successfully.", response)
        );
    }

    @PutMapping("/holdings/{holdingId}")
    public ResponseEntity<ApiResponse<HoldingResponse>> updateHolding(
            @PathVariable UUID holdingId,
            @Valid @RequestBody UpdateHoldingRequest request
    ) {
        HoldingResponse response =
                portfolioService.updateHolding(holdingId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Holding updated successfully.", response)
        );
    }

    @DeleteMapping("/holdings/{holdingId}")
    public ResponseEntity<ApiResponse<Void>> deleteHolding(
            @PathVariable UUID holdingId
    ) {
        portfolioService.deleteHolding(holdingId);

        return ResponseEntity.ok(
                ApiResponse.success("Holding deleted successfully.")
        );
    }

    @GetMapping("/valuation")
    public ResponseEntity<ApiResponse<PortfolioValuationResponse>> getMyPortfolioValuation() {

        PortfolioValuationResponse response =
                portfolioService.getMyPortfolioValuation();

        return ResponseEntity.ok(
                ApiResponse.success("Portfolio valuation fetched successfully.", response)
        );
    }

}
