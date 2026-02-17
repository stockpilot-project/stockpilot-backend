package com.stockpilot.client.yahoo;

import com.stockpilot.global.config.properties.YahooFinanceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class YahooFinanceConfig {

    private final YahooFinanceProperties properties;

    @Bean
    public RestClient yahooFinanceRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("x-rapidapi-key", properties.getApiKey())
                .defaultHeader("x-rapidapi-host", properties.getApiHost())
                .build();
    }
}
