package com.stockpilot.knowledge.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE investment_term_relations SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(
        name = "investment_term_relations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_term_relation_pair",
                columnNames = {"term_id", "related_term_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentTermRelation extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private InvestmentTerm term;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "related_term_id", nullable = false)
    private InvestmentTerm relatedTerm;

    @Builder
    public InvestmentTermRelation(InvestmentTerm term, InvestmentTerm relatedTerm) {
        this.term = term;
        this.relatedTerm = relatedTerm;
    }
}
