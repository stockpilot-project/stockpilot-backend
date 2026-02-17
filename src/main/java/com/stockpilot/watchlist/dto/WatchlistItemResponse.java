package com.stockpilot.watchlist.dto;

import com.stockpilot.stock.dto.DailyPriceResponse;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.watchlist.entity.WatchlistItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WatchlistItemResponse {

    private Long stockId;
    private String symbol;
    private String name;
    private String nameKr;
    private String market;
    private DailyPriceResponse latestPrice;

    public static WatchlistItemResponse of(WatchlistItem item, DailyPrice latestPrice) {
        Stock stock = item.getStock();
        return WatchlistItemResponse.builder()
                .stockId(stock.getId())
                .symbol(stock.getSymbol())
                .name(stock.getName())
                .nameKr(stock.getNameKr())
                .market(stock.getMarket())
                .latestPrice(latestPrice != null ? DailyPriceResponse.from(latestPrice) : null)
                .build();
    }
}
