package com.stockpilot.market.dto;

import com.stockpilot.stock.entity.DailyPrice;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MarketStockResponse {

    private Long stockId;
    private String symbol;
    private String name;
    private String nameKr;
    private String market;
    private LocalDate tradeDate;
    private BigDecimal closePrice;
    private BigDecimal changeRate;
    private Long volume;

    public static MarketStockResponse from(DailyPrice dailyPrice) {
        return MarketStockResponse.builder()
                .stockId(dailyPrice.getStock().getId())
                .symbol(dailyPrice.getStock().getSymbol())
                .name(dailyPrice.getStock().getName())
                .nameKr(dailyPrice.getStock().getNameKr())
                .market(dailyPrice.getStock().getMarket())
                .tradeDate(dailyPrice.getTradeDate())
                .closePrice(dailyPrice.getClosePrice())
                .changeRate(dailyPrice.getChangeRate())
                .volume(dailyPrice.getVolume())
                .build();
    }
}
