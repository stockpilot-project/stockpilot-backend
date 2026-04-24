package com.stockpilot.knowledge.dto;

import com.stockpilot.knowledge.entity.TermCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermCategoryResponse {

    private Long id;
    private String code;
    private String name;
    private int displayOrder;
    private Long termCount;

    public static TermCategoryResponse from(TermCategory category, long termCount) {
        return TermCategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .termCount(termCount)
                .build();
    }

    public static TermCategoryResponse from(TermCategory category) {
        return from(category, 0L);
    }
}
