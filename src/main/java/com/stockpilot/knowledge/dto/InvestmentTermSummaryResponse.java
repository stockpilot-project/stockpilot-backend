package com.stockpilot.knowledge.dto;

import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvestmentTermSummaryResponse {

    private Long id;
    private String name;
    private String fullName;
    private String summary;
    private TermCategoryResponse category;
    private Difficulty difficulty;

    public static InvestmentTermSummaryResponse from(InvestmentTerm term) {
        return InvestmentTermSummaryResponse.builder()
                .id(term.getId())
                .name(term.getName())
                .fullName(term.getFullName())
                .summary(term.getSummary())
                .category(TermCategoryResponse.from(term.getCategory()))
                .difficulty(term.getDifficulty())
                .build();
    }
}
