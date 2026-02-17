package com.stockpilot.watchlist.repository;

import com.stockpilot.watchlist.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findBySessionId(String sessionId);
}
