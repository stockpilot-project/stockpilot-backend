package com.stockpilot.watchlist.service;

import com.stockpilot.global.error.exception.BusinessException;
import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.StockRepository;
import com.stockpilot.watchlist.dto.WatchlistAddStockRequest;
import com.stockpilot.watchlist.dto.WatchlistCreateRequest;
import com.stockpilot.watchlist.dto.WatchlistResponse;
import com.stockpilot.watchlist.entity.Watchlist;
import com.stockpilot.watchlist.entity.WatchlistItem;
import com.stockpilot.watchlist.repository.WatchlistItemRepository;
import com.stockpilot.watchlist.repository.WatchlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @InjectMocks
    private WatchlistService watchlistService;

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private WatchlistItemRepository watchlistItemRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private DailyPriceRepository dailyPriceRepository;

    @Test
    @DisplayName("관심 목록을 생성한다")
    void createWatchlist() {
        // given
        WatchlistCreateRequest request = new WatchlistCreateRequest();
        ReflectionTestUtils.setField(request, "name", "My Watchlist");
        ReflectionTestUtils.setField(request, "sessionId", "session-123");

        Watchlist watchlist = Watchlist.builder().name("My Watchlist").sessionId("session-123").build();
        ReflectionTestUtils.setField(watchlist, "id", 1L);
        given(watchlistRepository.save(any(Watchlist.class))).willReturn(watchlist);

        // when
        WatchlistResponse result = watchlistService.createWatchlist(request);

        // then
        assertThat(result.getName()).isEqualTo("My Watchlist");
        assertThat(result.getSessionId()).isEqualTo("session-123");
    }

    @Test
    @DisplayName("존재하지 않는 관심 목록 조회 시 예외가 발생한다")
    void getWatchlist_notFound() {
        // given
        given(watchlistRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> watchlistService.getWatchlist(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("이미 추가된 종목을 다시 추가하면 예외가 발생한다")
    void addStock_duplicate() {
        // given
        Watchlist watchlist = Watchlist.builder().name("Test").sessionId("s1").build();
        ReflectionTestUtils.setField(watchlist, "id", 1L);

        Stock stock = Stock.builder().symbol("AAPL").name("Apple").market("US").build();
        ReflectionTestUtils.setField(stock, "id", 1L);

        WatchlistAddStockRequest request = new WatchlistAddStockRequest();
        ReflectionTestUtils.setField(request, "stockId", 1L);

        given(watchlistRepository.findById(1L)).willReturn(Optional.of(watchlist));
        given(stockRepository.findById(1L)).willReturn(Optional.of(stock));
        given(watchlistItemRepository.existsByWatchlistIdAndStockId(1L, 1L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> watchlistService.addStock(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("관심 목록에서 종목을 제거한다")
    void removeStock() {
        // given
        Watchlist watchlist = Watchlist.builder().name("Test").sessionId("s1").build();
        ReflectionTestUtils.setField(watchlist, "items", new ArrayList<>());
        Stock stock = Stock.builder().symbol("AAPL").name("Apple").market("US").build();
        WatchlistItem item = WatchlistItem.builder().watchlist(watchlist).stock(stock).build();
        watchlist.addItem(item);

        given(watchlistItemRepository.findByWatchlistIdAndStockId(1L, 1L))
                .willReturn(Optional.of(item));

        // when
        watchlistService.removeStock(1L, 1L);

        // then
        assertThat(watchlist.getItems()).isEmpty();
    }
}
