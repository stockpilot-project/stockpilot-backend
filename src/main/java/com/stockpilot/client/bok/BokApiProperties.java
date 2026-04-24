package com.stockpilot.client.bok;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "client.bok")
public class BokApiProperties {

    private String apiKey;
    private String baseUrl;
    private boolean enabled;
}
