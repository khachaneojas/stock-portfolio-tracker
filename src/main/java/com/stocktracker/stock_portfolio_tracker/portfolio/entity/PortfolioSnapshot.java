package com.stocktracker.stock_portfolio_tracker.portfolio.entity;

import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portfolio_snapshots")
public class PortfolioSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalInvested;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentValue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal profitLoss;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal profitLossPercentage;

    @Column(nullable = false)
    private Instant recordedAt;

}
