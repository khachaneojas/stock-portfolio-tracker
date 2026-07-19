package com.stocktracker.stock_portfolio_tracker.security;

import com.stocktracker.stock_portfolio_tracker.exception.UnauthorizedException;
import com.stocktracker.stock_portfolio_tracker.security.UserPrincipal;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return principal.getUser();
    }

    public UUID getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null) {
            throw new UnauthorizedException(
                    "User is not authenticated."
            );
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new UnauthorizedException(
                "Authenticated user details are unavailable."
        );
    }

}
