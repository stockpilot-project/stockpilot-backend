package com.stockpilot.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "yahoo-finance")
public class YahooFinanceProperties {

    private String apiKey;
    private String apiHost;
    private String baseUrl;
}
