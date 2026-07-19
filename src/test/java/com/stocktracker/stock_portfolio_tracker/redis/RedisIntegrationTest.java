package com.stocktracker.stock_portfolio_tracker.redis;

import com.stocktracker.stock_portfolio_tracker.config.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RedisIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_KEY =
            "test:stock:AAPL";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void cleanUp() {
        redisTemplate.delete(TEST_KEY);
    }

    @Test
    void shouldSaveAndRetrieveValueFromRedis() {
        redisTemplate.opsForValue()
                .set(TEST_KEY, "215.50");

        String storedValue =
                redisTemplate.opsForValue().get(TEST_KEY);

        assertThat(storedValue)
                .isEqualTo("215.50");
    }

    @Test
    void shouldDeleteValueFromRedis() {
        redisTemplate.opsForValue()
                .set(TEST_KEY, "215.50");

        Boolean deleted =
                redisTemplate.delete(TEST_KEY);

        String storedValue =
                redisTemplate.opsForValue().get(TEST_KEY);

        assertThat(deleted).isTrue();
        assertThat(storedValue).isNull();
    }

    @Test
    void shouldStoreValueWithExpiration() {
        redisTemplate.opsForValue().set(
                TEST_KEY,
                "215.50",
                Duration.ofMinutes(5)
        );

        String storedValue =
                redisTemplate.opsForValue().get(TEST_KEY);

        Long expiration =
                redisTemplate.getExpire(TEST_KEY);

        assertThat(storedValue)
                .isEqualTo("215.50");

        assertThat(expiration)
                .isPositive();
    }

}
