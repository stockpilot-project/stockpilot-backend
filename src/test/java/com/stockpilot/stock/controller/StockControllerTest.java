package com.stockpilot.stock.controller;

import com.stockpilot.global.error.GlobalExceptionHandler;
import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.stock.dto.SectorResponse;
import com.stockpilot.stock.dto.StockDetailResponse;
import com.stockpilot.stock.dto.StockResponse;
import com.stockpilot.stock.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @Test
    @DisplayName("GET /api/v1/stocks - 종목 목록 조회")
    void getStocks() throws Exception {
        // given
        StockResponse stock = StockResponse.builder()
                .id(1L).symbol("AAPL").name("Apple Inc.").nameKr("애플").market("US").build();
        given(stockService.getStocks("US")).willReturn(List.of(stock));

        // when & then
        mockMvc.perform(get("/api/v1/stocks").param("market", "US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].symbol").value("AAPL"));
    }

    @Test
    @DisplayName("GET /api/v1/stocks/{id} - 종목 상세 조회")
    void getStock() throws Exception {
        // given
        StockDetailResponse detail = StockDetailResponse.builder()
                .stock(StockResponse.builder().id(1L).symbol("AAPL").name("Apple").market("US").build())
                .build();
        given(stockService.getStock(1L)).willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/stocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stock.symbol").value("AAPL"));
    }

    @Test
    @DisplayName("GET /api/v1/stocks/{id} - 존재하지 않는 종목 조회 시 404")
    void getStock_notFound() throws Exception {
        // given
        given(stockService.getStock(999L))
                .willThrow(new EntityNotFoundException(ErrorCode.STOCK_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/stocks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("S001"));
    }

    @Test
    @DisplayName("GET /api/v1/stocks/starters - 초보 추천 종목 조회")
    void getStarterStocks() throws Exception {
        // given
        StockResponse stock = StockResponse.builder()
                .id(1L).symbol("AAPL").name("Apple").market("US").starterPack(true).build();
        given(stockService.getStarterStocks()).willReturn(List.of(stock));

        // when & then
        mockMvc.perform(get("/api/v1/stocks/starters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].starterPack").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/stocks/search - 종목 검색")
    void searchStocks() throws Exception {
        // given
        StockResponse stock = StockResponse.builder()
                .id(1L).symbol("005930").name("Samsung").nameKr("삼성전자").market("KR").build();
        given(stockService.searchStocks("삼성")).willReturn(List.of(stock));

        // when & then
        mockMvc.perform(get("/api/v1/stocks/search").param("q", "삼성"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nameKr").value("삼성전자"));
    }

    @Test
    @DisplayName("GET /api/v1/sectors - 섹터 목록 조회")
    void getSectors() throws Exception {
        // given
        SectorResponse sector = SectorResponse.builder()
                .id(1L).name("Technology").nameKr("기술").build();
        given(stockService.getSectors()).willReturn(List.of(sector));

        // when & then
        mockMvc.perform(get("/api/v1/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Technology"));
    }
}
