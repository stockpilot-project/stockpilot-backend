package com.stockpilot.watchlist.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WatchlistAddStockRequest {

    @NotNull(message = "종목 ID는 필수입니다.")
    private Long stockId;
}
