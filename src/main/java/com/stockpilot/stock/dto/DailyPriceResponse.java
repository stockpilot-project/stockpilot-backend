package com.stockpilot.stock.dto;

import com.stockpilot.stock.entity.DailyPrice;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class DailyPriceResponse {

    private Long id;
    private LocalDate tradeDate;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private BigDecimal changeRate;

    public static DailyPriceResponse from(DailyPrice dailyPrice) {
        return DailyPriceResponse.builder()
                .id(dailyPrice.getId())
                .tradeDate(dailyPrice.getTradeDate())
                .openPrice(dailyPrice.getOpenPrice())
                .highPrice(dailyPrice.getHighPrice())
                .lowPrice(dailyPrice.getLowPrice())
                .closePrice(dailyPrice.getClosePrice())
                .volume(dailyPrice.getVolume())
                .changeRate(dailyPrice.getChangeRate())
                .build();
    }
}
