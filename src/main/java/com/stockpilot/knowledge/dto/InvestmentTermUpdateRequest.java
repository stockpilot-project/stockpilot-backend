package com.stockpilot.knowledge.dto;

import com.stockpilot.knowledge.entity.Difficulty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InvestmentTermUpdateRequest {

    private String categoryCode;

    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String fullName;

    @Size(max = 500)
    private String summary;

    private String description;

    @Size(max = 500)
    private String formula;

    private String example;

    private Difficulty difficulty;

    private List<String> relatedTermNames;
}
