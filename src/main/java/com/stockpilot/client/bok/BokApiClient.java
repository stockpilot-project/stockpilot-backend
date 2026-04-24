package com.stockpilot.client.bok;

import com.stockpilot.client.bok.dto.EcosRow;
import com.stockpilot.client.bok.dto.EcosSearchResponse;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BokApiClient {

    private static final int MAX_ROWS = 10000;
    private static final String ERROR_CODE_OK = "INFO-000";
    private static final String ERROR_CODE_NO_DATA = "INFO-200";

    private final RestClient restClient;
    private final BokApiProperties properties;

    public BokApiClient(
            @Qualifier("bokRestClient") RestClient restClient,
            BokApiProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isEnabled() && hasApiKey();
    }

    public List<BokObservation> fetch(String statCode,
                                      String itemCode,
                                      Frequency frequency,
                                      LocalDate from,
                                      LocalDate to) {
        if (!isEnabled()) {
            log.warn("BOK API is disabled or missing API key. Returning empty list.");
            return List.of();
        }

        String cycle = toCycleCode(frequency);
        String startKey = toPeriodKey(frequency, from);
        String endKey = toPeriodKey(frequency, to);
        String path = buildPath(statCode, cycle, startKey, endKey, itemCode);

        EcosSearchResponse response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> UriComponentsBuilder.fromUriString(path).build(false).toUri())
                    .retrieve()
                    .body(EcosSearchResponse.class);
        } catch (RestClientException e) {
            throw new BokApiException("ECOS API call failed: " + e.getMessage(), e);
        }

        if (response == null) {
            throw new BokApiException("ECOS API returned null response");
        }

        if (response.getResult() != null && response.getResult().getCode() != null) {
            String code = response.getResult().getCode();
            if (ERROR_CODE_NO_DATA.equals(code)) {
                log.info("ECOS API reported no data: stat={} item={} {}..{}", statCode, itemCode, startKey, endKey);
                return List.of();
            }
            if (!ERROR_CODE_OK.equals(code)) {
                throw new BokApiException("ECOS API error [" + code + "]: " + response.getResult().getMessage());
            }
        }

        if (response.getStatisticSearch() == null || response.getStatisticSearch().getRows() == null) {
            return List.of();
        }

        List<BokObservation> observations = new ArrayList<>();
        for (EcosRow row : response.getStatisticSearch().getRows()) {
            LocalDate observedAt = parseTime(row.getTime(), frequency);
            if (observedAt == null) continue;
            if (row.getDataValue() == null || row.getDataValue().isBlank()) continue;
            try {
                observations.add(new BokObservation(observedAt, new BigDecimal(row.getDataValue().trim())));
            } catch (NumberFormatException e) {
                log.warn("Skipping unparsable ECOS value: time={} value={}", row.getTime(), row.getDataValue());
            }
        }
        return observations;
    }

    private String buildPath(String statCode, String cycle, String startKey, String endKey, String itemCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("/StatisticSearch/")
                .append(properties.getApiKey())
                .append("/json/kr/1/")
                .append(MAX_ROWS)
                .append('/')
                .append(statCode)
                .append('/')
                .append(cycle)
                .append('/')
                .append(startKey)
                .append('/')
                .append(endKey);
        if (itemCode != null && !itemCode.isBlank()) {
            sb.append('/').append(itemCode);
        }
        return sb.toString();
    }

    private boolean hasApiKey() {
        return properties.getApiKey() != null && !properties.getApiKey().isBlank();
    }

    private String toCycleCode(Frequency frequency) {
        return switch (frequency) {
            case DAILY -> "D";
            case MONTHLY -> "M";
            case QUARTERLY -> "Q";
        };
    }

    private String toPeriodKey(Frequency frequency, LocalDate date) {
        return switch (frequency) {
            case DAILY -> date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            case MONTHLY -> date.format(DateTimeFormatter.ofPattern("yyyyMM"));
            case QUARTERLY -> String.format("%dQ%d", date.getYear(), (date.getMonthValue() - 1) / 3 + 1);
        };
    }

    private LocalDate parseTime(String time, Frequency frequency) {
        if (time == null || time.isBlank()) return null;
        try {
            return switch (frequency) {
                case DAILY -> LocalDate.parse(time, DateTimeFormatter.ofPattern("yyyyMMdd"));
                case MONTHLY -> LocalDate.of(
                        Integer.parseInt(time.substring(0, 4)),
                        Integer.parseInt(time.substring(4, 6)),
                        1);
                case QUARTERLY -> {
                    int year = Integer.parseInt(time.substring(0, 4));
                    int quarter = Integer.parseInt(time.substring(time.length() - 1));
                    int month = (quarter - 1) * 3 + 1;
                    yield LocalDate.of(year, month, 1);
                }
            };
        } catch (Exception e) {
            log.warn("Failed to parse ECOS TIME value: {} (cycle={})", time, frequency);
            return null;
        }
    }
}
