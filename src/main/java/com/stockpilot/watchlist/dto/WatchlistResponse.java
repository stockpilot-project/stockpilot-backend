package com.stockpilot.watchlist.dto;

import com.stockpilot.watchlist.entity.Watchlist;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WatchlistResponse {

    private Long id;
    private String name;
    private String sessionId;
    private List<WatchlistItemResponse> items;
    private LocalDateTime createdAt;

    public static WatchlistResponse from(Watchlist watchlist, List<WatchlistItemResponse> items) {
        return WatchlistResponse.builder()
                .id(watchlist.getId())
                .name(watchlist.getName())
                .sessionId(watchlist.getSessionId())
                .items(items)
                .createdAt(watchlist.getCreatedAt())
                .build();
    }
}
