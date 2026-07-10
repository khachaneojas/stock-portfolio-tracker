package com.stocktracker.stock_portfolio_tracker.alert.repository;

import com.stocktracker.stock_portfolio_tracker.alert.entity.Alert;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByUserOrderByCreatedAtDesc(User user);

    Optional<Alert> findByIdAndUser(UUID id, User user);

    List<Alert> findByStockAndActiveTrueAndTriggeredFalse(Stock stock);

    List<Alert> findByActiveTrueAndTriggeredFalse();
}
