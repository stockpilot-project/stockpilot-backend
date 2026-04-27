package com.stockpilot.knowledge.indicator.repository;

import com.stockpilot.knowledge.indicator.entity.IndicatorObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndicatorObservationRepository extends JpaRepository<IndicatorObservation, Long> {

    List<IndicatorObservation> findByIndicatorIdAndObservedAtBetweenOrderByObservedAtAsc(
            Long indicatorId, LocalDate from, LocalDate to);

    Optional<IndicatorObservation> findFirstByIndicatorIdOrderByObservedAtDesc(Long indicatorId);

    Optional<IndicatorObservation> findFirstByIndicatorIdAndObservedAtLessThanOrderByObservedAtDesc(
            Long indicatorId, LocalDate observedAt);

    boolean existsByIndicatorIdAndObservedAt(Long indicatorId, LocalDate observedAt);

    long countByIndicatorId(Long indicatorId);
}
