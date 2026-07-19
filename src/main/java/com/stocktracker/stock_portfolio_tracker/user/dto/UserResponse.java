package com.stocktracker.stock_portfolio_tracker.user.dto;

import com.stocktracker.stock_portfolio_tracker.common.enums.Role;
import com.stocktracker.stock_portfolio_tracker.common.enums.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        UserStatus status
) {
}
