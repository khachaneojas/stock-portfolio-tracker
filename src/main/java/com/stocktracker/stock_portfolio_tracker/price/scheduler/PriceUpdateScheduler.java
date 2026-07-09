package com.stocktracker.stock_portfolio_tracker.price.scheduler;

import com.stocktracker.stock_portfolio_tracker.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceUpdateScheduler {

    private final PriceService priceService;

    @Scheduled(fixedRate = 10000)
    public void updateStockPrices() {
        priceService.updatePricesForAllActiveStocks();
    }

}
