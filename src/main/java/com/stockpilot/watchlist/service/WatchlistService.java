package com.stockpilot.watchlist.service;

import com.stockpilot.global.error.exception.BusinessException;
import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.StockRepository;
import com.stockpilot.watchlist.dto.WatchlistAddStockRequest;
import com.stockpilot.watchlist.dto.WatchlistCreateRequest;
import com.stockpilot.watchlist.dto.WatchlistItemResponse;
import com.stockpilot.watchlist.dto.WatchlistResponse;
import com.stockpilot.watchlist.entity.Watchlist;
import com.stockpilot.watchlist.entity.WatchlistItem;
import com.stockpilot.watchlist.repository.WatchlistItemRepository;
import com.stockpilot.watchlist.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;

    @Transactional
    public WatchlistResponse createWatchlist(WatchlistCreateRequest request) {
        Watchlist watchlist = Watchlist.builder()
                .name(request.getName())
                .sessionId(request.getSessionId())
                .build();
        watchlistRepository.save(watchlist);
        return WatchlistResponse.from(watchlist, List.of());
    }

    public WatchlistResponse getWatchlist(Long id) {
        Watchlist watchlist = watchlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WATCHLIST_NOT_FOUND));

        List<WatchlistItemResponse> items = watchlist.getItems().stream()
                .map(item -> {
                    var latestPrice = dailyPriceRepository
                            .findFirstByStockIdOrderByTradeDateDesc(item.getStock().getId())
                            .orElse(null);
                    return WatchlistItemResponse.of(item, latestPrice);
                })
                .toList();

        return WatchlistResponse.from(watchlist, items);
    }

    @Transactional
    public WatchlistResponse addStock(Long watchlistId, WatchlistAddStockRequest request) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WATCHLIST_NOT_FOUND));

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STOCK_NOT_FOUND));

        if (watchlistItemRepository.existsByWatchlistIdAndStockId(watchlistId, request.getStockId())) {
            throw new BusinessException(ErrorCode.WATCHLIST_ITEM_DUPLICATE);
        }

        WatchlistItem item = WatchlistItem.builder()
                .watchlist(watchlist)
                .stock(stock)
                .build();
        watchlist.addItem(item);

        return getWatchlist(watchlistId);
    }

    @Transactional
    public void removeStock(Long watchlistId, Long stockId) {
        WatchlistItem item = watchlistItemRepository.findByWatchlistIdAndStockId(watchlistId, stockId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WATCHLIST_ITEM_NOT_FOUND));

        Watchlist watchlist = item.getWatchlist();
        watchlist.removeItem(item);
    }
}
