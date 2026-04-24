package com.stockpilot.knowledge.dto;

import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InvestmentTermDetailResponse {

    private Long id;
    private String name;
    private String fullName;
    private String summary;
    private String description;
    private String formula;
    private String example;
    private TermCategoryResponse category;
    private Difficulty difficulty;
    private List<InvestmentTermSummaryResponse> relatedTerms;

    public static InvestmentTermDetailResponse of(InvestmentTerm term, List<InvestmentTerm> relatedTerms) {
        return InvestmentTermDetailResponse.builder()
                .id(term.getId())
                .name(term.getName())
                .fullName(term.getFullName())
                .summary(term.getSummary())
                .description(term.getDescription())
                .formula(term.getFormula())
                .example(term.getExample())
                .category(TermCategoryResponse.from(term.getCategory()))
                .difficulty(term.getDifficulty())
                .relatedTerms(relatedTerms.stream()
                        .map(InvestmentTermSummaryResponse::from)
                        .toList())
                .build();
    }
}
