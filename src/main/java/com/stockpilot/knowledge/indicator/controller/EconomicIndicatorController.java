package com.stockpilot.knowledge.indicator.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.knowledge.indicator.dto.IndicatorDetailResponse;
import com.stockpilot.knowledge.indicator.dto.IndicatorSummaryResponse;
import com.stockpilot.knowledge.indicator.dto.ObservationResponse;
import com.stockpilot.knowledge.indicator.service.EconomicIndicatorIngestService;
import com.stockpilot.knowledge.indicator.service.EconomicIndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "EconomicIndicator", description = "경제지표 API")
@RestController
@RequestMapping("/api/v1/economic-indicators")
@RequiredArgsConstructor
public class EconomicIndicatorController {

    private final EconomicIndicatorService indicatorService;
    private final EconomicIndicatorIngestService ingestService;

    @Operation(summary = "지표 목록 조회", description = "등록된 모든 지표의 최신값/직전대비 변화량을 반환합니다.")
    @GetMapping
    public ApiResponse<List<IndicatorSummaryResponse>> getIndicators() {
        return ApiResponse.ok(indicatorService.getIndicators());
    }

    @Operation(summary = "지표 상세 조회", description = "지표 메타 (설명, 주기, 출처, 관련 용어)를 반환합니다.")
    @GetMapping("/{id}")
    public ApiResponse<IndicatorDetailResponse> getIndicator(
            @Parameter(description = "지표 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.ok(indicatorService.getIndicator(id));
    }

    @Operation(summary = "지표 시계열 조회", description = "지정 기간의 관측치 시계열을 반환합니다. 기본 최근 1년.")
    @GetMapping("/{id}/observations")
    public ApiResponse<List<ObservationResponse>> getObservations(
            @Parameter(description = "지표 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "시작일 (yyyy-MM-dd)", example = "2023-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "종료일 (yyyy-MM-dd)", example = "2026-04-25")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.ok(indicatorService.getObservations(id, from, to));
    }

    // TODO: 관리자 인증 적용 필요 (Spring Security 도입 이후)
    @Operation(summary = "지표 수동 재수집", description = "BOK에서 해당 지표의 최신 관측치를 다시 가져옵니다. (관리자 전용 예정)")
    @PostMapping("/{id}/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> refresh(
            @Parameter(description = "지표 ID", example = "1")
            @PathVariable Long id
    ) {
        ingestService.refresh(id);
        return ApiResponse.ok();
    }
}
