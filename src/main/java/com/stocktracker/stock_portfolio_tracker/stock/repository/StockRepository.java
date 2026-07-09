package com.stocktracker.stock_portfolio_tracker.stock.repository;

import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
