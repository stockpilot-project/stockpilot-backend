package com.stockpilot.stock.dto;

import com.stockpilot.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockResponse {

    private Long id;
    private String symbol;
    private String name;
    private String nameKr;
    private String market;
    private boolean starterPack;
    private String description;
    private SectorResponse sector;

    public static StockResponse from(Stock stock) {
        return StockResponse.builder()
                .id(stock.getId())
                .symbol(stock.getSymbol())
                .name(stock.getName())
                .nameKr(stock.getNameKr())
                .market(stock.getMarket())
                .starterPack(stock.isStarterPack())
                .description(stock.getDescription())
                .sector(stock.getSector() != null ? SectorResponse.from(stock.getSector()) : null)
                .build();
    }
}
