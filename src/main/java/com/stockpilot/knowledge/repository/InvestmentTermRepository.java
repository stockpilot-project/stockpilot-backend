package com.stockpilot.knowledge.repository;

import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestmentTermRepository extends JpaRepository<InvestmentTerm, Long> {

    Optional<InvestmentTerm> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = "category")
    @Query("""
            SELECT t FROM InvestmentTerm t
            WHERE (:categoryCode IS NULL OR t.category.code = :categoryCode)
              AND (:difficulty IS NULL OR t.difficulty = :difficulty)
              AND (:q IS NULL
                   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(t.summary) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(COALESCE(t.fullName, '')) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<InvestmentTerm> search(@Param("categoryCode") String categoryCode,
                                @Param("difficulty") Difficulty difficulty,
                                @Param("q") String q,
                                Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Query("""
            SELECT t FROM InvestmentTerm t
            WHERE LOWER(t.name) LIKE LOWER(CONCAT(:q, '%'))
               OR LOWER(COALESCE(t.fullName, '')) LIKE LOWER(CONCAT(:q, '%'))
            ORDER BY t.name ASC
            """)
    List<InvestmentTerm> autocomplete(@Param("q") String q, Pageable pageable);

    @Query("SELECT COUNT(t) FROM InvestmentTerm t WHERE t.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
