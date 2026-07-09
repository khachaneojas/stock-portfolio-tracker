package com.stocktracker.stock_portfolio_tracker.stock.entity;

import com.stocktracker.stock_portfolio_tracker.common.entity.BaseEntity;

import com.stocktracker.stock_portfolio_tracker.common.enums.StockExchange;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "stocks",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_symbol", columnNames = "symbol")
        }
)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockExchange exchange;

    @Column(nullable = false, length = 80)
    private String sector;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private boolean active;
}
