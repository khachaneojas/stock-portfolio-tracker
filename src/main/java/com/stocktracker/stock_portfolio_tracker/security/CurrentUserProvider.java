package com.stocktracker.stock_portfolio_tracker.security;

import com.stocktracker.stock_portfolio_tracker.security.UserPrincipal;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return principal.getUser();
    }

}
