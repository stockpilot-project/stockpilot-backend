package com.stockpilot.knowledge.indicator.service;

import com.stockpilot.client.bok.BokApiClient;
import com.stockpilot.client.bok.BokApiException;
import com.stockpilot.client.bok.BokObservation;
import com.stockpilot.global.error.exception.BusinessException;
import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import com.stockpilot.knowledge.indicator.entity.IndicatorObservation;
import com.stockpilot.knowledge.indicator.repository.EconomicIndicatorRepository;
import com.stockpilot.knowledge.indicator.repository.IndicatorObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EconomicIndicatorIngestService {

    private static final int BACKFILL_YEARS = 3;

    private final EconomicIndicatorRepository indicatorRepository;
    private final IndicatorObservationRepository observationRepository;
    private final BokApiClient bokApiClient;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "indicatorSummaries", allEntries = true),
            @CacheEvict(value = "indicatorObservations", allEntries = true)
    })
    public int ingestLatest(EconomicIndicator indicator) {
        LocalDate to = LocalDate.now();
        LocalDate from = observationRepository
                .findFirstByIndicatorIdOrderByObservedAtDesc(indicator.getId())
                .map(obs -> nextPeriodStart(indicator.getFrequency(), obs.getObservedAt()))
                .orElse(to.minusYears(BACKFILL_YEARS));

        if (from.isAfter(to)) {
            return 0;
        }
        return ingest(indicator, from, to);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "indicatorSummaries", allEntries = true),
            @CacheEvict(value = "indicatorObservations", allEntries = true)
    })
    public int ingest(EconomicIndicator indicator, LocalDate from, LocalDate to) {
        if (!bokApiClient.isEnabled()) {
            log.info("BOK client disabled. Skipping ingest for indicator '{}'.", indicator.getCode());
            return 0;
        }

        List<BokObservation> rows;
        try {
            rows = bokApiClient.fetch(
                    indicator.getSourceStatCode(),
                    indicator.getSourceItemCode(),
                    indicator.getFrequency(),
                    from,
                    to
            );
        } catch (BokApiException e) {
            log.error("BOK ingest failed for '{}' ({}..{}): {}",
                    indicator.getCode(), from, to, e.getMessage());
            throw new BusinessException(ErrorCode.INDICATOR_SOURCE_UNAVAILABLE);
        }

        int inserted = 0;
        for (BokObservation row : rows) {
            if (observationRepository.existsByIndicatorIdAndObservedAt(indicator.getId(), row.getObservedAt())) {
                continue;
            }
            observationRepository.save(IndicatorObservation.builder()
                    .indicator(indicator)
                    .observedAt(row.getObservedAt())
                    .value(row.getValue())
                    .build());
            inserted++;
        }
        log.info("Ingested {} new observations for '{}' ({}..{})",
                inserted, indicator.getCode(), from, to);
        return inserted;
    }

    public int ingestByFrequency(Frequency frequency) {
        List<EconomicIndicator> indicators = indicatorRepository.findByFrequency(frequency);
        int total = 0;
        for (EconomicIndicator indicator : indicators) {
            try {
                total += ingestLatest(indicator);
            } catch (RuntimeException e) {
                log.error("Ingest failed for indicator '{}': {}", indicator.getCode(), e.getMessage());
            }
        }
        return total;
    }

    public int ingestAll() {
        int total = 0;
        for (Frequency frequency : Frequency.values()) {
            total += ingestByFrequency(frequency);
        }
        return total;
    }

    public int refresh(Long indicatorId) {
        EconomicIndicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INDICATOR_NOT_FOUND));
        return ingestLatest(indicator);
    }

    private LocalDate nextPeriodStart(Frequency frequency, LocalDate last) {
        return switch (frequency) {
            case DAILY -> last.plusDays(1);
            case MONTHLY -> last.plusMonths(1).withDayOfMonth(1);
            case QUARTERLY -> last.plusMonths(3).withDayOfMonth(1);
        };
    }
}
