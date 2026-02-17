package com.stockpilot.watchlist.repository;

import com.stockpilot.watchlist.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    Optional<WatchlistItem> findByWatchlistIdAndStockId(Long watchlistId, Long stockId);

    boolean existsByWatchlistIdAndStockId(Long watchlistId, Long stockId);
}
