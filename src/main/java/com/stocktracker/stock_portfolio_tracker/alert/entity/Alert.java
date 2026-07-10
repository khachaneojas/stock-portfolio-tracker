package com.stocktracker.stock_portfolio_tracker.alert.entity;

import com.stocktracker.stock_portfolio_tracker.common.entity.BaseEntity;

import com.stocktracker.stock_portfolio_tracker.common.enums.AlertType;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
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
@Table(name = "alerts")
public class Alert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AlertType alertType;

    @Column(precision = 19, scale = 4)
    private BigDecimal targetPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal targetPortfolioValue;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean triggered;

    private Instant triggeredAt;

}
