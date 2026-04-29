package com.stockpilot.knowledge.disclosure.repository;

import com.stockpilot.knowledge.disclosure.entity.Disclosure;
import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface DisclosureRepository extends JpaRepository<Disclosure, Long> {

    boolean existsByReceiptNo(String receiptNo);

    @EntityGraph(attributePaths = "stock")
    Optional<Disclosure> findWithStockById(Long id);

    @EntityGraph(attributePaths = "stock")
    @Query("""
            SELECT d FROM Disclosure d
            WHERE (:type IS NULL OR d.disclosureType = :type)
              AND (:from IS NULL OR d.submittedAt >= :from)
              AND (:to IS NULL OR d.submittedAt <= :to)
              AND (:q IS NULL
                   OR LOWER(d.reportName) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(d.corpName) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Disclosure> search(@Param("type") DisclosureType type,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            @Param("q") String q,
                            Pageable pageable);

    @EntityGraph(attributePaths = "stock")
    @Query("""
            SELECT d FROM Disclosure d
            WHERE d.stock.id = :stockId
              AND (:type IS NULL OR d.disclosureType = :type)
            """)
    Page<Disclosure> findByStockId(@Param("stockId") Long stockId,
                                   @Param("type") DisclosureType type,
                                   Pageable pageable);

    @EntityGraph(attributePaths = "stock")
    @Query("""
            SELECT d FROM Disclosure d
            WHERE d.stock.id IN :stockIds
              AND (:type IS NULL OR d.disclosureType = :type)
            """)
    Page<Disclosure> findByStockIds(@Param("stockIds") Collection<Long> stockIds,
                                    @Param("type") DisclosureType type,
                                    Pageable pageable);
}
