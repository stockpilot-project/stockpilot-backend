package com.stockpilot.knowledge.indicator.dto;

import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import com.stockpilot.knowledge.indicator.entity.IndicatorSource;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IndicatorDetailResponse {

    private Long id;
    private String code;
    private String name;
    private String unit;
    private Frequency frequency;
    private IndicatorSource source;
    private String description;
    private String whyItMatters;
    private Long relatedTermId;
    private String relatedTermName;
    private int displayOrder;

    public static IndicatorDetailResponse from(EconomicIndicator indicator) {
        return IndicatorDetailResponse.builder()
                .id(indicator.getId())
                .code(indicator.getCode())
                .name(indicator.getName())
                .unit(indicator.getUnit())
                .frequency(indicator.getFrequency())
                .source(indicator.getSource())
                .description(indicator.getDescription())
                .whyItMatters(indicator.getWhyItMatters())
                .relatedTermId(indicator.getRelatedTerm() != null ? indicator.getRelatedTerm().getId() : null)
                .relatedTermName(indicator.getRelatedTerm() != null ? indicator.getRelatedTerm().getName() : null)
                .displayOrder(indicator.getDisplayOrder())
                .build();
    }
}
