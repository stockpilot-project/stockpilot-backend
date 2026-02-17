package com.stockpilot.watchlist.controller;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.watchlist.dto.WatchlistAddStockRequest;
import com.stockpilot.watchlist.dto.WatchlistCreateRequest;
import com.stockpilot.watchlist.dto.WatchlistResponse;
import com.stockpilot.watchlist.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Watchlist", description = "관심 종목 API")
@RestController
@RequestMapping("/api/v1/watchlists")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Operation(summary = "관심 목록 생성", description = "새로운 관심 목록을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WatchlistResponse> createWatchlist(
            @Valid @RequestBody WatchlistCreateRequest request) {
        return ApiResponse.ok(watchlistService.createWatchlist(request));
    }

    @Operation(summary = "관심 목록 조회", description = "관심 목록과 종목의 현재가를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<WatchlistResponse> getWatchlist(
            @Parameter(description = "관심 목록 ID", example = "1")
            @PathVariable Long id) {
        return ApiResponse.ok(watchlistService.getWatchlist(id));
    }

    @Operation(summary = "종목 추가", description = "관심 목록에 종목을 추가합니다.")
    @PostMapping("/{id}/stocks")
    public ApiResponse<WatchlistResponse> addStock(
            @Parameter(description = "관심 목록 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody WatchlistAddStockRequest request) {
        return ApiResponse.ok(watchlistService.addStock(id, request));
    }

    @Operation(summary = "종목 제거", description = "관심 목록에서 종목을 제거합니다.")
    @DeleteMapping("/{id}/stocks/{stockId}")
    public ApiResponse<Void> removeStock(
            @Parameter(description = "관심 목록 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "종목 ID", example = "1")
            @PathVariable Long stockId) {
        watchlistService.removeStock(id, stockId);
        return ApiResponse.ok();
    }
}
