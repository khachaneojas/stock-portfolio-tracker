package com.stocktracker.stock_portfolio_tracker.alert.service;

import com.stocktracker.stock_portfolio_tracker.alert.dto.AlertResponse;
import com.stocktracker.stock_portfolio_tracker.alert.dto.CreateAlertRequest;
import com.stocktracker.stock_portfolio_tracker.alert.entity.Alert;
import com.stocktracker.stock_portfolio_tracker.alert.repository.AlertRepository;
import com.stocktracker.stock_portfolio_tracker.common.enums.AlertType;
import com.stocktracker.stock_portfolio_tracker.exception.InvalidDataException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.portfolio.dto.PortfolioValuationResponse;
import com.stocktracker.stock_portfolio_tracker.portfolio.service.PortfolioService;
import com.stocktracker.stock_portfolio_tracker.security.CurrentUserProvider;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.stock.repository.StockRepository;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import com.stocktracker.stock_portfolio_tracker.websocket.AlertWebSocketPublisher;
import com.stocktracker.stock_portfolio_tracker.websocket.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final StockRepository stockRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PortfolioService portfolioService;
    private final AlertWebSocketPublisher alertWebSocketPublisher;

    public AlertResponse createAlert(CreateAlertRequest request) {

        User currentUser = currentUserProvider.getCurrentUser();

        validateAlertRequest(request);

        Stock stock = null;

        if (request.alertType() == AlertType.PRICE_ABOVE
                || request.alertType() == AlertType.PRICE_BELOW) {

            String symbol = request.symbol().toUpperCase();

            stock = stockRepository.findBySymbol(symbol)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Stock not found with symbol: " + symbol)
                    );
        }

        Alert alert = Alert.builder()
                .user(currentUser)
                .stock(stock)
                .alertType(request.alertType())
                .targetPrice(request.targetPrice())
                .targetPortfolioValue(request.targetPortfolioValue())
                .active(true)
                .triggered(false)
                .build();

        return mapToResponse(alertRepository.save(alert));
    }

    public List<AlertResponse> getMyAlerts() {
        User currentUser = currentUserProvider.getCurrentUser();

        return alertRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AlertResponse deactivateAlert(UUID alertId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Alert alert = alertRepository.findByIdAndUser(alertId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Alert not found with id: " + alertId)
                );

        alert.setActive(false);

        return mapToResponse(alertRepository.save(alert));
    }

    public void deleteAlert(UUID alertId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Alert alert = alertRepository.findByIdAndUser(alertId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Alert not found with id: " + alertId)
                );

        alertRepository.delete(alert);
    }

    public void checkStockPriceAlerts(Stock stock, BigDecimal currentPrice) {

        List<Alert> alerts =
                alertRepository.findByStockAndActiveTrueAndTriggeredFalse(stock);

        for (Alert alert : alerts) {

            boolean shouldTrigger = switch (alert.getAlertType()) {
                case PRICE_ABOVE ->
                        currentPrice.compareTo(alert.getTargetPrice()) >= 0;

                case PRICE_BELOW ->
                        currentPrice.compareTo(alert.getTargetPrice()) <= 0;

                default -> false;
            };

            if (shouldTrigger) {
                triggerAlert(
                        alert,
                        currentPrice,
                        "Price alert triggered for " + stock.getSymbol()
                );
            }
        }
    }

    public void checkPortfolioValueAlerts() {

        List<Alert> alerts = alertRepository.findByActiveTrueAndTriggeredFalse()
                .stream()
                .filter(alert ->
                        alert.getAlertType() == AlertType.PORTFOLIO_VALUE_ABOVE
                                || alert.getAlertType() == AlertType.PORTFOLIO_VALUE_BELOW
                )
                .toList();

        for (Alert alert : alerts) {

            PortfolioValuationResponse valuation =
                    portfolioService.getPortfolioValuationForUser(alert.getUser());

            BigDecimal currentValue = valuation.currentValue();

            boolean shouldTrigger = switch (alert.getAlertType()) {
                case PORTFOLIO_VALUE_ABOVE ->
                        currentValue.compareTo(alert.getTargetPortfolioValue()) >= 0;

                case PORTFOLIO_VALUE_BELOW ->
                        currentValue.compareTo(alert.getTargetPortfolioValue()) <= 0;

                default -> false;
            };

            if (shouldTrigger) {
                triggerAlert(
                        alert,
                        currentValue,
                        "Portfolio value alert triggered"
                );
            }
        }
    }

    private void triggerAlert(
            Alert alert,
            BigDecimal currentValue,
            String message
    ) {
        alert.setTriggered(true);
        alert.setActive(false);
        alert.setTriggeredAt(Instant.now());

        Alert savedAlert = alertRepository.save(alert);

        alertWebSocketPublisher.publishAlert(
                new AlertMessage(
                        savedAlert.getId(),
                        savedAlert.getUser().getId(),
                        savedAlert.getStock() != null
                                ? savedAlert.getStock().getSymbol()
                                : null,
                        savedAlert.getAlertType(),
                        currentValue,
                        message,
                        savedAlert.getTriggeredAt()
                )
        );
    }

    private void validateAlertRequest(CreateAlertRequest request) {

        if (request.alertType() == AlertType.PRICE_ABOVE
                || request.alertType() == AlertType.PRICE_BELOW) {

            if (request.symbol() == null || request.symbol().isBlank()) {
                throw new InvalidDataException("Stock symbol is required for price alerts.");
            }

            if (request.targetPrice() == null) {
                throw new InvalidDataException("Target price is required for price alerts.");
            }
        }

        if (request.alertType() == AlertType.PORTFOLIO_VALUE_ABOVE
                || request.alertType() == AlertType.PORTFOLIO_VALUE_BELOW) {

            if (request.targetPortfolioValue() == null) {
                throw new InvalidDataException(
                        "Target portfolio value is required for portfolio value alerts."
                );
            }
        }
    }

    private AlertResponse mapToResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getStock() != null ? alert.getStock().getSymbol() : null,
                alert.getAlertType(),
                alert.getTargetPrice(),
                alert.getTargetPortfolioValue(),
                alert.isActive(),
                alert.isTriggered(),
                alert.getTriggeredAt()
        );
    }

}
