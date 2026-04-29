package com.stockpilot.client.dart;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "client.dart")
public class DartApiProperties {

    private String apiKey;
    private String baseUrl;
    private boolean enabled;
}
