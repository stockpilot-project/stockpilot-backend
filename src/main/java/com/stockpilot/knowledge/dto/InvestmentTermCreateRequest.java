package com.stockpilot.knowledge.dto;

import com.stockpilot.knowledge.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InvestmentTermCreateRequest {

    @NotBlank(message = "카테고리 코드는 필수입니다.")
    private String categoryCode;

    @NotBlank(message = "용어 이름은 필수입니다.")
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "요약은 필수입니다.")
    @Size(max = 500)
    private String summary;

    private String description;

    @Size(max = 500)
    private String formula;

    private String example;

    @NotNull(message = "난이도는 필수입니다.")
    private Difficulty difficulty;

    private List<String> relatedTermNames;
}
