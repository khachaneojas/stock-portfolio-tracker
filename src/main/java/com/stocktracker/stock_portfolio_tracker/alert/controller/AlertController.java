package com.stocktracker.stock_portfolio_tracker.alert.controller;

import com.stocktracker.stock_portfolio_tracker.alert.dto.AlertResponse;
import com.stocktracker.stock_portfolio_tracker.alert.dto.CreateAlertRequest;
import com.stocktracker.stock_portfolio_tracker.alert.service.AlertService;
import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @Valid @RequestBody CreateAlertRequest request
    ) {
        AlertResponse response = alertService.createAlert(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Alert created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getMyAlerts() {
        List<AlertResponse> response = alertService.getMyAlerts();

        return ResponseEntity.ok(
                ApiResponse.success("Alerts fetched successfully.", response)
        );
    }

    @PatchMapping("/{alertId}/deactivate")
    public ResponseEntity<ApiResponse<AlertResponse>> deactivateAlert(
            @PathVariable UUID alertId
    ) {
        AlertResponse response = alertService.deactivateAlert(alertId);

        return ResponseEntity.ok(
                ApiResponse.success("Alert deactivated successfully.", response)
        );
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(
            @PathVariable UUID alertId
    ) {
        alertService.deleteAlert(alertId);

        return ResponseEntity.ok(
                ApiResponse.success("Alert deleted successfully.")
        );
    }

}
