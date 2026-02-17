package com.stockpilot.stock.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.stock.dto.DailyPriceResponse;
import com.stockpilot.stock.dto.SectorResponse;
import com.stockpilot.stock.dto.StockDetailResponse;
import com.stockpilot.stock.dto.StockResponse;
import com.stockpilot.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Stock", description = "종목 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "종목 목록 조회", description = "전체 종목 또는 마켓(US/KR)별 종목 목록을 조회합니다.")
    @GetMapping("/stocks")
    public ApiResponse<List<StockResponse>> getStocks(
            @Parameter(description = "마켓 필터 (US, KR)", example = "US")
            @RequestParam(required = false) String market) {
        return ApiResponse.ok(stockService.getStocks(market));
    }

    @Operation(summary = "종목 상세 조회", description = "종목 상세 정보와 최신 시세를 조회합니다.")
    @GetMapping("/stocks/{id}")
    public ApiResponse<StockDetailResponse> getStock(
            @Parameter(description = "종목 ID", example = "1")
            @PathVariable Long id) {
        return ApiResponse.ok(stockService.getStock(id));
    }

    @Operation(summary = "종목 검색", description = "종목명, 한글명, 심볼로 검색합니다.")
    @GetMapping("/stocks/search")
    public ApiResponse<List<StockResponse>> searchStocks(
            @Parameter(description = "검색어", example = "samsung")
            @RequestParam String q) {
        return ApiResponse.ok(stockService.searchStocks(q));
    }

    @Operation(summary = "초보 추천 종목", description = "투자 초보자를 위한 추천 종목 목록을 조회합니다.")
    @GetMapping("/stocks/starters")
    public ApiResponse<List<StockResponse>> getStarterStocks() {
        return ApiResponse.ok(stockService.getStarterStocks());
    }

    @Operation(summary = "기간별 시세 조회", description = "특정 종목의 기간별 일별 시세를 조회합니다.")
    @GetMapping("/stocks/{id}/prices")
    public ApiResponse<List<DailyPriceResponse>> getPrices(
            @Parameter(description = "종목 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "시작일", example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "종료일", example = "2026-02-17")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(stockService.getPrices(id, from, to));
    }

    @Tag(name = "Sector", description = "섹터 API")
    @Operation(summary = "섹터 목록 조회", description = "전체 섹터 목록을 조회합니다.")
    @GetMapping("/sectors")
    public ApiResponse<List<SectorResponse>> getSectors() {
        return ApiResponse.ok(stockService.getSectors());
    }

    @Tag(name = "Sector", description = "섹터 API")
    @Operation(summary = "섹터별 종목 조회", description = "특정 섹터에 속한 종목 목록을 조회합니다.")
    @GetMapping("/sectors/{id}/stocks")
    public ApiResponse<List<StockResponse>> getStocksBySector(
            @Parameter(description = "섹터 ID", example = "1")
            @PathVariable Long id) {
        return ApiResponse.ok(stockService.getStocksBySector(id));
    }
}
