package com.stocktracker.stock_portfolio_tracker.price.repository;

import com.stocktracker.stock_portfolio_tracker.price.entity.StockPrice;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockPriceRepository extends JpaRepository<StockPrice, UUID> {

    Optional<StockPrice> findTopByStockOrderByRecordedAtDesc(Stock stock);

    List<StockPrice> findTop20ByStockOrderByRecordedAtDesc(Stock stock);
}
