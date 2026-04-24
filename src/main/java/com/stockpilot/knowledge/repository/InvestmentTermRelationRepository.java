package com.stockpilot.knowledge.repository;

import com.stockpilot.knowledge.entity.InvestmentTerm;
import com.stockpilot.knowledge.entity.InvestmentTermRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvestmentTermRelationRepository extends JpaRepository<InvestmentTermRelation, Long> {

    @Query("""
            SELECT r.relatedTerm FROM InvestmentTermRelation r
            WHERE r.term.id = :termId
            ORDER BY r.relatedTerm.name ASC
            """)
    List<InvestmentTerm> findRelatedTerms(@Param("termId") Long termId);

    boolean existsByTermIdAndRelatedTermId(Long termId, Long relatedTermId);
}
