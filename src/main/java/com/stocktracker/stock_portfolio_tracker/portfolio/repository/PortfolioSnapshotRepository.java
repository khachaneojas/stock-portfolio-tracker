package com.stocktracker.stock_portfolio_tracker.portfolio.repository;

import com.stocktracker.stock_portfolio_tracker.portfolio.entity.PortfolioSnapshot;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, UUID> {

    List<PortfolioSnapshot> findTop30ByUserOrderByRecordedAtDesc(User user);

}
