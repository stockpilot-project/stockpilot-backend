package com.stockpilot.client.yahoo;

import com.stockpilot.client.yahoo.dto.YahooQuoteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class YahooFinanceClient {

    private final RestClient restClient;

    public YahooFinanceClient(@Qualifier("yahooFinanceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<List<YahooQuoteResponse.Quote>> getQuotes(List<String> symbols) {
        try {
            String symbolParam = String.join(",", symbols);
            YahooQuoteResponse response = restClient.get()
                    .uri("/api/v1/markets/stock/quotes?ticker={symbols}", symbolParam)
                    .retrieve()
                    .body(YahooQuoteResponse.class);

            if (response != null && response.getBody() != null && response.getBody().getResult() != null) {
                return Optional.of(response.getBody().getResult());
            }
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Yahoo Finance API call failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
