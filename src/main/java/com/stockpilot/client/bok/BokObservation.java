package com.stockpilot.client.bok;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class BokObservation {

    private final LocalDate observedAt;
    private final BigDecimal value;
}
