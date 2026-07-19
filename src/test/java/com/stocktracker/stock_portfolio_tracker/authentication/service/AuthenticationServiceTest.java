package com.stocktracker.stock_portfolio_tracker.authentication.service;


import com.stocktracker.stock_portfolio_tracker.auth.dto.AuthenticationResponse;
import com.stocktracker.stock_portfolio_tracker.auth.dto.LoginRequest;
import com.stocktracker.stock_portfolio_tracker.auth.dto.RegisterRequest;
import com.stocktracker.stock_portfolio_tracker.auth.service.AuthenticationService;
import com.stocktracker.stock_portfolio_tracker.exception.DuplicateResourceException;
import com.stocktracker.stock_portfolio_tracker.security.JwtService;
import com.stocktracker.stock_portfolio_tracker.common.enums.Role;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import com.stocktracker.stock_portfolio_tracker.common.enums.UserStatus;
import com.stocktracker.stock_portfolio_tracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "Mike Jones",
                "MIKE@EXAMPLE.COM",
                "Password@123"
        );

        UUID userId = UUID.randomUUID();

        when(userRepository.existsByEmail("mike@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(userId);
                    return user;
                });

        when(jwtService.issueToken(userId.toString()))
                .thenReturn("generated-jwt-token");

        AuthenticationResponse response =
                authenticationService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token())
                .isEqualTo("generated-jwt-token");
        assertThat(response.userId())
                .isEqualTo(userId);
        assertThat(response.fullName())
                .isEqualTo("Mike Jones");
        assertThat(response.email())
                .isEqualTo("mike@example.com");
        assertThat(response.role())
                .isEqualTo(Role.USER);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFullName())
                .isEqualTo("Mike Jones");
        assertThat(savedUser.getEmail())
                .isEqualTo("mike@example.com");
        assertThat(savedUser.getPassword())
                .isEqualTo("encoded-password");
        assertThat(savedUser.getRole())
                .isEqualTo(Role.USER);
        assertThat(savedUser.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        verify(userRepository)
                .existsByEmail("mike@example.com");

        verify(passwordEncoder)
                .encode("Password@123");

        verify(jwtService)
                .issueToken(userId.toString());

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingEmail() {
        RegisterRequest request = new RegisterRequest(
                "Existing User",
                "EXISTING@EXAMPLE.COM",
                "Password@123"
        );

        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                authenticationService.register(request)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists.");

        verify(userRepository)
                .existsByEmail("existing@example.com");

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                passwordEncoder,
                jwtService,
                authenticationManager
        );
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest(
                "MIKE@EXAMPLE.COM",
                "Password@123"
        );

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .fullName("Mike Jones")
                .email("mike@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("mike@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.issueToken(userId.toString()))
                .thenReturn("generated-jwt-token");

        AuthenticationResponse response =
                authenticationService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token())
                .isEqualTo("generated-jwt-token");
        assertThat(response.userId())
                .isEqualTo(userId);
        assertThat(response.fullName())
                .isEqualTo("Mike Jones");
        assertThat(response.email())
                .isEqualTo("mike@example.com");
        assertThat(response.role())
                .isEqualTo(Role.USER);

        verify(authenticationManager).authenticate(
                argThat(authentication ->
                        authentication
                                instanceof UsernamePasswordAuthenticationToken
                                && authentication.getName()
                                .equals("mike@example.com")
                                && authentication.getCredentials()
                                .equals("Password@123")
                )
        );

        verify(userRepository)
                .findByEmail("mike@example.com");

        verify(jwtService)
                .issueToken(userId.toString());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotLoadUserWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest(
                "mike@example.com",
                "wrong-password"
        );

        doThrow(
                new org.springframework.security.authentication
                        .BadCredentialsException("Invalid credentials")
        )
                .when(authenticationManager)
                .authenticate(any());

        assertThatThrownBy(() ->
                authenticationService.login(request)
        )
                .isInstanceOf(
                        org.springframework.security.authentication
                                .BadCredentialsException.class
                );

        verify(authenticationManager)
                .authenticate(any());

        verifyNoInteractions(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

}
