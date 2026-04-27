package com.stockpilot.knowledge.indicator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stockpilot.knowledge.indicator.entity.IndicatorObservation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ObservationResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate observedAt;

    private BigDecimal value;

    public static ObservationResponse from(IndicatorObservation observation) {
        return ObservationResponse.builder()
                .observedAt(observation.getObservedAt())
                .value(observation.getValue())
                .build();
    }
}
