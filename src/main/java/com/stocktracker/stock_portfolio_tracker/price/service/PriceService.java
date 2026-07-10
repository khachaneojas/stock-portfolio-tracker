package com.stocktracker.stock_portfolio_tracker.price.service;

import com.stocktracker.stock_portfolio_tracker.alert.service.AlertService;
import com.stocktracker.stock_portfolio_tracker.exception.PriceNotAvailableException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.price.dto.StockPriceResponse;
import com.stocktracker.stock_portfolio_tracker.price.entity.StockPrice;
import com.stocktracker.stock_portfolio_tracker.price.repository.StockPriceRepository;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.stock.repository.StockRepository;
import com.stocktracker.stock_portfolio_tracker.websocket.PriceWebSocketPublisher;
import com.stocktracker.stock_portfolio_tracker.websocket.dto.PriceUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PriceService {
    private static final String STOCK_PRICE_KEY_PREFIX = "stock:price:";
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PriceWebSocketPublisher priceWebSocketPublisher;
    private final AlertService alertService;

    private final Random random = new Random();

    public StockPriceResponse getLatestPrice(String symbol) {

        String normalizedSymbol = symbol.toUpperCase();

        Stock stock = stockRepository.findBySymbol(normalizedSymbol)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with symbol: " + normalizedSymbol)
                );

        String redisKey = STOCK_PRICE_KEY_PREFIX + normalizedSymbol;
        String cachedPrice = redisTemplate.opsForValue().get(redisKey);

        if (cachedPrice != null) {
            return new StockPriceResponse(
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    new BigDecimal(cachedPrice),
                    stock.getCurrency(),
                    Instant.now()
            );
        }

        StockPrice latestPrice = stockPriceRepository
                .findTopByStockOrderByRecordedAtDesc(stock)
                .orElseThrow(() ->
                        new PriceNotAvailableException("Price not available for stock: " + normalizedSymbol)
                );

        return mapToResponse(latestPrice);
    }

    public List<StockPriceResponse> getPriceHistory(String symbol) {

        String normalizedSymbol = symbol.toUpperCase();

        Stock stock = stockRepository.findBySymbol(normalizedSymbol)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with symbol: " + normalizedSymbol)
                );

        return stockPriceRepository.findTop20ByStockOrderByRecordedAtDesc(stock)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void updatePricesForAllActiveStocks() {

        List<Stock> activeStocks = stockRepository.findAll()
                .stream()
                .filter(Stock::isActive)
                .toList();

        for (Stock stock : activeStocks) {
            BigDecimal newPrice = generateSimulatedPrice(stock);
            Instant recordedAt = Instant.now();

            StockPrice stockPrice = StockPrice.builder()
                    .stock(stock)
                    .price(newPrice)
                    .recordedAt(recordedAt)
                    .build();

            stockPriceRepository.save(stockPrice);

            redisTemplate.opsForValue().set(
                    STOCK_PRICE_KEY_PREFIX + stock.getSymbol(),
                    newPrice.toPlainString()
            );

            priceWebSocketPublisher.publishPriceUpdate(
                    new PriceUpdateMessage(
                            stock.getSymbol(),
                            stock.getCompanyName(),
                            newPrice,
                            stock.getCurrency(),
                            recordedAt
                    )
            );

            alertService.checkStockPriceAlerts(stock, newPrice);
        }

        alertService.checkPortfolioValueAlerts();
    }

    private BigDecimal generateSimulatedPrice(Stock stock) {

        BigDecimal existingPrice = stockPriceRepository
                .findTopByStockOrderByRecordedAtDesc(stock)
                .map(StockPrice::getPrice)
                .orElse(BigDecimal.valueOf(100));

        double changePercent = -2 + (4 * random.nextDouble());

        BigDecimal multiplier = BigDecimal.valueOf(1 + changePercent / 100);

        return existingPrice
                .multiply(multiplier)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private StockPriceResponse mapToResponse(StockPrice stockPrice) {

        Stock stock = stockPrice.getStock();

        return new StockPriceResponse(
                stock.getSymbol(),
                stock.getCompanyName(),
                stockPrice.getPrice(),
                stock.getCurrency(),
                stockPrice.getRecordedAt()
        );
    }

    public BigDecimal getLatestPriceValue(String symbol) {

        String normalizedSymbol = symbol.toUpperCase();

        Stock stock = stockRepository.findBySymbol(normalizedSymbol)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with symbol: " + normalizedSymbol)
                );

        String redisKey = STOCK_PRICE_KEY_PREFIX + normalizedSymbol;
        String cachedPrice = redisTemplate.opsForValue().get(redisKey);

        if (cachedPrice != null) {
            return new BigDecimal(cachedPrice);
        }

        return stockPriceRepository
                .findTopByStockOrderByRecordedAtDesc(stock)
                .map(StockPrice::getPrice)
                .orElseThrow(() ->
                        new PriceNotAvailableException("Price not available for stock: " + normalizedSymbol)
                );
    }

}
