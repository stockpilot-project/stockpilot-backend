package com.stockpilot.market.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.market.dto.MarketStockResponse;
import com.stockpilot.market.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Market", description = "시장 데이터 API")
@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;

    @Operation(summary = "급등 종목", description = "변동률 상위 종목을 조회합니다.")
    @GetMapping("/gainers")
    public ApiResponse<List<MarketStockResponse>> getGainers(
            @Parameter(description = "마켓 필터 (US, KR)", example = "US")
            @RequestParam(required = false) String market) {
        return ApiResponse.ok(marketDataService.getGainers(market));
    }

    @Operation(summary = "급락 종목", description = "변동률 하위 종목을 조회합니다.")
    @GetMapping("/losers")
    public ApiResponse<List<MarketStockResponse>> getLosers(
            @Parameter(description = "마켓 필터 (US, KR)", example = "US")
            @RequestParam(required = false) String market) {
        return ApiResponse.ok(marketDataService.getLosers(market));
    }

    @Operation(summary = "거래량 상위 (트렌딩)", description = "거래량 상위 종목을 조회합니다.")
    @GetMapping("/trending")
    public ApiResponse<List<MarketStockResponse>> getTrending() {
        return ApiResponse.ok(marketDataService.getTrending());
    }
}
