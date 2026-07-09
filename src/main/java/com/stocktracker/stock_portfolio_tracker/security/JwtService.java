package com.stocktracker.stock_portfolio_tracker.security;

import com.stocktracker.stock_portfolio_tracker.exception.JwtException;
import com.stocktracker.stock_portfolio_tracker.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public static final long DEFAULT_EXPIRY_DURATION =
            Duration.of(24, ChronoUnit.HOURS).toMillis();

    private SecretKey getSignedSecretKey() {
        Objects.requireNonNull(jwtSecret);

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaims(String jwtString) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignedSecretKey())
                    .build()
                    .parseSignedClaims(jwtString)
                    .getPayload();
        } catch (Exception ex) {
            throw new JwtException(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private String getJwtToken(String authorizationHeader) {
        if (StringUtils.isNotBlank(authorizationHeader)
                && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        throw new UnauthorizedException("Missing or invalid Authorization header.");
    }

    public String issueToken(String subject) {
        Objects.requireNonNull(subject);
        return issueToken(subject, DEFAULT_EXPIRY_DURATION);
    }

    public String issueToken(String subject, long expiration) {
        Objects.requireNonNull(subject);

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .signWith(getSignedSecretKey(), Jwts.SIG.HS512);

        if (expiration > 1) {
            jwtBuilder.expiration(
                    new Date(System.currentTimeMillis() + expiration)
            );
        }

        return jwtBuilder.compact();
    }

    public String getSubject(String authorizationHeader) {
        String token = getJwtToken(authorizationHeader);

        Claims claims = getClaims(token);
        String subject = claims.getSubject();

        if (StringUtils.isBlank(subject)) {
            throw new UnauthorizedException("Invalid token subject.");
        }

        return subject;
    }

    public UUID getUserId(String authorizationHeader) {
        String subject = getSubject(authorizationHeader);

        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid token subject.");
        }
    }

}
