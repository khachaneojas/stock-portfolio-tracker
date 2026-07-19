package com.stocktracker.stock_portfolio_tracker.stock.service;

import com.stocktracker.stock_portfolio_tracker.common.enums.StockExchange;
import com.stocktracker.stock_portfolio_tracker.exception.DuplicateResourceException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockRequest;
import com.stocktracker.stock_portfolio_tracker.stock.dto.StockResponse;
import com.stocktracker.stock_portfolio_tracker.stock.entity.Stock;
import com.stocktracker.stock_portfolio_tracker.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {


    @Mock
    private StockRepository stockRepository;

    private StockService stockService;

    private StockExchange exchange;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository);

        /*
         * This avoids assuming a particular enum constant such as
         * NASDAQ or NYSE.
         */
        exchange = StockExchange.values()[0];
    }

    @Test
    void shouldCreateStockSuccessfully() {
        StockRequest request = new StockRequest(
                "aapl",
                "Apple Inc.",
                exchange,
                "Technology",
                "usd"
        );

        UUID stockId = UUID.randomUUID();

        when(stockRepository.existsBySymbol("AAPL"))
                .thenReturn(false);

        when(stockRepository.save(any(Stock.class)))
                .thenAnswer(invocation -> {
                    Stock stock = invocation.getArgument(0);
                    stock.setId(stockId);
                    return stock;
                });

        StockResponse response =
                stockService.createStock(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(stockId);
        assertThat(response.symbol()).isEqualTo("AAPL");
        assertThat(response.companyName())
                .isEqualTo("Apple Inc.");
        assertThat(response.exchange())
                .isEqualTo(exchange);
        assertThat(response.sector())
                .isEqualTo("Technology");
        assertThat(response.currency())
                .isEqualTo("USD");
        assertThat(response.active()).isTrue();

        ArgumentCaptor<Stock> stockCaptor =
                ArgumentCaptor.forClass(Stock.class);

        verify(stockRepository)
                .save(stockCaptor.capture());

        Stock savedStock = stockCaptor.getValue();

        assertThat(savedStock.getSymbol())
                .isEqualTo("AAPL");
        assertThat(savedStock.getCurrency())
                .isEqualTo("USD");
        assertThat(savedStock.isActive())
                .isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateStock() {
        StockRequest request = new StockRequest(
                "aapl",
                "Apple Inc.",
                exchange,
                "Technology",
                "usd"
        );

        when(stockRepository.existsBySymbol("AAPL"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                stockService.createStock(request)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(
                        "Stock already exists with symbol: AAPL"
                );

        verify(stockRepository)
                .existsBySymbol("AAPL");

        verify(stockRepository, never())
                .save(any(Stock.class));
    }

    @Test
    void shouldReturnAllStocks() {
        Stock apple = createStock(
                UUID.randomUUID(),
                "AAPL",
                "Apple Inc.",
                true
        );

        Stock microsoft = createStock(
                UUID.randomUUID(),
                "MSFT",
                "Microsoft Corporation",
                true
        );

        when(stockRepository.findAll())
                .thenReturn(List.of(apple, microsoft));

        List<StockResponse> result =
                stockService.getAllStocks();

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(StockResponse::symbol)
                .containsExactly("AAPL", "MSFT");

        verify(stockRepository).findAll();
    }

    @Test
    void shouldReturnStockById() {
        UUID stockId = UUID.randomUUID();

        Stock stock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                true
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(stock));

        StockResponse response =
                stockService.getStockById(stockId);

        assertThat(response.id())
                .isEqualTo(stockId);
        assertThat(response.symbol())
                .isEqualTo("AAPL");
        assertThat(response.companyName())
                .isEqualTo("Apple Inc.");

        verify(stockRepository)
                .findById(stockId);
    }

    @Test
    void shouldThrowExceptionWhenStockIdDoesNotExist() {
        UUID stockId = UUID.randomUUID();

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                stockService.getStockById(stockId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Stock not found with id: " + stockId
                );

        verify(stockRepository)
                .findById(stockId);
    }

    @Test
    void shouldReturnStockBySymbol() {
        Stock stock = createStock(
                UUID.randomUUID(),
                "AAPL",
                "Apple Inc.",
                true
        );

        when(stockRepository.findBySymbol("AAPL"))
                .thenReturn(Optional.of(stock));

        StockResponse response =
                stockService.getStockBySymbol("aapl");

        assertThat(response.symbol())
                .isEqualTo("AAPL");

        verify(stockRepository)
                .findBySymbol("AAPL");
    }

    @Test
    void shouldThrowExceptionWhenStockSymbolDoesNotExist() {
        when(stockRepository.findBySymbol("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                stockService.getStockBySymbol("unknown")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Stock not found with symbol: unknown"
                );

        verify(stockRepository)
                .findBySymbol("UNKNOWN");
    }

    @Test
    void shouldUpdateStockSuccessfully() {
        UUID stockId = UUID.randomUUID();

        Stock existingStock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                true
        );

        StockRequest request = new StockRequest(
                "appl",
                "Apple Incorporated",
                exchange,
                "Consumer Technology",
                "gbp"
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(existingStock));

        when(stockRepository.existsBySymbol("APPL"))
                .thenReturn(false);

        when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockResponse response =
                stockService.updateStock(stockId, request);

        assertThat(response.id())
                .isEqualTo(stockId);
        assertThat(response.symbol())
                .isEqualTo("APPL");
        assertThat(response.companyName())
                .isEqualTo("Apple Incorporated");
        assertThat(response.sector())
                .isEqualTo("Consumer Technology");
        assertThat(response.currency())
                .isEqualTo("GBP");

        verify(stockRepository)
                .existsBySymbol("APPL");

        verify(stockRepository)
                .save(existingStock);
    }

    @Test
    void shouldNotCheckDuplicateWhenSymbolIsUnchanged() {
        UUID stockId = UUID.randomUUID();

        Stock existingStock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                true
        );

        StockRequest request = new StockRequest(
                "aapl",
                "Apple Incorporated",
                exchange,
                "Technology",
                "usd"
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(existingStock));

        when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockResponse response =
                stockService.updateStock(stockId, request);

        assertThat(response.symbol())
                .isEqualTo("AAPL");

        assertThat(response.companyName())
                .isEqualTo("Apple Incorporated");

        verify(stockRepository, never())
                .existsBySymbol(anyString());

        verify(stockRepository)
                .save(existingStock);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingSymbol() {
        UUID stockId = UUID.randomUUID();

        Stock existingStock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                true
        );

        StockRequest request = new StockRequest(
                "msft",
                "Microsoft Corporation",
                exchange,
                "Technology",
                "usd"
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(existingStock));

        when(stockRepository.existsBySymbol("MSFT"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                stockService.updateStock(stockId, request)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(
                        "Stock already exists with symbol: MSFT"
                );

        verify(stockRepository, never())
                .save(any(Stock.class));
    }

    @Test
    void shouldActivateStock() {
        UUID stockId = UUID.randomUUID();

        Stock stock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                false
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(stock));

        when(stockRepository.save(stock))
                .thenReturn(stock);

        StockResponse response =
                stockService.activateStock(stockId);

        assertThat(response.active()).isTrue();
        assertThat(stock.isActive()).isTrue();

        verify(stockRepository)
                .save(stock);
    }

    @Test
    void shouldDeactivateStock() {
        UUID stockId = UUID.randomUUID();

        Stock stock = createStock(
                stockId,
                "AAPL",
                "Apple Inc.",
                true
        );

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(stock));

        when(stockRepository.save(stock))
                .thenReturn(stock);

        StockResponse response =
                stockService.deactivateStock(stockId);

        assertThat(response.active()).isFalse();
        assertThat(stock.isActive()).isFalse();

        verify(stockRepository)
                .save(stock);
    }

    private Stock createStock(
            UUID id,
            String symbol,
            String companyName,
            boolean active
    ) {
        return Stock.builder()
                .id(id)
                .symbol(symbol)
                .companyName(companyName)
                .exchange(exchange)
                .sector("Technology")
                .currency("USD")
                .active(active)
                .build();
    }

}
