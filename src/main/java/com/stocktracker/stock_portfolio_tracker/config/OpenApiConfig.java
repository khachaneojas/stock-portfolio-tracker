package com.stocktracker.stock_portfolio_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stockPortfolioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Real-Time Stock Portfolio Tracker API")
                        .version("1.0.0")
                        .description("Backend API for tracking stock portfolios, live prices, alerts, and performance analytics.")
                        .contact(new Contact()
                                .name("StockTracker")
                                .email("support@stocktracker.com"))
                        .license(new License()
                                .name("MIT License")));
    }

}
