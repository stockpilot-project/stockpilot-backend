package com.stockpilot.knowledge.disclosure.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.knowledge.disclosure.dto.DisclosureDetailResponse;
import com.stockpilot.knowledge.disclosure.dto.DisclosureResponse;
import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import com.stockpilot.knowledge.disclosure.service.DisclosureIngestService;
import com.stockpilot.knowledge.disclosure.service.DisclosureQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Disclosure", description = "공시 정보 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DisclosureController {

    private final DisclosureQueryService queryService;
    private final DisclosureIngestService ingestService;

    @Operation(summary = "공시 목록 조회", description = "전체 공시 타임라인 (타입/기간/검색어 필터, 페이징)")
    @GetMapping("/disclosures")
    public ApiResponse<Page<DisclosureResponse>> getDisclosures(
            @Parameter(description = "공시 유형", example = "MATERIAL")
            @RequestParam(required = false) DisclosureType type,
            @Parameter(description = "시작일 (yyyy-MM-dd)", example = "2026-04-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "종료일 (yyyy-MM-dd)", example = "2026-04-30")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "검색어 (report_name / corp_name)", example = "배당")
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(queryService.search(type, from, to, q, pageable));
    }

    @Operation(summary = "공시 상세 조회", description = "단건 공시 상세 + 종목 요약")
    @GetMapping("/disclosures/{id}")
    public ApiResponse<DisclosureDetailResponse> getDisclosure(
            @Parameter(description = "공시 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.ok(queryService.get(id));
    }

    @Operation(summary = "종목별 공시 조회", description = "특정 종목의 공시 목록")
    @GetMapping("/stocks/{stockId}/disclosures")
    public ApiResponse<Page<DisclosureResponse>> getDisclosuresByStock(
            @Parameter(description = "종목 ID", example = "1")
            @PathVariable Long stockId,
            @Parameter(description = "공시 유형", example = "PERIODIC")
            @RequestParam(required = false) DisclosureType type,
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(queryService.findByStock(stockId, type, pageable));
    }

    @Operation(summary = "관심 목록 공시 조회", description = "관심 종목 전체의 공시를 통합 조회")
    @GetMapping("/watchlists/{watchlistId}/disclosures")
    public ApiResponse<Page<DisclosureResponse>> getDisclosuresByWatchlist(
            @Parameter(description = "관심 목록 ID", example = "1")
            @PathVariable Long watchlistId,
            @Parameter(description = "공시 유형", example = "MATERIAL")
            @RequestParam(required = false) DisclosureType type,
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(queryService.findByWatchlist(watchlistId, type, pageable));
    }

    // TODO: 관리자 인증 적용 필요 (Spring Security 도입 이후)
    @Operation(summary = "공시 수동 재수집", description = "추적 종목 + 관심 종목의 최근 3일 공시를 다시 수집합니다. (관리자 전용 예정)")
    @PostMapping("/disclosures/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> refresh() {
        ingestService.ingestForTrackedCorps();
        return ApiResponse.ok();
    }
}
