package com.stocktracker.stock_portfolio_tracker.portfolio.service;

import com.stocktracker.stock_portfolio_tracker.portfolio.dto.*;
import com.stocktracker.stock_portfolio_tracker.portfolio.entity.PortfolioSnapshot;
import com.stocktracker.stock_portfolio_tracker.portfolio.repository.PortfolioSnapshotRepository;
import com.stocktracker.stock_portfolio_tracker.price.service.PriceService;
import com.stocktracker.stock_portfolio_tracker.security.CurrentUserProvider;
import com.stocktracker.stock_portfolio_tracker.exception.DuplicateResourceException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.portfolio.entity.PortfolioHolding;
import com.stocktracker.stock_portfolio_tracker.portfolio.repository.PortfolioHoldingRepository;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.stock.repository.StockRepository;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioHoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PriceService priceService;
    private final PortfolioSnapshotRepository snapshotRepository;

    public HoldingResponse addHolding(AddHoldingRequest request) {

        User currentUser = currentUserProvider.getCurrentUser();

        String symbol = request.symbol().toUpperCase();

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with symbol: " + symbol)
                );

        if (!stock.isActive()) {
            throw new ResourceNotFoundException("Stock is not active: " + symbol);
        }

        if (holdingRepository.existsByUserAndStock(currentUser, stock)) {
            throw new DuplicateResourceException(
                    "Holding already exists for stock: " + symbol
            );
        }

        PortfolioHolding holding = PortfolioHolding.builder()
                .user(currentUser)
                .stock(stock)
                .quantity(request.quantity())
                .averageBuyPrice(request.averageBuyPrice())
                .build();

        PortfolioHolding savedHolding = holdingRepository.save(holding);

        return mapToResponse(savedHolding);
    }

    public List<HoldingResponse> getMyHoldings() {

        User currentUser = currentUserProvider.getCurrentUser();

        return holdingRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public HoldingResponse getHoldingById(UUID holdingId) {

        User currentUser = currentUserProvider.getCurrentUser();

        PortfolioHolding holding = holdingRepository.findByIdAndUser(holdingId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Holding not found with id: " + holdingId)
                );

        return mapToResponse(holding);
    }

    public HoldingResponse updateHolding(
            UUID holdingId,
            UpdateHoldingRequest request
    ) {

        User currentUser = currentUserProvider.getCurrentUser();

        PortfolioHolding holding = holdingRepository.findByIdAndUser(holdingId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Holding not found with id: " + holdingId)
                );

        holding.setQuantity(request.quantity());
        holding.setAverageBuyPrice(request.averageBuyPrice());

        PortfolioHolding updatedHolding = holdingRepository.save(holding);

        return mapToResponse(updatedHolding);
    }

    public void deleteHolding(UUID holdingId) {

        User currentUser = currentUserProvider.getCurrentUser();

        PortfolioHolding holding = holdingRepository.findByIdAndUser(holdingId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Holding not found with id: " + holdingId)
                );

        holdingRepository.delete(holding);
    }

    private HoldingResponse mapToResponse(PortfolioHolding holding) {

        Stock stock = holding.getStock();

        BigDecimal investedAmount =
                holding.getQuantity().multiply(holding.getAverageBuyPrice());

        return new HoldingResponse(
                holding.getId(),
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getExchange().name(),
                stock.getSector(),
                stock.getCurrency(),
                holding.getQuantity(),
                holding.getAverageBuyPrice(),
                investedAmount
        );
    }

    public PortfolioValuationResponse getMyPortfolioValuation() {

        User currentUser = currentUserProvider.getCurrentUser();
        return getPortfolioValuationForUser(currentUser);

    }

    private HoldingValuationResponse mapToValuationResponse(PortfolioHolding holding) {

        Stock stock = holding.getStock();

        BigDecimal currentPrice = priceService.getLatestPriceValue(stock.getSymbol());

        BigDecimal investedAmount = holding.getQuantity()
                .multiply(holding.getAverageBuyPrice());

        BigDecimal currentValue = holding.getQuantity()
                .multiply(currentPrice);

        BigDecimal profitLoss = currentValue.subtract(investedAmount);

        BigDecimal profitLossPercentage = BigDecimal.ZERO;

        if (investedAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercentage = profitLoss
                    .divide(investedAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new HoldingValuationResponse(
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getSector(),
                stock.getCurrency(),
                holding.getQuantity(),
                holding.getAverageBuyPrice(),
                currentPrice,
                investedAmount,
                currentValue,
                profitLoss,
                profitLossPercentage
        );
    }

    public PortfolioValuationResponse getPortfolioValuationForUser(User user) {

        List<PortfolioHolding> holdings = holdingRepository.findByUser(user);

        List<HoldingValuationResponse> holdingValuations = holdings.stream()
                .map(this::mapToValuationResponse)
                .toList();

        BigDecimal totalInvested = holdingValuations.stream()
                .map(HoldingValuationResponse::investedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentValue = holdingValuations.stream()
                .map(HoldingValuationResponse::currentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss = currentValue.subtract(totalInvested);

        BigDecimal totalProfitLossPercentage = BigDecimal.ZERO;

        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            totalProfitLossPercentage = totalProfitLoss
                    .divide(totalInvested, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new PortfolioValuationResponse(
                totalInvested,
                currentValue,
                totalProfitLoss,
                totalProfitLossPercentage,
                holdingValuations
        );
    }

    public void createSnapshotForUser(User user) {

        PortfolioValuationResponse valuation = getPortfolioValuationForUser(user);

        PortfolioSnapshot snapshot = PortfolioSnapshot.builder()
                .user(user)
                .totalInvested(valuation.totalInvested())
                .currentValue(valuation.currentValue())
                .profitLoss(valuation.totalProfitLoss())
                .profitLossPercentage(valuation.totalProfitLossPercentage())
                .recordedAt(Instant.now())
                .build();

        snapshotRepository.save(snapshot);
    }

    public List<PortfolioSnapshotResponse> getMyPortfolioSnapshots() {

        User currentUser = currentUserProvider.getCurrentUser();

        return snapshotRepository.findTop30ByUserOrderByRecordedAtDesc(currentUser)
                .stream()
                .map(snapshot -> new PortfolioSnapshotResponse(
                        snapshot.getTotalInvested(),
                        snapshot.getCurrentValue(),
                        snapshot.getProfitLoss(),
                        snapshot.getProfitLossPercentage(),
                        snapshot.getRecordedAt()
                ))
                .toList();
    }

}
