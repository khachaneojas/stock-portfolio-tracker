package com.stocktracker.stock_portfolio_tracker.auth.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.Role;

import java.util.UUID;

public record AuthenticationResponse(
        String token,

        UUID userId,

        String fullName,

        String email,

        Role role
) {
}
