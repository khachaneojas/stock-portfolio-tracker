package com.stocktracker.stock_portfolio_tracker.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.stocktracker.stock_portfolio_tracker.exception.UnauthorizedException;
import com.stocktracker.stock_portfolio_tracker.security.UserPrincipal;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import com.stocktracker.stock_portfolio_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUserId(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UnauthorizedException("User not found.")
                );

        return new UserPrincipal(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() ->
                        new UnauthorizedException("User not found.")
                );

        return new UserPrincipal(user);
    }

}
