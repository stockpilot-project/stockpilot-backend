package com.stockpilot.knowledge.indicator.repository;

import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EconomicIndicatorRepository extends JpaRepository<EconomicIndicator, Long> {

    @EntityGraph(attributePaths = "relatedTerm")
    List<EconomicIndicator> findAllByOrderByDisplayOrderAsc();

    Optional<EconomicIndicator> findByCode(String code);

    List<EconomicIndicator> findByFrequency(Frequency frequency);
}
