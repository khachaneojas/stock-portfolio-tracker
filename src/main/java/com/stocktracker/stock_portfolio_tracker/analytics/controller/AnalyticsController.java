package com.stocktracker.stock_portfolio_tracker.analytics.controller;

import com.stocktracker.stock_portfolio_tracker.analytics.dto.AllocationResponse;
import com.stocktracker.stock_portfolio_tracker.analytics.dto.AnalyticsSummaryResponse;
import com.stocktracker.stock_portfolio_tracker.analytics.service.AnalyticsService;
import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary() {

        AnalyticsSummaryResponse response = analyticsService.getSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Analytics summary fetched successfully.", response)
        );
    }

    @GetMapping("/allocation/stocks")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getStockAllocation() {

        List<AllocationResponse> response =
                analyticsService.getStockAllocation();

        return ResponseEntity.ok(
                ApiResponse.success("Stock allocation fetched successfully.", response)
        );
    }

    @GetMapping("/allocation/sectors")
    public ResponseEntity<ApiResponse<List<AllocationResponse>>> getSectorAllocation() {

        List<AllocationResponse> response =
                analyticsService.getSectorAllocation();

        return ResponseEntity.ok(
                ApiResponse.success("Sector allocation fetched successfully.", response)
        );
    }

}
