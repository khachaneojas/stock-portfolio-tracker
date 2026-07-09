package com.stocktracker.stock_portfolio_tracker.portfolio.repository;

import com.stocktracker.stock_portfolio_tracker.portfolio.entity.PortfolioHolding;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, UUID> {

    List<PortfolioHolding> findByUser(User user);

    Optional<PortfolioHolding> findByIdAndUser(UUID id, User user);

    boolean existsByUserAndStock(User user, Stock stock);

}
