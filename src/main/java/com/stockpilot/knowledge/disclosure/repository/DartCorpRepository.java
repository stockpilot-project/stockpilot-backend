package com.stockpilot.knowledge.disclosure.repository;

import com.stockpilot.knowledge.disclosure.entity.DartCorp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DartCorpRepository extends JpaRepository<DartCorp, Long> {

    Optional<DartCorp> findByCorpCode(String corpCode);

    Optional<DartCorp> findByStockCode(String stockCode);
}
