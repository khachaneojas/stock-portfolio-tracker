package com.stocktracker.stock_portfolio_tracker.price.service;

import com.stocktracker.stock_portfolio_tracker.price.dto.FinnhubQuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final RestClient.Builder restClientBuilder;

    @Value("${market.data.finnhub.base-url}")
    private String baseUrl;

    @Value("${market.data.finnhub.api-key}")
    private String apiKey;

    @Value("${market.data.finnhub.enabled:true}")
    private boolean enabled;

    public BigDecimal getLatestPrice(String symbol) {

        if (!enabled) {
            return null;
        }

        RestClient restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();

        FinnhubQuoteResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(FinnhubQuoteResponse.class);

        if (response == null || response.c() == null || response.c().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return response.c();
    }

}
