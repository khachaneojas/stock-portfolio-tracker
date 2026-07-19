package com.stocktracker.stock_portfolio_tracker.user.repository;

import com.stocktracker.stock_portfolio_tracker.common.enums.Role;
import com.stocktracker.stock_portfolio_tracker.common.enums.UserStatus;
import com.stocktracker.stock_portfolio_tracker.config.AbstractIntegrationTest;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = User.builder()
                .fullName("Mike Jones")
                .email("mike@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.saveAndFlush(user);

        Optional<User> result =
                userRepository.findByEmail("mike@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getFullName())
                .isEqualTo("Mike Jones");
        assertThat(result.get().getEmail())
                .isEqualTo("mike@example.com");
        assertThat(result.get().getRole())
                .isEqualTo(Role.USER);
        assertThat(result.get().getStatus())
                .isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> result =
                userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.saveAndFlush(user);

        boolean exists =
                userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists =
                userRepository.existsByEmail("unknown@example.com");

        assertThat(exists).isFalse();
    }

}
