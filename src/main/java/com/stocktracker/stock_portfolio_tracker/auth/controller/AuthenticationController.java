package com.stocktracker.stock_portfolio_tracker.auth.controller;

import com.stocktracker.stock_portfolio_tracker.auth.dto.AuthenticationResponse;
import com.stocktracker.stock_portfolio_tracker.auth.dto.LoginRequest;
import com.stocktracker.stock_portfolio_tracker.auth.dto.RegisterRequest;
import com.stocktracker.stock_portfolio_tracker.auth.service.AuthenticationService;
import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response = authenticationService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful.", response)
        );
    }

}
