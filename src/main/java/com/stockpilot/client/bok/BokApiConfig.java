package com.stockpilot.client.bok;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BokApiConfig {

    private final BokApiProperties properties;

    @Bean
    public RestClient bokRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
