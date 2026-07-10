package com.stocktracker.stock_portfolio_tracker.portfolio.scheduler;

import com.stocktracker.stock_portfolio_tracker.portfolio.repository.PortfolioHoldingRepository;
import com.stocktracker.stock_portfolio_tracker.portfolio.service.PortfolioService;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PortfolioSnapshotScheduler {

    private final PortfolioHoldingRepository holdingRepository;
    private final PortfolioService portfolioService;

    @Scheduled(fixedRate = 60000)
    public void createPortfolioSnapshots() {

        Set<User> users = new HashSet<>();

        holdingRepository.findAll()
                .forEach(holding -> users.add(holding.getUser()));

        users.forEach(portfolioService::createSnapshotForUser);
    }
}
