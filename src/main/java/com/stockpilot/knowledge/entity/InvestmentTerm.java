package com.stockpilot.knowledge.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investment_terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentTerm extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private TermCategory category;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String formula;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvestmentTermRelation> relations = new ArrayList<>();

    @Builder
    public InvestmentTerm(TermCategory category, String name, String fullName, String summary,
                          String description, String formula, String example, Difficulty difficulty) {
        this.category = category;
        this.name = name;
        this.fullName = fullName;
        this.summary = summary;
        this.description = description;
        this.formula = formula;
        this.example = example;
        this.difficulty = difficulty;
    }

    public void update(TermCategory category, String name, String fullName, String summary,
                       String description, String formula, String example, Difficulty difficulty) {
        if (category != null) this.category = category;
        if (name != null) this.name = name;
        if (fullName != null) this.fullName = fullName;
        if (summary != null) this.summary = summary;
        if (description != null) this.description = description;
        if (formula != null) this.formula = formula;
        if (example != null) this.example = example;
        if (difficulty != null) this.difficulty = difficulty;
    }
}
