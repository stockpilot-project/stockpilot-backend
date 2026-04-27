package com.stockpilot.knowledge.indicator.entity;

import com.stockpilot.global.common.entity.Timestamp;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "economic_indicators")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EconomicIndicator extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IndicatorSource source;

    @Column(name = "source_stat_code", nullable = false, length = 50)
    private String sourceStatCode;

    @Column(name = "source_item_code", length = 50)
    private String sourceItemCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "why_it_matters", columnDefinition = "TEXT")
    private String whyItMatters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_term_id")
    private InvestmentTerm relatedTerm;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    public EconomicIndicator(String code, String name, String unit, Frequency frequency,
                             IndicatorSource source, String sourceStatCode, String sourceItemCode,
                             String description, String whyItMatters, InvestmentTerm relatedTerm,
                             int displayOrder) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.source = source;
        this.sourceStatCode = sourceStatCode;
        this.sourceItemCode = sourceItemCode;
        this.description = description;
        this.whyItMatters = whyItMatters;
        this.relatedTerm = relatedTerm;
        this.displayOrder = displayOrder;
    }
}
