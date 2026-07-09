package com.stocktracker.stock_portfolio_tracker.stock.service;

import com.stocktracker.stock_portfolio_tracker.exception.DuplicateResourceException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockRequest;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockResponse;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public StockResponse createStock(StockRequest request) {

        String symbol = request.symbol().toUpperCase();

        if (stockRepository.existsBySymbol(symbol)) {
            throw new DuplicateResourceException("Stock already exists with symbol: " + symbol);
        }

        Stock stock = Stock.builder()
                .symbol(symbol)
                .companyName(request.companyName())
                .exchange(request.exchange())
                .sector(request.sector())
                .currency(request.currency().toUpperCase())
                .active(true)
                .build();

        Stock savedStock = stockRepository.save(stock);

        return mapToResponse(savedStock);
    }

    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StockResponse getStockById(UUID id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with id: " + id)
                );

        return mapToResponse(stock);
    }

    public StockResponse getStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with symbol: " + symbol)
                );

        return mapToResponse(stock);
    }

    public StockResponse updateStock(UUID id, StockRequest request) {

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with id: " + id)
                );

        String newSymbol = request.symbol().toUpperCase();

        if (!stock.getSymbol().equals(newSymbol)
                && stockRepository.existsBySymbol(newSymbol)) {
            throw new DuplicateResourceException("Stock already exists with symbol: " + newSymbol);
        }

        stock.setSymbol(newSymbol);
        stock.setCompanyName(request.companyName());
        stock.setExchange(request.exchange());
        stock.setSector(request.sector());
        stock.setCurrency(request.currency().toUpperCase());

        Stock updatedStock = stockRepository.save(stock);

        return mapToResponse(updatedStock);
    }

    public StockResponse activateStock(UUID id) {
        Stock stock = getStockEntityById(id);
        stock.setActive(true);
        return mapToResponse(stockRepository.save(stock));
    }

    public StockResponse deactivateStock(UUID id) {
        Stock stock = getStockEntityById(id);
        stock.setActive(false);
        return mapToResponse(stockRepository.save(stock));
    }

    private Stock getStockEntityById(UUID id) {
        return stockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock not found with id: " + id)
                );
    }

    private StockResponse mapToResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getExchange(),
                stock.getSector(),
                stock.getCurrency(),
                stock.isActive()
        );
    }

}
