package com.stocktracker.stock_portfolio_tracker.auth.service;

import com.stocktracker.stock_portfolio_tracker.auth.dto.AuthenticationResponse;
import com.stocktracker.stock_portfolio_tracker.auth.dto.LoginRequest;
import com.stocktracker.stock_portfolio_tracker.auth.dto.RegisterRequest;
import com.stocktracker.stock_portfolio_tracker.common.enums.Role;
import com.stocktracker.stock_portfolio_tracker.common.enums.UserStatus;
import com.stocktracker.stock_portfolio_tracker.exception.DuplicateResourceException;
import com.stocktracker.stock_portfolio_tracker.security.JwtService;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import com.stocktracker.stock_portfolio_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        String email = request.email().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists.");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.issueToken(savedUser.getId().toString());

        return new AuthenticationResponse(
                token,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthenticationResponse login(LoginRequest request) {

        String email = request.email().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String token = jwtService.issueToken(user.getId().toString());

        return new AuthenticationResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
