package com.stockpilot.knowledge.disclosure.dto;

import com.stockpilot.knowledge.disclosure.entity.Disclosure;
import com.stockpilot.stock.dto.StockResponse;
import com.stockpilot.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisclosureDetailResponse {

    private DisclosureResponse disclosure;
    private StockResponse stock;

    public static DisclosureDetailResponse from(Disclosure disclosure) {
        Stock stock = disclosure.getStock();
        return DisclosureDetailResponse.builder()
                .disclosure(DisclosureResponse.from(disclosure))
                .stock(stock != null ? StockResponse.from(stock) : null)
                .build();
    }
}
