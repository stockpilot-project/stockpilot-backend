package com.stockpilot.knowledge.disclosure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "disclosure")
public class DisclosureProperties {

    private List<String> trackedTickers = new ArrayList<>();
}
