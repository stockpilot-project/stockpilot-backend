package com.stockpilot.stock.service;

import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.stock.dto.DailyPriceResponse;
import com.stockpilot.stock.dto.StockDetailResponse;
import com.stockpilot.stock.dto.StockResponse;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Sector;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.SectorRepository;
import com.stockpilot.stock.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private DailyPriceRepository dailyPriceRepository;

    @Test
    @DisplayName("마켓별 종목 목록을 조회한다")
    void getStocks_byMarket() {
        // given
        Stock stock = Stock.builder()
                .symbol("AAPL").name("Apple Inc.").nameKr("애플").market("US").build();
        given(stockRepository.findByMarket("US")).willReturn(List.of(stock));

        // when
        List<StockResponse> result = stockService.getStocks("US");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSymbol()).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("전체 종목 목록을 조회한다")
    void getStocks_all() {
        // given
        Stock stock1 = Stock.builder().symbol("AAPL").name("Apple").market("US").build();
        Stock stock2 = Stock.builder().symbol("005930").name("Samsung").market("KR").build();
        given(stockRepository.findAll()).willReturn(List.of(stock1, stock2));

        // when
        List<StockResponse> result = stockService.getStocks(null);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("종목 상세 조회 시 최신 시세를 포함한다")
    void getStock_withLatestPrice() {
        // given
        Stock stock = Stock.builder().symbol("AAPL").name("Apple").market("US").build();
        DailyPrice price = DailyPrice.builder()
                .stock(stock).tradeDate(LocalDate.now())
                .openPrice(BigDecimal.valueOf(150)).highPrice(BigDecimal.valueOf(155))
                .lowPrice(BigDecimal.valueOf(149)).closePrice(BigDecimal.valueOf(153))
                .volume(1000000L).changeRate(BigDecimal.valueOf(2.5))
                .build();

        given(stockRepository.findById(1L)).willReturn(Optional.of(stock));
        given(dailyPriceRepository.findFirstByStockIdOrderByTradeDateDesc(1L))
                .willReturn(Optional.of(price));

        // when
        StockDetailResponse result = stockService.getStock(1L);

        // then
        assertThat(result.getStock().getSymbol()).isEqualTo("AAPL");
        assertThat(result.getLatestPrice()).isNotNull();
        assertThat(result.getLatestPrice().getClosePrice()).isEqualByComparingTo(BigDecimal.valueOf(153));
    }

    @Test
    @DisplayName("존재하지 않는 종목 조회 시 예외가 발생한다")
    void getStock_notFound() {
        // given
        given(stockRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stockService.getStock(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("초보 추천 종목을 조회한다")
    void getStarterStocks() {
        // given
        Stock stock = Stock.builder()
                .symbol("AAPL").name("Apple").market("US").starterPack(true).build();
        given(stockRepository.findByStarterPackTrue()).willReturn(List.of(stock));

        // when
        List<StockResponse> result = stockService.getStarterStocks();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isStarterPack()).isTrue();
    }

    @Test
    @DisplayName("기간별 시세를 조회한다")
    void getPrices() {
        // given
        Stock stock = Stock.builder().symbol("AAPL").name("Apple").market("US").build();
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 17);

        DailyPrice price = DailyPrice.builder()
                .stock(stock).tradeDate(from)
                .openPrice(BigDecimal.valueOf(150)).highPrice(BigDecimal.valueOf(155))
                .lowPrice(BigDecimal.valueOf(149)).closePrice(BigDecimal.valueOf(153))
                .volume(1000000L).changeRate(BigDecimal.valueOf(2.5))
                .build();

        given(stockRepository.existsById(1L)).willReturn(true);
        given(dailyPriceRepository.findByStockIdAndTradeDateBetweenOrderByTradeDateDesc(1L, from, to))
                .willReturn(List.of(price));

        // when
        List<DailyPriceResponse> result = stockService.getPrices(1L, from, to);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("종목 검색을 수행한다")
    void searchStocks() {
        // given
        Stock stock = Stock.builder().symbol("005930").name("Samsung Electronics").nameKr("삼성전자").market("KR").build();
        given(stockRepository.findByNameContainingIgnoreCaseOrNameKrContainingIgnoreCaseOrSymbolContainingIgnoreCase(
                "삼성", "삼성", "삼성")).willReturn(List.of(stock));

        // when
        List<StockResponse> result = stockService.searchStocks("삼성");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNameKr()).isEqualTo("삼성전자");
    }
}
