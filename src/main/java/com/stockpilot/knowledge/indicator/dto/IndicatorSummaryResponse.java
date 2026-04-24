package com.stockpilot.knowledge.indicator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import com.stockpilot.knowledge.indicator.entity.IndicatorSource;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class IndicatorSummaryResponse {

    private Long id;
    private String code;
    private String name;
    private String unit;
    private Frequency frequency;
    private IndicatorSource source;
    private BigDecimal latestValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate latestObservedAt;

    private BigDecimal changeFromPrev;
    private Long relatedTermId;
    private int displayOrder;

    public static IndicatorSummaryResponse of(EconomicIndicator indicator,
                                              BigDecimal latestValue,
                                              LocalDate latestObservedAt,
                                              BigDecimal changeFromPrev) {
        return IndicatorSummaryResponse.builder()
                .id(indicator.getId())
                .code(indicator.getCode())
                .name(indicator.getName())
                .unit(indicator.getUnit())
                .frequency(indicator.getFrequency())
                .source(indicator.getSource())
                .latestValue(latestValue)
                .latestObservedAt(latestObservedAt)
                .changeFromPrev(changeFromPrev)
                .relatedTermId(indicator.getRelatedTerm() != null ? indicator.getRelatedTerm().getId() : null)
                .displayOrder(indicator.getDisplayOrder())
                .build();
    }
}
