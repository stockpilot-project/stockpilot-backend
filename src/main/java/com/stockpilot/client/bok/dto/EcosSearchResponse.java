package com.stockpilot.client.bok.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ECOS StatisticSearch API 응답.
 *
 * 정상 응답은 "StatisticSearch" 필드에 데이터가 담기고,
 * 오류일 때는 "RESULT" 필드에 CODE/MESSAGE가 담긴다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcosSearchResponse {

    @JsonProperty("StatisticSearch")
    private StatisticSearch statisticSearch;

    @JsonProperty("RESULT")
    private ResultInfo result;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatisticSearch {

        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("row")
        private List<EcosRow> rows;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultInfo {

        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }
}
