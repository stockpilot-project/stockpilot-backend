package com.stockpilot.stock.dto;

import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDetailResponse {

    private StockResponse stock;
    private DailyPriceResponse latestPrice;

    public static StockDetailResponse of(Stock stock, DailyPrice latestPrice) {
        return StockDetailResponse.builder()
                .stock(StockResponse.from(stock))
                .latestPrice(latestPrice != null ? DailyPriceResponse.from(latestPrice) : null)
                .build();
    }
}
