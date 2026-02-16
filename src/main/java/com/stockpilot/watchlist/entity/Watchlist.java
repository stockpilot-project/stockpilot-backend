package com.stockpilot.watchlist.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watchlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Watchlist extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistItem> items = new ArrayList<>();

    @Builder
    public Watchlist(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public void addItem(WatchlistItem item) {
        this.items.add(item);
    }

    public void removeItem(WatchlistItem item) {
        this.items.remove(item);
    }
}
