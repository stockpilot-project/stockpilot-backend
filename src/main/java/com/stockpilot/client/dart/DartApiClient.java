package com.stockpilot.client.dart;

import com.stockpilot.client.dart.dto.DartListItem;
import com.stockpilot.client.dart.dto.DartListResponse;
import com.stockpilot.client.dart.parser.DartCorpCodeXmlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DartApiClient {

    private static final String STATUS_OK = "000";
    private static final String STATUS_NO_DATA = "013";
    private static final String STATUS_RATE_LIMIT = "020";
    private static final DateTimeFormatter REQ_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES = 50;

    private final RestClient restClient;
    private final DartApiProperties properties;
    private final DartCorpCodeXmlParser corpCodeXmlParser;

    public DartApiClient(
            @Qualifier("dartRestClient") RestClient restClient,
            DartApiProperties properties,
            DartCorpCodeXmlParser corpCodeXmlParser
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.corpCodeXmlParser = corpCodeXmlParser;
    }

    public boolean isEnabled() {
        return properties.isEnabled()
                && properties.getApiKey() != null
                && !properties.getApiKey().isBlank();
    }

    public List<DartListItem> fetchDisclosures(String corpCode, LocalDate from, LocalDate to) {
        if (!isEnabled()) return List.of();

        List<DartListItem> all = new ArrayList<>();
        int page = 1;
        while (page <= MAX_PAGES) {
            DartListResponse response = callList(corpCode, from, to, page);
            if (response == null) break;

            String status = response.getStatus();
            if (STATUS_NO_DATA.equals(status)) break;
            if (STATUS_RATE_LIMIT.equals(status)) {
                throw new DartApiException("DART rate limit exceeded", status, true);
            }
            if (!STATUS_OK.equals(status)) {
                throw new DartApiException(
                        "DART list.json error [" + status + "]: " + response.getMessage(),
                        status, false);
            }

            if (response.getList() != null) {
                all.addAll(response.getList());
            }
            int totalPage = response.getTotalPage() != null ? response.getTotalPage() : 1;
            if (page >= totalPage) break;
            page++;
        }
        return all;
    }

    public List<com.stockpilot.client.dart.DartCorpEntry> fetchCorpCodes() {
        if (!isEnabled()) return List.of();
        try {
            byte[] zipBytes = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/corpCode.xml")
                            .queryParam("crtfc_key", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(byte[].class);
            if (zipBytes == null || zipBytes.length == 0) {
                throw new DartApiException("DART corpCode.xml returned empty body", null, false);
            }
            return corpCodeXmlParser.parse(zipBytes);
        } catch (RestClientException e) {
            throw new DartApiException("DART corpCode.xml call failed: " + e.getMessage(), e);
        }
    }

    private DartListResponse callList(String corpCode, LocalDate from, LocalDate to, int page) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/list.json")
                            .queryParam("crtfc_key", properties.getApiKey())
                            .queryParam("corp_code", corpCode)
                            .queryParam("bgn_de", from.format(REQ_DATE_FORMAT))
                            .queryParam("end_de", to.format(REQ_DATE_FORMAT))
                            .queryParam("page_no", page)
                            .queryParam("page_count", PAGE_SIZE)
                            .build())
                    .retrieve()
                    .body(DartListResponse.class);
        } catch (RestClientException e) {
            throw new DartApiException("DART list.json call failed: " + e.getMessage(), e);
        }
    }
}
