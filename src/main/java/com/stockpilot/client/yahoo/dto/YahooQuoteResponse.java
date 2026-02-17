package com.stockpilot.client.yahoo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooQuoteResponse {

    private Body body;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private List<Quote> result;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quote {
        private String symbol;
        private Double regularMarketOpen;
        private Double regularMarketDayHigh;
        private Double regularMarketDayLow;
        private Double regularMarketPrice;
        private Long regularMarketVolume;
        private Double regularMarketChangePercent;
    }
}
