package com.stockpilot.knowledge.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.knowledge.dto.InvestmentTermCreateRequest;
import com.stockpilot.knowledge.dto.InvestmentTermDetailResponse;
import com.stockpilot.knowledge.dto.InvestmentTermSummaryResponse;
import com.stockpilot.knowledge.dto.InvestmentTermUpdateRequest;
import com.stockpilot.knowledge.dto.TermCategoryResponse;
import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.service.InvestmentTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "InvestmentTerm", description = "투자 용어 사전 API")
@RestController
@RequestMapping("/api/v1/investment-terms")
@RequiredArgsConstructor
public class InvestmentTermController {

    private final InvestmentTermService investmentTermService;

    @Operation(summary = "용어 목록 조회", description = "카테고리/난이도/검색어로 필터링, 페이징된 목록을 반환합니다.")
    @GetMapping
    public ApiResponse<Page<InvestmentTermSummaryResponse>> getTerms(
            @Parameter(description = "카테고리 코드", example = "FINANCIAL_RATIO")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "난이도", example = "BEGINNER")
            @RequestParam(required = false) Difficulty difficulty,
            @Parameter(description = "검색어", example = "PER")
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.ok(investmentTermService.getTerms(categoryCode, difficulty, q, pageable));
    }

    @Operation(summary = "용어 상세 조회", description = "용어 상세 + 관련 용어 목록을 반환합니다.")
    @GetMapping("/{id}")
    public ApiResponse<InvestmentTermDetailResponse> getTerm(
            @Parameter(description = "용어 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.ok(investmentTermService.getTerm(id));
    }

    @Operation(summary = "용어 자동완성", description = "name/fullName의 접두사 매칭으로 상위 N개를 반환합니다.")
    @GetMapping("/search")
    public ApiResponse<List<InvestmentTermSummaryResponse>> autocomplete(
            @Parameter(description = "검색어 (prefix)", example = "PE")
            @RequestParam String q,
            @Parameter(description = "최대 개수 (1~20)", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.ok(investmentTermService.autocomplete(q, limit));
    }

    @Operation(summary = "카테고리 목록 조회", description = "각 카테고리의 용어 수를 포함해 반환합니다.")
    @GetMapping("/categories")
    public ApiResponse<List<TermCategoryResponse>> getCategories() {
        return ApiResponse.ok(investmentTermService.getCategories());
    }

    // TODO: 관리자 인증 적용 필요 (Spring Security 도입 이후)
    @Operation(summary = "용어 등록", description = "새 용어를 등록합니다. (관리자 전용 예정)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InvestmentTermDetailResponse> createTerm(
            @Valid @RequestBody InvestmentTermCreateRequest request
    ) {
        return ApiResponse.ok(investmentTermService.createTerm(request));
    }

    // TODO: 관리자 인증 적용 필요 (Spring Security 도입 이후)
    @Operation(summary = "용어 수정", description = "기존 용어를 수정합니다. (관리자 전용 예정)")
    @PatchMapping("/{id}")
    public ApiResponse<InvestmentTermDetailResponse> updateTerm(
            @Parameter(description = "용어 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody InvestmentTermUpdateRequest request
    ) {
        return ApiResponse.ok(investmentTermService.updateTerm(id, request));
    }

    // TODO: 관리자 인증 적용 필요 (Spring Security 도입 이후)
    @Operation(summary = "용어 삭제", description = "용어를 삭제합니다. (관리자 전용 예정)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteTerm(
            @Parameter(description = "용어 ID", example = "1")
            @PathVariable Long id
    ) {
        investmentTermService.deleteTerm(id);
        return ApiResponse.ok();
    }
}
