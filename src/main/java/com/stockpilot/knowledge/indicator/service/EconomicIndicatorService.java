package com.stockpilot.knowledge.indicator.service;

import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.knowledge.indicator.dto.IndicatorDetailResponse;
import com.stockpilot.knowledge.indicator.dto.IndicatorSummaryResponse;
import com.stockpilot.knowledge.indicator.dto.ObservationResponse;
import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.IndicatorObservation;
import com.stockpilot.knowledge.indicator.repository.EconomicIndicatorRepository;
import com.stockpilot.knowledge.indicator.repository.IndicatorObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EconomicIndicatorService {

    private final EconomicIndicatorRepository indicatorRepository;
    private final IndicatorObservationRepository observationRepository;

    @Cacheable(value = "indicatorSummaries", key = "'all'")
    public List<IndicatorSummaryResponse> getIndicators() {
        return indicatorRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Cacheable(value = "indicatorDetail", key = "#id")
    public IndicatorDetailResponse getIndicator(Long id) {
        EconomicIndicator indicator = indicatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INDICATOR_NOT_FOUND));
        return IndicatorDetailResponse.from(indicator);
    }

    @Cacheable(value = "indicatorObservations", key = "#id + ':' + #from + ':' + #to")
    public List<ObservationResponse> getObservations(Long id, LocalDate from, LocalDate to) {
        if (!indicatorRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.INDICATOR_NOT_FOUND);
        }
        LocalDate safeTo = (to != null) ? to : LocalDate.now();
        LocalDate safeFrom = (from != null) ? from : safeTo.minusYears(1);
        return observationRepository
                .findByIndicatorIdAndObservedAtBetweenOrderByObservedAtAsc(id, safeFrom, safeTo)
                .stream()
                .map(ObservationResponse::from)
                .toList();
    }

    private IndicatorSummaryResponse toSummary(EconomicIndicator indicator) {
        IndicatorObservation latest = observationRepository
                .findFirstByIndicatorIdOrderByObservedAtDesc(indicator.getId())
                .orElse(null);
        if (latest == null) {
            return IndicatorSummaryResponse.of(indicator, null, null, null);
        }
        BigDecimal change = observationRepository
                .findFirstByIndicatorIdAndObservedAtLessThanOrderByObservedAtDesc(
                        indicator.getId(), latest.getObservedAt())
                .map(prev -> latest.getValue().subtract(prev.getValue()))
                .orElse(null);
        return IndicatorSummaryResponse.of(indicator, latest.getValue(), latest.getObservedAt(), change);
    }
}
